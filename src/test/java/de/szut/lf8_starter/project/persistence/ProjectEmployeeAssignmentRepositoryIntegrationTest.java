package de.szut.lf8_starter.project.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.szut.lf8_starter.AbstractIntegrationTest;
import de.szut.lf8_starter.project.domain.ProjectEmployeeAssignment;
import de.szut.lf8_starter.project.domain.ProjectEntity;
import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

class ProjectEmployeeAssignmentRepositoryIntegrationTest extends AbstractIntegrationTest {

    private static final long EMPLOYEE_ID = 101L;
    private static final long OTHER_EMPLOYEE_ID = 102L;
    private static final long QUALIFICATION_ID = 30L;
    private static final long OTHER_QUALIFICATION_ID = 40L;
    private static final long RESPONSIBLE_EMPLOYEE_ID = 1L;
    private static final long CUSTOMER_ID = 1L;
    private static final String PROJECT_DESIGNATION = "Neues Portal";
    private static final String OTHER_PROJECT_DESIGNATION = "Zweites Portal";
    private static final LocalDate PROJECT_START_DATE = LocalDate.of(2026, 1, 1);
    private static final LocalDate PROJECT_PLANNED_END_DATE = LocalDate.of(2026, 12, 31);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectEmployeeAssignmentRepository repository;

    @Test
    void saveAndFlush_duplicateProjectAndEmployee_throwsDataIntegrityViolationException() {
        // Arrange
        ProjectEntity project = saveProject(PROJECT_DESIGNATION);
        repository.saveAndFlush(new ProjectEmployeeAssignment(project, EMPLOYEE_ID, QUALIFICATION_ID));
        ProjectEmployeeAssignment duplicate =
                new ProjectEmployeeAssignment(project, EMPLOYEE_ID, OTHER_QUALIFICATION_ID);
        // Act
        ThrowableAssert.ThrowingCallable action = () -> repository.saveAndFlush(duplicate);
        // Assert
        assertThatThrownBy(action).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void saveAndFlush_sameEmployeeInOtherProject_persistsAssignment() {
        // Arrange
        ProjectEntity project = saveProject(PROJECT_DESIGNATION);
        ProjectEntity otherProject = saveProject(OTHER_PROJECT_DESIGNATION);
        repository.saveAndFlush(new ProjectEmployeeAssignment(project, EMPLOYEE_ID, QUALIFICATION_ID));
        // Act
        ProjectEmployeeAssignment saved = repository.saveAndFlush(
                new ProjectEmployeeAssignment(otherProject, EMPLOYEE_ID, OTHER_QUALIFICATION_ID));
        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findAllByProjectId(otherProject.getId()))
                .extracting(ProjectEmployeeAssignment::getEmployeeId)
                .containsExactly(EMPLOYEE_ID);
    }

    @Test
    void findAllByProjectId_projectWithoutAssignments_returnsEmptyList() {
        // Arrange
        ProjectEntity project = saveProject(PROJECT_DESIGNATION);
        ProjectEntity otherProject = saveProject(OTHER_PROJECT_DESIGNATION);
        repository.saveAndFlush(new ProjectEmployeeAssignment(otherProject, OTHER_EMPLOYEE_ID, QUALIFICATION_ID));
        // Act
        List<ProjectEmployeeAssignment> assignments = repository.findAllByProjectId(project.getId());
        // Assert
        assertThat(assignments).isEmpty();
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
}
