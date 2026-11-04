package de.szut.lf8_starter.project.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Set;

import de.szut.lf8_starter.AbstractIntegrationTest;
import de.szut.lf8_starter.project.domain.ProjectEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ProjectRepositoryIntegrationTest extends AbstractIntegrationTest {

    private static final long RESPONSIBLE_EMPLOYEE_ID = 1L;
    private static final long CUSTOMER_ID = 5L;
    private static final long FIRST_REQUIRED_QUALIFICATION_ID = 10L;
    private static final long SECOND_REQUIRED_QUALIFICATION_ID = 20L;
    private static final String PROJECT_DESIGNATION = "Neues Portal";
    private static final LocalDate PROJECT_START_DATE = LocalDate.of(2026, 1, 1);
    private static final LocalDate PROJECT_PLANNED_END_DATE = LocalDate.of(2026, 12, 31);

    @Autowired
    private ProjectRepository repository;

    @Test
    void saveAndFlush_validProject_persistsGeneratedIdAndQualifications() {
        // Arrange
        ProjectEntity project = new ProjectEntity();
        project.setDesignation(PROJECT_DESIGNATION);
        project.setResponsibleEmployeeId(RESPONSIBLE_EMPLOYEE_ID);
        project.setCustomerId(CUSTOMER_ID);
        project.setStartDate(PROJECT_START_DATE);
        project.setPlannedEndDate(PROJECT_PLANNED_END_DATE);
        project.setRequiredQualificationIds(Set.of(
                FIRST_REQUIRED_QUALIFICATION_ID,
                SECOND_REQUIRED_QUALIFICATION_ID));
        // Act
        ProjectEntity saved = repository.saveAndFlush(project);
        // Assert
        assertThat(saved.getId()).isNotNull();
        ProjectEntity loaded = repository.findById(saved.getId()).orElseThrow();
        assertThat(loaded.getId()).isEqualTo(saved.getId());
        assertThat(loaded.getDesignation()).isEqualTo(PROJECT_DESIGNATION);
        assertThat(loaded.getResponsibleEmployeeId()).isEqualTo(RESPONSIBLE_EMPLOYEE_ID);
        assertThat(loaded.getCustomerId()).isEqualTo(CUSTOMER_ID);
        assertThat(loaded.getStartDate()).isEqualTo(PROJECT_START_DATE);
        assertThat(loaded.getPlannedEndDate()).isEqualTo(PROJECT_PLANNED_END_DATE);
        assertThat(loaded.getRequiredQualificationIds()).containsExactlyInAnyOrder(
                FIRST_REQUIRED_QUALIFICATION_ID,
                SECOND_REQUIRED_QUALIFICATION_ID);
    }
}
