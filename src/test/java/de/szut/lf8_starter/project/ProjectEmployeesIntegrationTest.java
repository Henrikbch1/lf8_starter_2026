package de.szut.lf8_starter.project;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.szut.lf8_starter.AbstractIntegrationTest;
import de.szut.lf8_starter.project.domain.ProjectEmployeeAssignment;
import de.szut.lf8_starter.project.domain.ProjectEntity;
import de.szut.lf8_starter.project.persistence.ProjectEmployeeAssignmentRepository;
import de.szut.lf8_starter.project.persistence.ProjectRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/**
 * HTTP-level integration tests for GET /projects/{id}/employees using the real security filter chain
 * and the Testcontainers PostgreSQL database.
 */
class ProjectEmployeesIntegrationTest extends AbstractIntegrationTest {

    private static final String PROJECT_EMPLOYEES_URL = "/projects/{id}/employees";
    private static final String SWAGGER_API_DOCS_URL = "/v3/api-docs";
    private static final String PROJECT_EMPLOYEES_OPENAPI_PATH = "$.paths['/projects/{id}/employees'].get.responses";
    private static final String PROJECT_DESIGNATION = "Project Alpha";
    private static final String OTHER_PROJECT_DESIGNATION = "Project Beta";
    private static final String PROJECT_NOT_FOUND_DETAIL = "Project with ID %d not found";
    private static final String INVALID_TOKEN = "invalid-token";
    private static final long FIRST_EMPLOYEE_ID = 101L;
    private static final long SECOND_EMPLOYEE_ID = 102L;
    private static final long OTHER_PROJECT_EMPLOYEE_ID = 103L;
    private static final long FIRST_QUALIFICATION_ID = 30L;
    private static final long SECOND_QUALIFICATION_ID = 40L;
    private static final long OTHER_PROJECT_QUALIFICATION_ID = 50L;
    private static final long RESPONSIBLE_EMPLOYEE_ID = 1L;
    private static final long CUSTOMER_ID = 1L;
    private static final LocalDate PROJECT_START_DATE = LocalDate.of(2026, 10, 1);
    private static final LocalDate PROJECT_PLANNED_END_DATE = LocalDate.of(2026, 12, 31);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectEmployeeAssignmentRepository projectEmployeeAssignmentRepository;

