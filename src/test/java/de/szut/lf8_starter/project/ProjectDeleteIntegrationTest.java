package de.szut.lf8_starter.project;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import de.szut.lf8_starter.AbstractIntegrationTest;
import de.szut.lf8_starter.employee.EmployeeDto;
import de.szut.lf8_starter.project.domain.ProjectEntity;
import de.szut.lf8_starter.project.persistence.ProjectRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

public class ProjectDeleteIntegrationTest extends AbstractIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ProjectRepository projectRepository;

  /** Verifies that an existing project can be deleted and cannot be retrieved afterward. */
  @Test
  void deleteProject_shouldReturn204AndGetShouldReturn404() throws Exception {
    // Arrange: Create a project with all required fields.
    ProjectEntity project = new ProjectEntity();
    project.setDesignation("Project to Delete");
    project.setCustomerId(1L);
    project.setResponsibleEmployeeId(1L);
    project.setStartDate(LocalDate.of(2026, 10, 1));
    project.setPlannedEndDate(LocalDate.of(2026, 12, 31));
    // Save the project in the test database.
    ProjectEntity savedProject = projectRepository.saveAndFlush(project);
    Long projectId = savedProject.getId();
    // Act & Assert: Delete the project and expect HTTP 204 with an empty body.
    mockMvc
        .perform(delete("/projects/{id}", projectId).with(jwt()))
        .andExpect(status().isNoContent())
        .andExpect(content().string(""));
    // Act & Assert: Verify that the deleted project returns HTTP 404.
    mockMvc.perform(get("/projects/{id}", projectId).with(jwt())).andExpect(status().isNotFound());
  }
  /**
   * Verifies that deleting a non-existing project returns HTTP 404 and an error message containing
   * the requested project ID.
   */
  @Test
  void deleteProject_shouldReturn404WhenProjectDoesNotExist() throws Exception {
    // Arrange: Use an ID that does not exist in the database.
    Long projectId = 999999L;
    // Act & Assert: Attempt to delete the non-existing project.
    mockMvc
        .perform(delete("/projects/{id}", projectId).with(jwt()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.detail").value("Project with ID 999999 not found"));
  }
  /** Verifies that deleting a project without authentication returns HTTP 401 Unauthorized. */
  @Test
  void deleteProject_withoutToken_shouldReturn401() throws Exception {
    // Arrange: Define a project ID for the unauthenticated request.
    Long projectId = 1L;
    // Act & Assert: Send a DELETE request without a JWT token.
    mockMvc.perform(delete("/projects/{id}", projectId)).andExpect(status().isUnauthorized());
  }
  /**
   * Verifies the complete project lifecycle through the REST API: create a project, delete it, and
   * confirm that it no longer exists.
   */
  @Test
  void createDeleteAndGetProject_shouldReturn201204And404() throws Exception {
    // Arrange: Mock an existing employee returned by the Employee-Service.
    EmployeeDto employee = mock(EmployeeDto.class);
    // Return the employee when the service searches for ID 1.
    when(employeeClient.findById(1L)).thenReturn(Optional.of(employee));
    // Arrange: Prepare the JSON request for creating a project.
    String requestBody =
        """
                {
                    "designation": "Project Lifecycle Test",
                    "responsibleEmployeeId": 1,
                    "customerId": 1,
                    "startDate": "2026-10-01",
                    "plannedEndDate": "2026-12-31",
                    "requiredQualificationIds": []
                }
                """;

    // Act & Assert: Create a project and expect HTTP 201.
    String location =
        mockMvc
            .perform(
                post("/projects")
                    .with(jwt())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andReturn()
            .getResponse()
            .getHeader("Location");

    // Extract the project ID from the Location header.
    Long projectId = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));
    // Act & Assert: Delete the created project and expect HTTP 204.
    mockMvc
        .perform(delete("/projects/{id}", projectId).with(jwt()))
        .andExpect(status().isNoContent())
        .andExpect(content().string(""));
    // Act & Assert: Verify that the deleted project returns HTTP 404.
    mockMvc.perform(get("/projects/{id}", projectId).with(jwt())).andExpect(status().isNotFound());
  }
}
