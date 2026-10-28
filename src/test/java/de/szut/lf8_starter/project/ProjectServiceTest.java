package de.szut.lf8_starter.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import de.szut.lf8_starter.project.domain.InvalidProjectPeriodException;
import de.szut.lf8_starter.project.domain.ProjectEntity;
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
    private static final long RESPONSIBLE_EMPLOYEE_ID = 1L;
    private static final String PROJECT_ALPHA_DESIGNATION = "Project Alpha";
    private static final String PROJECT_BETA_DESIGNATION = "Project Beta";
    private static final String PROJECT_GAMMA_DESIGNATION = "Project Gamma";
    private static final String EMPLOYEE_FIRST_NAME = "Max";
    private static final String EMPLOYEE_LAST_NAME = "Mustermann";
    private static final String EMPLOYEE_SERVICE_CONNECTION_ERROR = "connection refused";
    private static final LocalDate PROJECT_START_DATE = LocalDate.of(2026, 11, 1);
    private static final LocalDate PROJECT_END_DATE = LocalDate.of(2026, 12, 31);

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private EmployeeClient employeeClient;

    @Mock
    private ProjectMapper projectMapper;

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
            assertThat(projects)
                    .extracting(ProjectEntity::getId)
                    .containsExactly(FIRST_PROJECT_ID, SECOND_PROJECT_ID);
            assertThat(projects)
                    .extracting(ProjectEntity::getDesignation)
                    .containsExactly(PROJECT_ALPHA_DESIGNATION, PROJECT_BETA_DESIGNATION);
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

    private static ProjectEntity projectWithPeriod(LocalDate startDate, LocalDate plannedEndDate) {
        ProjectEntity project = new ProjectEntity();
        project.setDesignation(PROJECT_GAMMA_DESIGNATION);
        project.setResponsibleEmployeeId(RESPONSIBLE_EMPLOYEE_ID);
        project.setStartDate(startDate);
        project.setPlannedEndDate(plannedEndDate);
        return project;
    }
}
