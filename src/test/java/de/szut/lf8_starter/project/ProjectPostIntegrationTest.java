package de.szut.lf8_starter.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.szut.lf8_starter.AbstractIntegrationTest;
import de.szut.lf8_starter.common.ApiExceptionHandler;
import de.szut.lf8_starter.employee.EmployeeDto;
import de.szut.lf8_starter.employee.EmployeeServiceUnavailableException;
import de.szut.lf8_starter.project.persistence.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class ProjectPostIntegrationTest extends AbstractIntegrationTest {

    private static final long RESPONSIBLE_EMPLOYEE_ID = 1L;
    private static final long CUSTOMER_ID = 10L;
    private static final long UNKNOWN_EMPLOYEE_ID = 99L;
    private static final int FIRST_REQUIRED_QUALIFICATION_ID = 1;
    private static final int SECOND_REQUIRED_QUALIFICATION_ID = 2;

    private static final String PROJECTS_ENDPOINT = "/projects";
    private static final String PROJECT_DESIGNATION = "Projekt Alpha";
    private static final String RESPONSIBLE_EMPLOYEE_FIRST_NAME = "Max";
    private static final String RESPONSIBLE_EMPLOYEE_LAST_NAME = "Mustermann";
    private static final String CUSTOMER_CONTACT_NAME = "Erika Muster";
    private static final String PROJECT_GOAL_COMMENT = "Testziel";
    private static final String PROJECT_START_DATE = "2026-11-01";
    private static final String PROJECT_PLANNED_END_DATE = "2026-12-31";
    private static final String INVALID_PROJECT_START_DATE = "31.12.2026";
    private static final String EXPECTED_DATE_FORMAT = "yyyy-MM-dd";
    private static final String PROJECT_VALIDATION_ERROR = "project must be valid";
    private static final String PROJECT_CREATE_DTO = "projectCreateDto";
    private static final String INVALID_INPUT_DETAIL = "Ungültige Eingabe";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    @BeforeEach
    void employeeOneExists() {
        when(employeeClient.findById(RESPONSIBLE_EMPLOYEE_ID))
                .thenReturn(Optional.of(new EmployeeDto(
                        RESPONSIBLE_EMPLOYEE_ID,
                        RESPONSIBLE_EMPLOYEE_FIRST_NAME,
                        RESPONSIBLE_EMPLOYEE_LAST_NAME,
                        List.of())));
    }

    @Nested
    class SuccessfulCreation {

        @Test
        void create_validRequest_returns201() throws Exception {
            // Arrange
            String request = """
                    {
                      "designation":"%s",
                      "responsibleEmployeeId":%d,
                      "customerId":%d,
                      "customerContactName":"%s",
                      "goalComment":"%s",
                      "startDate":"%s",
                      "plannedEndDate":"%s",
                      "requiredQualificationIds":[%d,%d]
                    }
                    """.formatted(
                    PROJECT_DESIGNATION,
                    RESPONSIBLE_EMPLOYEE_ID,
                    CUSTOMER_ID,
                    CUSTOMER_CONTACT_NAME,
                    PROJECT_GOAL_COMMENT,
                    PROJECT_START_DATE,
                    PROJECT_PLANNED_END_DATE,
                    FIRST_REQUIRED_QUALIFICATION_ID,
                    SECOND_REQUIRED_QUALIFICATION_ID);
            // Act
            ResultActions result = postProject(request);
            // Assert
            result
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.designation").value(PROJECT_DESIGNATION))
                    .andExpect(jsonPath("$.responsibleEmployeeId").value(RESPONSIBLE_EMPLOYEE_ID))
                    .andExpect(jsonPath("$.customerId").value(CUSTOMER_ID))
                    .andExpect(jsonPath("$.customerContactName").value(CUSTOMER_CONTACT_NAME))
                    .andExpect(jsonPath("$.goalComment").value(PROJECT_GOAL_COMMENT))
                    .andExpect(jsonPath("$.startDate").value(PROJECT_START_DATE))
                    .andExpect(jsonPath("$.plannedEndDate").value(PROJECT_PLANNED_END_DATE))
                    .andExpect(jsonPath(
                            "$.requiredQualificationIds",
                            containsInAnyOrder(
                                    FIRST_REQUIRED_QUALIFICATION_ID,
                                    SECOND_REQUIRED_QUALIFICATION_ID)));
        }

        @Test
        void create_withoutRequiredQualifications_returnsEmptyList() throws Exception {
            // Arrange
            String request = validRequest();
            // Act
            ResultActions result = postProject(request);
            // Assert
            result
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.requiredQualificationIds").isEmpty());
        }
    }

    @Nested
    class Validation {

        @Test
        void validation_globalError_includesObjectErrorInProblemDetail() {
            // Arrange
            BindingResult bindingResult = mock(BindingResult.class);
            MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
            ObjectError globalError = new ObjectError(PROJECT_CREATE_DTO, PROJECT_VALIDATION_ERROR);
            when(exception.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(List.of());
            when(bindingResult.getGlobalErrors()).thenReturn(List.of(globalError));
            ApiExceptionHandler handler = new ApiExceptionHandler();
            // Act
            ProblemDetail result = handler.validation(exception);
            // Assert
            assertThat(result.getStatus()).isEqualTo(400);
            assertThat(result.getDetail()).isEqualTo(INVALID_INPUT_DETAIL);
            assertThat(result.getProperties())
                    .containsEntry("errors", Map.of(PROJECT_CREATE_DTO, PROJECT_VALIDATION_ERROR));
        }

        @Test
        void create_withoutDesignation_returns400() throws Exception {
            // Arrange
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
                    PROJECT_PLANNED_END_DATE);
            // Act
            ResultActions result = postProject(request);
            // Assert
            result
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.designation").exists());
        }

        @Test
        void create_invalidDateFormat_returns400() throws Exception {
            // Arrange
            String request = projectRequest(
                    PROJECT_DESIGNATION,
                    RESPONSIBLE_EMPLOYEE_ID,
                    INVALID_PROJECT_START_DATE,
                    PROJECT_PLANNED_END_DATE);
            // Act
            ResultActions result = postProject(request);
            // Assert
            result
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.detail", containsString(EXPECTED_DATE_FORMAT)));
        }

        @Test
        void create_startAfterEnd_returns422() throws Exception {
            // Arrange
            String request = projectRequest(
                    PROJECT_DESIGNATION,
                    RESPONSIBLE_EMPLOYEE_ID,
                    PROJECT_PLANNED_END_DATE,
                    PROJECT_START_DATE);
            // Act
            ResultActions result = postProject(request);
            // Assert
            result.andExpect(status().isUnprocessableContent());
        }
    }

    @Nested
    class EmployeeLookup {

        @Test
        void create_unknownEmployee_returns404() throws Exception {
            // Arrange
            when(employeeClient.findById(UNKNOWN_EMPLOYEE_ID)).thenReturn(Optional.empty());
            String request = projectRequest(
                    PROJECT_DESIGNATION,
                    UNKNOWN_EMPLOYEE_ID,
                    PROJECT_START_DATE,
                    PROJECT_PLANNED_END_DATE);
            // Act
            ResultActions result = postProject(request);
            // Assert
            result
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.detail", containsString(Long.toString(UNKNOWN_EMPLOYEE_ID))));
        }

        @Test
        void create_employeeServiceUnavailable_returns503AndSavesNothing() throws Exception {
            // Arrange
            when(employeeClient.findById(RESPONSIBLE_EMPLOYEE_ID))
                    .thenThrow(new EmployeeServiceUnavailableException(new RuntimeException("connection refused")));
            long projectsBefore = projectRepository.count();
            // Act
            ResultActions result = postProject(validRequest());
            // Assert
            result.andExpect(status().isServiceUnavailable());
            assertThat(projectRepository.count()).isEqualTo(projectsBefore);
        }
    }

    @Nested
    class Authentication {

        @Test
        void create_withoutToken_returns401() throws Exception {
            // Arrange
            String request = validRequest();
            // Act
            ResultActions result = mockMvc.perform(post(PROJECTS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request));
            // Assert
            result.andExpect(status().isUnauthorized());
        }
    }

    private ResultActions postProject(String request) throws Exception {
        return mockMvc.perform(post(PROJECTS_ENDPOINT)
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));
    }

    private static String projectRequest(
            String designation,
            long responsibleEmployeeId,
            String startDate,
            String plannedEndDate) {
        return """
                {
                  "designation":"%s",
                  "responsibleEmployeeId":%d,
                  "customerId":%d,
                  "startDate":"%s",
                  "plannedEndDate":"%s"
                }
                """.formatted(
                designation,
                responsibleEmployeeId,
                CUSTOMER_ID,
                startDate,
                plannedEndDate);
    }

    private static String validRequest() {
        return projectRequest(
                PROJECT_DESIGNATION,
                RESPONSIBLE_EMPLOYEE_ID,
                PROJECT_START_DATE,
                PROJECT_PLANNED_END_DATE);
    }
}
