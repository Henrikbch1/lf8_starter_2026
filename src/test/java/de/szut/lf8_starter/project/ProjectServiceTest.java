package de.szut.lf8_starter.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import de.szut.lf8_starter.employee.EmployeeClient;
import de.szut.lf8_starter.employee.EmployeeDto;
import de.szut.lf8_starter.employee.EmployeeNotFoundException;
import de.szut.lf8_starter.employee.EmployeeServiceUnavailableException;
import de.szut.lf8_starter.project.application.ProjectMapper;
import de.szut.lf8_starter.project.application.ProjectService;
import de.szut.lf8_starter.project.domain.ProjectEmployeeAssignment;
import de.szut.lf8_starter.project.domain.ProjectEntity;
import de.szut.lf8_starter.project.domain.exception.InvalidProjectPeriodException;
import de.szut.lf8_starter.project.domain.exception.ProjectNotFoundException;
import de.szut.lf8_starter.project.domain.exception.ResponsibleEmployeeNotFoundException;
import de.szut.lf8_starter.project.persistence.ProjectEmployeeAssignmentRepository;
import de.szut.lf8_starter.project.persistence.ProjectRepository;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    private static final long FIRST_PROJECT_ID = 1L;
    private static final long SECOND_PROJECT_ID = 2L;
    private static final long MISSING_PROJECT_ID = 999L;
    private static final long RESPONSIBLE_EMPLOYEE_ID = 1L;
    private static final long CHANGED_RESPONSIBLE_EMPLOYEE_ID = 2L;
    private static final String PROJECT_ALPHA_DESIGNATION = "Project Alpha";
    private static final String PROJECT_BETA_DESIGNATION = "Project Beta";
    private static final String PROJECT_GAMMA_DESIGNATION = "Project Gamma";
    private static final String EMPLOYEE_FIRST_NAME = "Max";
    private static final String EMPLOYEE_LAST_NAME = "Mustermann";
    private static final String EMPLOYEE_SERVICE_CONNECTION_ERROR = "connection refused";
    private static final long FIRST_EMPLOYEE_ID = 101L;
    private static final long SECOND_EMPLOYEE_ID = 102L;
    private static final long FIRST_QUALIFICATION_ID = 30L;
    private static final long SECOND_QUALIFICATION_ID = 40L;
    private static final LocalDate PROJECT_START_DATE = LocalDate.of(2026, 11, 1);
    private static final LocalDate PROJECT_END_DATE = LocalDate.of(2026, 12, 31);

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private EmployeeClient employeeClient;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private ProjectEmployeeAssignmentRepository projectEmployeeAssignmentRepository;

    @InjectMocks
    private ProjectService underTest;

    @Nested
    class FindAllProjects {

        @Test
        void findAllProjects_returnsEmptyList() {
            // Arrange
            when(projectRepository.findAll()).thenReturn(List.of());
            // Act
            List<ProjectEntity> projects = underTest.findAllProjects();
            // Assert
            assertThat(projects).isEmpty();
            verify(projectRepository).findAll();
        }

        @Test
        void findAllProjects_returnsAllProjects() {
            // Arrange
            ProjectEntity firstProject = new ProjectEntity();
            firstProject.setId(FIRST_PROJECT_ID);
            firstProject.setDesignation(PROJECT_ALPHA_DESIGNATION);
            ProjectEntity secondProject = new ProjectEntity();
            secondProject.setId(SECOND_PROJECT_ID);
            secondProject.setDesignation(PROJECT_BETA_DESIGNATION);
            when(projectRepository.findAll()).thenReturn(List.of(firstProject, secondProject));
            // Act
            List<ProjectEntity> projects = underTest.findAllProjects();
            // Assert
            assertThat(projects).hasSize(2);
            assertThat(projects.getFirst().getId()).isEqualTo(FIRST_PROJECT_ID);
            assertThat(projects.getFirst().getDesignation()).isEqualTo(PROJECT_ALPHA_DESIGNATION);
            assertThat(projects.getLast().getId()).isEqualTo(SECOND_PROJECT_ID);
            assertThat(projects.getLast().getDesignation()).isEqualTo(PROJECT_BETA_DESIGNATION);
            verify(projectRepository).findAll();
        }
    }

    @Nested
    class FindProjectById {

        @Test
        void findProjectById_returnsProject() {
            // Arrange
            ProjectEntity project = new ProjectEntity();
            project.setId(FIRST_PROJECT_ID);
            project.setDesignation(PROJECT_ALPHA_DESIGNATION);
            when(projectRepository.findById(FIRST_PROJECT_ID)).thenReturn(Optional.of(project));
            // Act
            ProjectEntity result = underTest.findProjectById(FIRST_PROJECT_ID);
            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(FIRST_PROJECT_ID);
            assertThat(result.getDesignation()).isEqualTo(PROJECT_ALPHA_DESIGNATION);
            verify(projectRepository).findById(FIRST_PROJECT_ID);
        }

        @Test
        void findProjectById_missingProject_throwsProjectNotFoundException() {
            // Arrange
            when(projectRepository.findById(MISSING_PROJECT_ID)).thenReturn(Optional.empty());
            // Act
            ThrowableAssert.ThrowingCallable action =
                    () -> underTest.findProjectById(MISSING_PROJECT_ID);
            // Assert
            assertThatThrownBy(action)
                    .isInstanceOfSatisfying(
                            ProjectNotFoundException.class,
                            exception -> {
                                assertThat(exception.getProjectId()).isEqualTo(MISSING_PROJECT_ID);
                                assertThat(exception)
                                        .hasMessage("Project with ID 999 not found");
                            });
            verify(projectRepository).findById(MISSING_PROJECT_ID);
        }
    }

    @Nested
    class Create {

        @Test
        void create_startAfterEnd_throwsInvalidProjectPeriodException() {
            // Arrange
            ProjectEntity project = projectWithPeriod(PROJECT_END_DATE, PROJECT_START_DATE);
            // Act
            ThrowableAssert.ThrowingCallable action = () -> underTest.create(project);
            // Assert
            assertThatThrownBy(action).isInstanceOf(InvalidProjectPeriodException.class);
            verifyNoInteractions(employeeClient, projectRepository);
        }

        @Test
        void create_startEqualsEnd_savesProject() {
            // Arrange
            ProjectEntity project = projectWithPeriod(PROJECT_START_DATE, PROJECT_START_DATE);
            EmployeeDto employee = new EmployeeDto(
                    RESPONSIBLE_EMPLOYEE_ID,
                    EMPLOYEE_FIRST_NAME,
                    EMPLOYEE_LAST_NAME,
                    List.of());
            when(employeeClient.findById(RESPONSIBLE_EMPLOYEE_ID)).thenReturn(Optional.of(employee));
            when(projectRepository.save(project)).thenReturn(project);
            // Act
            ProjectEntity result = underTest.create(project);
            // Assert
            assertThat(result).isSameAs(project);
            verify(employeeClient).findById(RESPONSIBLE_EMPLOYEE_ID);
            verify(projectRepository).save(project);
        }

        @Test
        void create_responsibleEmployeeNotFound_throwsEmployeeNotFoundException() {
            // Arrange
            ProjectEntity project = projectWithPeriod(PROJECT_START_DATE, PROJECT_END_DATE);
            when(employeeClient.findById(RESPONSIBLE_EMPLOYEE_ID)).thenReturn(Optional.empty());
            // Act
            ThrowableAssert.ThrowingCallable action = () -> underTest.create(project);
            // Assert
            assertThatThrownBy(action).isInstanceOf(EmployeeNotFoundException.class);
            verify(employeeClient).findById(RESPONSIBLE_EMPLOYEE_ID);
            verify(projectRepository, never()).save(any(ProjectEntity.class));
        }

        @Test
        void create_employeeServiceUnavailable_throwsEmployeeServiceUnavailableException() {
            // Arrange
            ProjectEntity project = projectWithPeriod(PROJECT_START_DATE, PROJECT_END_DATE);
            when(employeeClient.findById(RESPONSIBLE_EMPLOYEE_ID))
                    .thenThrow(new EmployeeServiceUnavailableException(
                            new RuntimeException(EMPLOYEE_SERVICE_CONNECTION_ERROR)));
            // Act
            ThrowableAssert.ThrowingCallable action = () -> underTest.create(project);
            // Assert
            assertThatThrownBy(action).isInstanceOf(EmployeeServiceUnavailableException.class);
            verify(employeeClient).findById(RESPONSIBLE_EMPLOYEE_ID);
            verify(projectRepository, never()).save(any(ProjectEntity.class));
        }
    }

    @Nested
    class Update {

        @Test
        void update_changedResponsibleEmployee_verifiesAndSavesChanges() {
            // Arrange
            ProjectEntity project = projectWithPeriod(PROJECT_START_DATE, PROJECT_END_DATE);
            project.setId(FIRST_PROJECT_ID);
            project.setDesignation(PROJECT_ALPHA_DESIGNATION);
            ProjectEntity changes = projectWithPeriod(PROJECT_START_DATE, PROJECT_END_DATE);
            changes.setResponsibleEmployeeId(CHANGED_RESPONSIBLE_EMPLOYEE_ID);
            when(projectRepository.findById(FIRST_PROJECT_ID)).thenReturn(Optional.of(project));
            when(employeeClient.findById(CHANGED_RESPONSIBLE_EMPLOYEE_ID))
                    .thenReturn(Optional.of(new EmployeeDto(
                            CHANGED_RESPONSIBLE_EMPLOYEE_ID,
                            EMPLOYEE_FIRST_NAME,
                            EMPLOYEE_LAST_NAME,
                            List.of())));
            when(projectRepository.save(project)).thenReturn(project);
            // Act
            ProjectEntity result = underTest.update(FIRST_PROJECT_ID, changes);
            // Assert
            assertThat(result).isSameAs(project);
            verify(employeeClient).findById(CHANGED_RESPONSIBLE_EMPLOYEE_ID);
            verify(projectMapper).applyUpdate(project, changes);
            verify(projectRepository).save(project);
        }

        @Test
        void update_unchangedResponsibleEmployee_skipsEmployeeLookup() {
            // Arrange
            ProjectEntity project = projectWithPeriod(PROJECT_START_DATE, PROJECT_END_DATE);
            project.setId(FIRST_PROJECT_ID);
            ProjectEntity changes = projectWithPeriod(PROJECT_START_DATE, PROJECT_END_DATE);
            when(projectRepository.findById(FIRST_PROJECT_ID)).thenReturn(Optional.of(project));
            when(projectRepository.save(project)).thenReturn(project);
            // Act
            ProjectEntity result = underTest.update(FIRST_PROJECT_ID, changes);
            // Assert
            assertThat(result).isSameAs(project);
            verifyNoInteractions(employeeClient);
            verify(projectMapper).applyUpdate(project, changes);
            verify(projectRepository).save(project);
        }

        @Test
        void update_missingProject_throwsProjectNotFoundException() {
            // Arrange
            ProjectEntity changes = projectWithPeriod(PROJECT_START_DATE, PROJECT_END_DATE);
            when(projectRepository.findById(MISSING_PROJECT_ID)).thenReturn(Optional.empty());
            // Act
            ThrowableAssert.ThrowingCallable action =
                    () -> underTest.update(MISSING_PROJECT_ID, changes);
            // Assert
            assertThatThrownBy(action).isInstanceOf(ProjectNotFoundException.class);
            verifyNoInteractions(employeeClient, projectMapper);
            verify(projectRepository, never()).save(any(ProjectEntity.class));
        }

        @Test
        void update_invalidPeriod_throwsInvalidProjectPeriodException() {
            // Arrange
            ProjectEntity project = projectWithPeriod(PROJECT_START_DATE, PROJECT_END_DATE);
            project.setId(FIRST_PROJECT_ID);
            ProjectEntity changes = projectWithPeriod(PROJECT_END_DATE, PROJECT_START_DATE);
            when(projectRepository.findById(FIRST_PROJECT_ID)).thenReturn(Optional.of(project));
            // Act
            ThrowableAssert.ThrowingCallable action =
                    () -> underTest.update(FIRST_PROJECT_ID, changes);
            // Assert
            assertThatThrownBy(action).isInstanceOf(InvalidProjectPeriodException.class);
            verifyNoInteractions(employeeClient, projectMapper);
            verify(projectRepository, never()).save(any(ProjectEntity.class));
        }

        @Test
        void update_unknownChangedResponsibleEmployee_throwsResponsibleEmployeeNotFoundException() {
            // Arrange
            ProjectEntity project = projectWithPeriod(PROJECT_START_DATE, PROJECT_END_DATE);
            project.setId(FIRST_PROJECT_ID);
            project.setDesignation(PROJECT_ALPHA_DESIGNATION);
            ProjectEntity changes = projectWithPeriod(PROJECT_START_DATE, PROJECT_END_DATE);
            changes.setResponsibleEmployeeId(CHANGED_RESPONSIBLE_EMPLOYEE_ID);
            when(projectRepository.findById(FIRST_PROJECT_ID)).thenReturn(Optional.of(project));
            when(employeeClient.findById(CHANGED_RESPONSIBLE_EMPLOYEE_ID)).thenReturn(Optional.empty());
            // Act
            ThrowableAssert.ThrowingCallable action =
                    () -> underTest.update(FIRST_PROJECT_ID, changes);
            // Assert
            assertThatThrownBy(action).isInstanceOf(ResponsibleEmployeeNotFoundException.class);
            assertThat(project.getDesignation()).isEqualTo(PROJECT_ALPHA_DESIGNATION);
            verify(employeeClient).findById(CHANGED_RESPONSIBLE_EMPLOYEE_ID);
            verifyNoInteractions(projectMapper);
            verify(projectRepository, never()).save(any(ProjectEntity.class));
        }

        @Test
        void update_employeeServiceUnavailable_leavesProjectUnchanged() {
            // Arrange
            ProjectEntity project = projectWithPeriod(PROJECT_START_DATE, PROJECT_END_DATE);
            project.setId(FIRST_PROJECT_ID);
            project.setDesignation(PROJECT_ALPHA_DESIGNATION);
            ProjectEntity changes = projectWithPeriod(PROJECT_START_DATE, PROJECT_END_DATE);
            changes.setResponsibleEmployeeId(CHANGED_RESPONSIBLE_EMPLOYEE_ID);
            when(projectRepository.findById(FIRST_PROJECT_ID)).thenReturn(Optional.of(project));
            when(employeeClient.findById(CHANGED_RESPONSIBLE_EMPLOYEE_ID))
                    .thenThrow(new EmployeeServiceUnavailableException(
                            new RuntimeException(EMPLOYEE_SERVICE_CONNECTION_ERROR)));
            // Act
            ThrowableAssert.ThrowingCallable action =
                    () -> underTest.update(FIRST_PROJECT_ID, changes);
            // Assert
            assertThatThrownBy(action).isInstanceOf(EmployeeServiceUnavailableException.class);
            assertThat(project.getDesignation()).isEqualTo(PROJECT_ALPHA_DESIGNATION);
            assertThat(project.getResponsibleEmployeeId()).isEqualTo(RESPONSIBLE_EMPLOYEE_ID);
            verify(employeeClient).findById(CHANGED_RESPONSIBLE_EMPLOYEE_ID);
            verifyNoInteractions(projectMapper);
            verify(projectRepository, never()).save(any(ProjectEntity.class));
        }
    }

    @Nested
    class FindEmployeeAssignments {

        @Test
        void findEmployeeAssignments_projectWithAssignments_returnsAssignmentsOfProject() {
            // Arrange
            ProjectEntity project = new ProjectEntity();
            project.setId(FIRST_PROJECT_ID);
            ProjectEmployeeAssignment firstAssignment =
                    new ProjectEmployeeAssignment(project, FIRST_EMPLOYEE_ID, FIRST_QUALIFICATION_ID);
            ProjectEmployeeAssignment secondAssignment =
                    new ProjectEmployeeAssignment(project, SECOND_EMPLOYEE_ID, SECOND_QUALIFICATION_ID);
            when(projectEmployeeAssignmentRepository.findAllByProjectId(FIRST_PROJECT_ID))
                    .thenReturn(List.of(firstAssignment, secondAssignment));
            // Act
            List<ProjectEmployeeAssignment> assignments = underTest.findEmployeeAssignments(project);
            // Assert
            assertThat(assignments)
                    .extracting(ProjectEmployeeAssignment::getEmployeeId, ProjectEmployeeAssignment::getQualificationId)
                    .containsExactly(
                            tuple(FIRST_EMPLOYEE_ID, FIRST_QUALIFICATION_ID),
                            tuple(SECOND_EMPLOYEE_ID, SECOND_QUALIFICATION_ID));
            verify(projectEmployeeAssignmentRepository).findAllByProjectId(FIRST_PROJECT_ID);
        }

        @Test
        void findEmployeeAssignments_projectWithoutAssignments_returnsEmptyList() {
            // Arrange
            ProjectEntity project = new ProjectEntity();
            project.setId(SECOND_PROJECT_ID);
            when(projectEmployeeAssignmentRepository.findAllByProjectId(SECOND_PROJECT_ID))
                    .thenReturn(List.of());
            // Act
            List<ProjectEmployeeAssignment> assignments = underTest.findEmployeeAssignments(project);
            // Assert
            assertThat(assignments).isEmpty();
            verify(projectEmployeeAssignmentRepository).findAllByProjectId(SECOND_PROJECT_ID);
        }
    }

    private static ProjectEntity projectWithPeriod(LocalDate startDate, LocalDate plannedEndDate) {
        ProjectEntity project = new ProjectEntity();
        project.setDesignation(PROJECT_GAMMA_DESIGNATION);
        project.setResponsibleEmployeeId(RESPONSIBLE_EMPLOYEE_ID);
        project.setStartDate(startDate);
        project.setPlannedEndDate(plannedEndDate);
        return project;
    }
}
