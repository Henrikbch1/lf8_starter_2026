
package de.szut.lf8_starter.project;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.szut.lf8_starter.AbstractIntegrationTest;
import de.szut.lf8_starter.employee.EmployeeDto;
import de.szut.lf8_starter.employee.QualificationDto;
import de.szut.lf8_starter.project.domain.ProjectEmployeeAssignment;
import de.szut.lf8_starter.project.domain.ProjectEntity;
import de.szut.lf8_starter.project.persistence.ProjectEmployeeAssignmentRepository;
import de.szut.lf8_starter.project.persistence.ProjectRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for assigning employees to projects.
 */
class ProjectEmployeeAssignmentIntegrationTest extends AbstractIntegrationTest {

    private static final long EMPLOYEE_ID = 101L;
    private static final long QUALIFICATION_ID = 30L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectEmployeeAssignmentRepository assignmentRepository;

    /**
     * Verifies that an employee with the required qualification
     * can be assigned to an existing project.
     */
    @Test
    void assignEmployee_validRequest_returns201AndSavesAssignment() throws Exception {

        // Arrange: Create an existing project.
        ProjectEntity project = new ProjectEntity();
        project.setDesignation("Project Alpha");
        project.setResponsibleEmployeeId(1L);
        project.setCustomerId(1L);
        project.setStartDate(LocalDate.of(2026, 10, 1));
        project.setPlannedEndDate(LocalDate.of(2026, 12, 31));

        project = projectRepository.saveAndFlush(project);

        // Arrange: Mock the employee service response.
        EmployeeDto employee = new EmployeeDto(
                EMPLOYEE_ID,
                "Max",
                "Mustermann",
                List.of(new QualificationDto(QUALIFICATION_ID, "Java"))
        );

        when(employeeClient.findById(EMPLOYEE_ID))
                .thenReturn(Optional.of(employee));

        String requestBody = """
                {
                  "employeeId": 101,
                  "qualificationId": 30
                }
                """;

        // Act: Assign the employee to the project.
        mockMvc.perform(post("/projects/{projectId}/employees", project.getId())
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        // Assert: Verify the assignment was saved.
        List<ProjectEmployeeAssignment> assignments =
                assignmentRepository.findAllByProjectId(project.getId());

        assertEquals(1, assignments.size());
        assertEquals(EMPLOYEE_ID, assignments.get(0).getEmployeeId());
        assertEquals(QUALIFICATION_ID, assignments.get(0).getQualificationId());
        assertTrue(assignmentRepository.existsByProjectIdAndEmployeeId(
                project.getId(), EMPLOYEE_ID));
    }


    /**
     * Verifies that assigning an employee to a nonexistent project returns HTTP 404.
     */
    @Test
    void assignEmployee_unknownProject_returns404() throws Exception {
        mockMvc.perform(post("/projects/{projectId}/employees", 999999L)
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "employeeId": 101,
                              "qualificationId": 30
                            }
                            """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.projectId").value(999999));
    }

    /**
     * Verifies that an employee without the required qualification
     * cannot be assigned to a project.
     */
    @Test
    void assignEmployee_missingQualification_returns422() throws Exception {
        ProjectEntity project = createProject(
                "Qualification Test",
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 12, 31));

        EmployeeDto employee = new EmployeeDto(
                101L,
                "Max",
                "Mustermann",
                List.of(new QualificationDto(40L, "Python")));

        when(employeeClient.findById(101L))
                .thenReturn(Optional.of(employee));

        mockMvc.perform(post("/projects/{projectId}/employees", project.getId())
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "employeeId": 101,
                              "qualificationId": 30
                            }
                            """))
                .andExpect(status().isUnprocessableEntity());

