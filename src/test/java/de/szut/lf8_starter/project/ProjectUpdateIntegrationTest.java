package de.szut.lf8_starter.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import de.szut.lf8_starter.AbstractIntegrationTest;
import de.szut.lf8_starter.employee.EmployeeDto;
import de.szut.lf8_starter.employee.EmployeeServiceUnavailableException;
import de.szut.lf8_starter.project.domain.ProjectEntity;
import de.szut.lf8_starter.project.persistence.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/**
 * HTTP-level integration tests for PUT /projects/{id}.
 */
class ProjectUpdateIntegrationTest extends AbstractIntegrationTest {

    private static final String PROJECT_URL = "/projects/{id}";
    private static final String SWAGGER_API_DOCS_URL = "/v3/api-docs";
    private static final String PROJECT_UPDATE_OPENAPI_PATH = "$.paths['/projects/{id}'].put.responses";
    private static final String PROJECT_NOT_FOUND_DETAIL = "Project with ID %d not found";
    private static final String PROJECT_DESIGNATION = "Project updated";
    private static final String ORIGINAL_PROJECT_DESIGNATION = "Project original";
    private static final String INVALID_DATE_FORMAT = "31.12.2026";
    private static final String PROJECT_START_DATE = "2026-11-01";
    private static final String PROJECT_END_DATE = "2026-12-31";
    private static final String UPDATED_START_DATE = "2026-11-05";
    private static final String UPDATED_END_DATE = "2027-01-15";
    private static final long RESPONSIBLE_EMPLOYEE_ID = 1L;
    private static final long CHANGED_RESPONSIBLE_EMPLOYEE_ID = 2L;
    private static final long CUSTOMER_ID = 20L;
    private static final long UNKNOWN_PROJECT_ID = 999_999L;
    private static final long FIRST_QUALIFICATION_ID = 3L;
    private static final long SECOND_QUALIFICATION_ID = 4L;
    private static final LocalDate ACTUAL_END_DATE = LocalDate.of(2026, 12, 30);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    /**
     * Verifies that a valid request updates project data and preserves the ID and actual end date.
     */
    @Test
    void update_validRequest_returns200AndPreservesProjectIdentity() throws Exception {
        // Arrange
        ProjectEntity project = saveProject();
        when(employeeClient.findById(CHANGED_RESPONSIBLE_EMPLOYEE_ID))
                .thenReturn(Optional.of(new EmployeeDto(
                        CHANGED_RESPONSIBLE_EMPLOYEE_ID,
                        "Ada",
                        "Lovelace",
                        List.of())));
        String request = updateRequest(
                PROJECT_DESIGNATION,
                CHANGED_RESPONSIBLE_EMPLOYEE_ID,
                UPDATED_START_DATE,
                UPDATED_END_DATE);
        // Act
        ResultActions result = putProject(project.getId(), request);
        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(project.getId()))
                .andExpect(jsonPath("$.designation").value(PROJECT_DESIGNATION))
                .andExpect(jsonPath("$.responsibleEmployeeId").value(CHANGED_RESPONSIBLE_EMPLOYEE_ID))
                .andExpect(jsonPath("$.customerId").value(CUSTOMER_ID))
                .andExpect(jsonPath("$.startDate").value(UPDATED_START_DATE))
                .andExpect(jsonPath("$.plannedEndDate").value(UPDATED_END_DATE))
                .andExpect(jsonPath("$.actualEndDate").value(ACTUAL_END_DATE.toString()))
                .andExpect(jsonPath(
                        "$.requiredQualificationIds",
                        containsInAnyOrder(3, 4)));
        ProjectEntity updatedProject = projectRepository.findById(project.getId()).orElseThrow();
        assertThat(updatedProject.getId()).isEqualTo(project.getId());
        assertThat(updatedProject.getActualEndDate()).isEqualTo(ACTUAL_END_DATE);
        verify(employeeClient).findById(CHANGED_RESPONSIBLE_EMPLOYEE_ID);
    }

    /**
     * Verifies that updating an unknown project returns HTTP 404 with its ID in the problem detail.
     */
    @Test
    void update_unknownProject_returns404WithProjectId() throws Exception {
        // Arrange
        String request = validRequest();
        // Act
        ResultActions result = putProject(UNKNOWN_PROJECT_ID, request);
        // Assert
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value(PROJECT_NOT_FOUND_DETAIL.formatted(UNKNOWN_PROJECT_ID)))
                .andExpect(jsonPath("$.projectId").value(UNKNOWN_PROJECT_ID));
    }

    /**
     * Verifies that a missing required field returns HTTP 400.
     */
    @Test
    void update_missingDesignation_returns400() throws Exception {
        // Arrange
        ProjectEntity project = saveProject();
        String request = """
                {
                  "responsibleEmployeeId":%d,
                  "customerId":%d,
                  "startDate":"%s",
                  "plannedEndDate":"%s"
                }
                """.formatted(
                RESPONSIBLE_EMPLOYEE_ID,
                CUSTOMER_ID,
                PROJECT_START_DATE,
                PROJECT_END_DATE);
        // Act
        ResultActions result = putProject(project.getId(), request);
        // Assert
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.designation").exists());
    }

    /**
     * Verifies that a date with an invalid format returns HTTP 400.
     */
    @Test
    void update_invalidDateFormat_returns400() throws Exception {
        // Arrange
        ProjectEntity project = saveProject();
        String request = updateRequest(
                PROJECT_DESIGNATION,
                RESPONSIBLE_EMPLOYEE_ID,
                INVALID_DATE_FORMAT,
                PROJECT_END_DATE);
        // Act
        ResultActions result = putProject(project.getId(), request);
        // Assert
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", containsString("yyyy-MM-dd")));
    }

    /**
     * Verifies that an invalid project period returns HTTP 422 without changing the project.
     */
    @Test
    void update_startAfterEnd_returns422AndLeavesProjectUnchanged() throws Exception {
        // Arrange
        ProjectEntity project = saveProject();
        String request = updateRequest(
                PROJECT_DESIGNATION,
                RESPONSIBLE_EMPLOYEE_ID,
                PROJECT_END_DATE,
                PROJECT_START_DATE);
        // Act
        ResultActions result = putProject(project.getId(), request);
        // Assert
        result.andExpect(status().isUnprocessableContent());
        assertProjectUnchanged(project.getId());
        verify(employeeClient, never()).findById(RESPONSIBLE_EMPLOYEE_ID);
    }

    /**
     * Verifies that an unknown changed responsible employee returns HTTP 422 without changing the project.
     */
    @Test
    void update_unknownResponsibleEmployee_returns422AndLeavesProjectUnchanged() throws Exception {
        // Arrange
        ProjectEntity project = saveProject();
        when(employeeClient.findById(CHANGED_RESPONSIBLE_EMPLOYEE_ID)).thenReturn(Optional.empty());
        String request = updateRequest(
                PROJECT_DESIGNATION,
                CHANGED_RESPONSIBLE_EMPLOYEE_ID,
                PROJECT_START_DATE,
                PROJECT_END_DATE);
        // Act
        ResultActions result = putProject(project.getId(), request);
        // Assert
        result.andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.detail", containsString(Long.toString(CHANGED_RESPONSIBLE_EMPLOYEE_ID))));
        assertProjectUnchanged(project.getId());
    }

    /**
     * Verifies that an unavailable employee service returns HTTP 503 without changing the project.
     */
    @Test
    void update_employeeServiceUnavailable_returns503AndLeavesProjectUnchanged() throws Exception {
        // Arrange
        ProjectEntity project = saveProject();
        when(employeeClient.findById(CHANGED_RESPONSIBLE_EMPLOYEE_ID))
                .thenThrow(new EmployeeServiceUnavailableException(new RuntimeException("connection refused")));
        String request = updateRequest(
                PROJECT_DESIGNATION,
                CHANGED_RESPONSIBLE_EMPLOYEE_ID,
                PROJECT_START_DATE,
                PROJECT_END_DATE);
        // Act
        ResultActions result = putProject(project.getId(), request);
        // Assert
        result.andExpect(status().isServiceUnavailable());
        assertProjectUnchanged(project.getId());
    }

    /**
     * Verifies that an unchanged responsible employee does not require an employee-service lookup.
     */
    @Test
    void update_unchangedResponsibleEmployee_doesNotRequireEmployeeLookup() throws Exception {
        // Arrange
        ProjectEntity project = saveProject();
        String request = updateRequest(
                PROJECT_DESIGNATION,
                RESPONSIBLE_EMPLOYEE_ID,
                UPDATED_START_DATE,
                UPDATED_END_DATE);
        // Act
        ResultActions result = putProject(project.getId(), request);
        // Assert
        result.andExpect(status().isOk());
        verify(employeeClient, never()).findById(RESPONSIBLE_EMPLOYEE_ID);
    }

    /**
     * Verifies that an unauthenticated update request returns HTTP 401.
     */
    @Test
    void update_withoutToken_returns401() throws Exception {
        // Arrange
        ProjectEntity project = saveProject();
        String request = validRequest();
        // Act
        ResultActions result = mockMvc.perform(put(PROJECT_URL, project.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));
        // Assert
        result.andExpect(status().isUnauthorized());
    }

    /**
     * Verifies that the OpenAPI document lists all documented update response codes.
     */
    @Test
    void openApiDocs_updateEndpoint_documentsStatusCodes() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(get(SWAGGER_API_DOCS_URL)
                .with(jwt())
                .accept(MediaType.APPLICATION_JSON));
        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath(PROJECT_UPDATE_OPENAPI_PATH + "['200']").exists())
                .andExpect(jsonPath(PROJECT_UPDATE_OPENAPI_PATH + "['400']").exists())
                .andExpect(jsonPath(PROJECT_UPDATE_OPENAPI_PATH + "['401']").exists())
                .andExpect(jsonPath(PROJECT_UPDATE_OPENAPI_PATH + "['404']").exists())
                .andExpect(jsonPath(PROJECT_UPDATE_OPENAPI_PATH + "['422']").exists())
                .andExpect(jsonPath(PROJECT_UPDATE_OPENAPI_PATH + "['503']").exists());
    }

    private ResultActions putProject(final Long projectId, final String request) throws Exception {
        return mockMvc.perform(put(PROJECT_URL, projectId)
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .accept(MediaType.APPLICATION_JSON));
    }

    private ProjectEntity saveProject() {
        ProjectEntity project = new ProjectEntity();
        project.setDesignation(ORIGINAL_PROJECT_DESIGNATION);
        project.setResponsibleEmployeeId(RESPONSIBLE_EMPLOYEE_ID);
        project.setCustomerId(10L);
        project.setStartDate(LocalDate.parse(PROJECT_START_DATE));
        project.setPlannedEndDate(LocalDate.parse(PROJECT_END_DATE));
        project.setActualEndDate(ACTUAL_END_DATE);
        project.setRequiredQualificationIds(Set.of(FIRST_QUALIFICATION_ID));
        return projectRepository.saveAndFlush(project);
    }

    private void assertProjectUnchanged(final Long projectId) {
        ProjectEntity unchangedProject = projectRepository.findById(projectId).orElseThrow();
        assertThat(unchangedProject.getDesignation()).isEqualTo(ORIGINAL_PROJECT_DESIGNATION);
        assertThat(unchangedProject.getResponsibleEmployeeId()).isEqualTo(RESPONSIBLE_EMPLOYEE_ID);
        assertThat(unchangedProject.getRequiredQualificationIds()).containsExactly(FIRST_QUALIFICATION_ID);
    }

    private static String validRequest() {
        return updateRequest(
                PROJECT_DESIGNATION,
                RESPONSIBLE_EMPLOYEE_ID,
                PROJECT_START_DATE,
                PROJECT_END_DATE);
    }

    private static String updateRequest(
            final String designation,
            final long responsibleEmployeeId,
            final String startDate,
            final String plannedEndDate) {
        return """
                {
                  "designation":"%s",
                  "responsibleEmployeeId":%d,
                  "customerId":%d,
                  "customerContactName":"Updated contact",
                  "goalComment":"Updated goal",
                  "startDate":"%s",
                  "plannedEndDate":"%s",
                  "requiredQualificationIds":[%d,%d]
                }
                """.formatted(
                designation,
                responsibleEmployeeId,
                CUSTOMER_ID,
                startDate,
                plannedEndDate,
                FIRST_QUALIFICATION_ID,
                SECOND_QUALIFICATION_ID);
    }
}
