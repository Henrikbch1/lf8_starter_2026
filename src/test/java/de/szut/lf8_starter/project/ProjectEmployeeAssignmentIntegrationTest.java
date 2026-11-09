
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
}