    /**
     * Verifies that a project with two assignments returns HTTP 200 with exactly these two employee
     * IDs and roles, and that employees of another project are not included.
     */
    @Test
    void findProjectEmployees_twoAssignments_returns200WithExactEmployeeIdsAndRoles() throws Exception {
        // Arrange
        ProjectEntity project = saveProject(PROJECT_DESIGNATION);
        ProjectEntity otherProject = saveProject(OTHER_PROJECT_DESIGNATION);
        saveAssignment(project, FIRST_EMPLOYEE_ID, FIRST_QUALIFICATION_ID);
        saveAssignment(project, SECOND_EMPLOYEE_ID, SECOND_QUALIFICATION_ID);
        saveAssignment(otherProject, OTHER_PROJECT_EMPLOYEE_ID, OTHER_PROJECT_QUALIFICATION_ID);
        String expectedBody = """
                {
                  "projectId": %d,
                  "designation": "%s",
                  "employees": [
                    {"employeeId": %d, "role": %d},
                    {"employeeId": %d, "role": %d}
                  ]
                }
                """.formatted(
                project.getId(),
                PROJECT_DESIGNATION,
                FIRST_EMPLOYEE_ID,
                FIRST_QUALIFICATION_ID,
                SECOND_EMPLOYEE_ID,
                SECOND_QUALIFICATION_ID);
        // Act
        ResultActions result = mockMvc.perform(get(PROJECT_EMPLOYEES_URL, project.getId())
                .with(jwt())
                .accept(MediaType.APPLICATION_JSON));
        // Assert
        result.andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedBody))
                .andExpect(jsonPath("$.employees.length()").value(2));
    }

    /**
     * Verifies that a project without assignments returns HTTP 200 with an empty employee list.
     */
    @Test
    void findProjectEmployees_noAssignments_returns200WithEmptyEmployeeList() throws Exception {
        // Arrange
        ProjectEntity project = saveProject(PROJECT_DESIGNATION);
        // Act
        ResultActions result = mockMvc.perform(get(PROJECT_EMPLOYEES_URL, project.getId())
                .with(jwt())
                .accept(MediaType.APPLICATION_JSON));
        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value(project.getId()))
                .andExpect(jsonPath("$.designation").value(PROJECT_DESIGNATION))
                .andExpect(jsonPath("$.employees").isArray())
                .andExpect(jsonPath("$.employees").isEmpty());
    }

    /**
     * Verifies that a missing project returns HTTP 404 and a problem detail that names the requested
     * project ID.
     */
    @Test
    void findProjectEmployees_unknownProject_returns404WithProjectIdInDetail() throws Exception {
        // Arrange
        ProjectEntity deletedProject = saveProject(PROJECT_DESIGNATION);
        Long missingProjectId = deletedProject.getId();
        projectRepository.delete(deletedProject);
        projectRepository.flush();
        // Act
        ResultActions result = mockMvc.perform(get(PROJECT_EMPLOYEES_URL, missingProjectId)
                .with(jwt())
                .accept(MediaType.APPLICATION_JSON));
        // Assert
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value(PROJECT_NOT_FOUND_DETAIL.formatted(missingProjectId)))
                .andExpect(jsonPath("$.projectId").value(missingProjectId));
    }

    /**
     * Verifies that a request without a token is rejected with HTTP 401 before the project is looked up.
     */
    @Test
    void findProjectEmployees_withoutToken_returns401() throws Exception {
        // Arrange
        ProjectEntity project = saveProject(PROJECT_DESIGNATION);
        // Act
        ResultActions result = mockMvc.perform(get(PROJECT_EMPLOYEES_URL, project.getId())
                .accept(MediaType.APPLICATION_JSON));
        // Assert
        result.andExpect(status().isUnauthorized());
    }

    /**
     * Verifies that a malformed bearer token is rejected with HTTP 401.
     */
    @Test
    void findProjectEmployees_withInvalidToken_returns401() throws Exception {
        // Arrange
        ProjectEntity project = saveProject(PROJECT_DESIGNATION);
        // Act
        ResultActions result = mockMvc.perform(get(PROJECT_EMPLOYEES_URL, project.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + INVALID_TOKEN)
                .accept(MediaType.APPLICATION_JSON));
        // Assert
        result.andExpect(status().isUnauthorized());
    }

    /**
     * Verifies that the OpenAPI document describes the endpoint with the status codes 200, 401 and 404.
     */
    @Test
    void openApiDocs_projectEmployeesEndpoint_documentsStatusCodes200_401_404() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(get(SWAGGER_API_DOCS_URL)
                .with(jwt())
                .accept(MediaType.APPLICATION_JSON));
        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath(PROJECT_EMPLOYEES_OPENAPI_PATH + "['200']").exists())
                .andExpect(jsonPath(PROJECT_EMPLOYEES_OPENAPI_PATH + "['401']").exists())
                .andExpect(jsonPath(PROJECT_EMPLOYEES_OPENAPI_PATH + "['404']").exists());
    }

    private ProjectEntity saveProject(final String designation) {
        ProjectEntity project = new ProjectEntity();
        project.setDesignation(designation);
        project.setResponsibleEmployeeId(RESPONSIBLE_EMPLOYEE_ID);
        project.setCustomerId(CUSTOMER_ID);
        project.setStartDate(PROJECT_START_DATE);
        project.setPlannedEndDate(PROJECT_PLANNED_END_DATE);
        return projectRepository.saveAndFlush(project);
    }

    private void saveAssignment(final ProjectEntity project, final long employeeId, final long qualificationId) {
        projectEmployeeAssignmentRepository.saveAndFlush(
                new ProjectEmployeeAssignment(project, employeeId, qualificationId));
    }
}
