package de.szut.lf8_starter.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.szut.lf8_starter.AbstractIntegrationTest;
import de.szut.lf8_starter.project.domain.ProjectEmployeeAssignment;
import de.szut.lf8_starter.project.domain.ProjectEntity;
import de.szut.lf8_starter.project.persistence.ProjectEmployeeAssignmentRepository;
import de.szut.lf8_starter.project.persistence.ProjectRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/**
 * Verifies the interplay of the project deletion (T004) with the employee assignments of T006:
 * the database foreign key must remove the assignments of a deleted project.
 */
class ProjectDeleteAssignmentsIntegrationTest extends AbstractIntegrationTest {

    private static final String PROJECT_DESIGNATION = "Project to Delete";
    private static final String OTHER_PROJECT_DESIGNATION = "Project that stays";
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

    @Autowired
    private EntityManager entityManager;

    /**
     * Verifies that deleting a project with assignments returns HTTP 204 and that, after the persistence
     * context is cleared, the database no longer contains the assignments of the deleted project while
     * the assignments of another project remain.
     */
    @Test
    void deleteProject_withAssignments_removesAssignmentsFromDatabase() throws Exception {
        // Arrange
        ProjectEntity project = saveProject(PROJECT_DESIGNATION);
        ProjectEntity otherProject = saveProject(OTHER_PROJECT_DESIGNATION);
        saveAssignment(project, FIRST_EMPLOYEE_ID, FIRST_QUALIFICATION_ID);
        saveAssignment(project, SECOND_EMPLOYEE_ID, SECOND_QUALIFICATION_ID);
        saveAssignment(otherProject, OTHER_PROJECT_EMPLOYEE_ID, OTHER_PROJECT_QUALIFICATION_ID);
        Long projectId = project.getId();
        Long otherProjectId = otherProject.getId();
        // Act
        ResultActions result = mockMvc.perform(delete("/projects/{id}", projectId).with(jwt()));
        entityManager.flush();
        entityManager.clear();
        // Assert
        result.andExpect(status().isNoContent());
        assertThat(projectEmployeeAssignmentRepository.findAllByProjectId(projectId)).isEmpty();
        assertThat(projectEmployeeAssignmentRepository.findAllByProjectId(otherProjectId))
                .extracting(ProjectEmployeeAssignment::getEmployeeId)
                .containsExactly(OTHER_PROJECT_EMPLOYEE_ID);
    }

    /**
     * Verifies that after deleting a project with assignments, the employees endpoint of that project
     * returns HTTP 404.
     */
    @Test
    void findProjectEmployees_afterDeletingProjectWithAssignments_returns404() throws Exception {
        // Arrange
        ProjectEntity project = saveProject(PROJECT_DESIGNATION);
        saveAssignment(project, FIRST_EMPLOYEE_ID, FIRST_QUALIFICATION_ID);
        Long projectId = project.getId();
        // Act
        ResultActions deleteResult = mockMvc.perform(delete("/projects/{id}", projectId).with(jwt()));
        ResultActions result = mockMvc.perform(get("/projects/{id}/employees", projectId).with(jwt()));
        // Assert
        deleteResult.andExpect(status().isNoContent());
        result.andExpect(status().isNotFound());
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