        assertTrue(assignmentRepository.findAllByProjectId(project.getId()).isEmpty());
    }

    /**
     * Verifies that assigning the same employee twice to one project
     * returns HTTP 409.
     */
    @Test
    void assignEmployee_duplicateAssignment_returns409() throws Exception {
        ProjectEntity project = createProject(
                "Duplicate Test",
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 12, 31));

        EmployeeDto employee = new EmployeeDto(
                101L,
                "Max",
                "Mustermann",
                List.of(new QualificationDto(30L, "Java")));

        when(employeeClient.findById(101L))
                .thenReturn(Optional.of(employee));

        assignmentRepository.saveAndFlush(
                new ProjectEmployeeAssignment(project, 101L, 30L));

        mockMvc.perform(post("/projects/{projectId}/employees", project.getId())
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "employeeId": 101,
                              "qualificationId": 30
                            }
                            """))
                .andExpect(status().isConflict());

        assertEquals(1, assignmentRepository.findAllByProjectId(project.getId()).size());
    }

    /**
     * Verifies that overlapping project periods prevent an employee assignment.
     * A shared boundary date also counts as an overlap.
     */
    @Test
    void assignEmployee_overlappingPeriods_returns409() throws Exception {
        ProjectEntity existingProject = createProject(
                "Existing Project",
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 11, 30));

        ProjectEntity newProject = createProject(
                "New Project",
                LocalDate.of(2026, 11, 30),
                LocalDate.of(2026, 12, 31));

        assignmentRepository.saveAndFlush(
                new ProjectEmployeeAssignment(existingProject, 101L, 30L));

        EmployeeDto employee = new EmployeeDto(
                101L,
                "Max",
                "Mustermann",
                List.of(new QualificationDto(30L, "Java")));

        when(employeeClient.findById(101L))
                .thenReturn(Optional.of(employee));

        mockMvc.perform(post("/projects/{projectId}/employees", newProject.getId())
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "employeeId": 101,
                              "qualificationId": 30
                            }
                            """))
                .andExpect(status().isConflict());

        assertTrue(assignmentRepository.findAllByProjectId(newProject.getId()).isEmpty());
    }

    /**
     * Verifies that unauthenticated requests return HTTP 401.
     */
    @Test
    void assignEmployee_withoutToken_returns401() throws Exception {
        mockMvc.perform(post("/projects/{projectId}/employees", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "employeeId": 101,
                              "qualificationId": 30
                            }
                            """))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Creates and persists a project for integration testing.
     *
     * @param designation the project designation
     * @param startDate the project start date
     * @param endDate the planned project end date
     * @return the persisted project
     */
    private ProjectEntity createProject(
            String designation,
            LocalDate startDate,
            LocalDate endDate) {

        ProjectEntity project = new ProjectEntity();
        project.setDesignation(designation);
        project.setResponsibleEmployeeId(1L);
        project.setCustomerId(1L);
        project.setStartDate(startDate);
        project.setPlannedEndDate(endDate);

        return projectRepository.saveAndFlush(project);
    }
    /**
     * Verifies that assigning a non-existing employee returns HTTP 404,
     * includes the missing employee ID in the response,
     * and does not persist an assignment.
     *
     * @throws Exception if the HTTP request execution fails
     */
    @Test
    void assignEmployee_employeeNotFound_returns404() throws Exception {

        // Arrange: Create and persist an existing project.
        ProjectEntity project = createProject(
                "Employee Not Found Test",
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 12, 31));

        // Arrange: Simulate an employee that does not exist.
        when(employeeClient.findById(999L))
                .thenReturn(Optional.empty());

        // Act and Assert: Send the assignment request and expect HTTP 404.
        mockMvc.perform(post("/projects/{projectId}/employees", project.getId())
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "employeeId": 999,
                              "qualificationId": 30
                            }
                            """))
                .andExpect(status().isNotFound())

                // Assert: Verify that the error message contains the employee ID.
                .andExpect(jsonPath("$.detail").value(
                        org.hamcrest.Matchers.containsString("999")));

        // Assert: Verify that no assignment was persisted.
        assertTrue(assignmentRepository
                .findAllByProjectId(project.getId())
                .isEmpty());
    }
}
