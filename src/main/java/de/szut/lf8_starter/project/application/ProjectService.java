package de.szut.lf8_starter.project.application;

import de.szut.lf8_starter.employee.EmployeeClient;
import de.szut.lf8_starter.employee.EmployeeNotFoundException;
import de.szut.lf8_starter.project.domain.InvalidProjectPeriodException;
import de.szut.lf8_starter.project.persistence.ProjectEntity;
import de.szut.lf8_starter.project.persistence.ProjectRepository;
import org.springframework.stereotype.Service;

/**
 * Coordinates project creation and its required validations.
 */
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final EmployeeClient employeeClient;

    /**
     * Creates a service using the project repository and employee client.
     *
     * @param projectRepository repository used to persist projects
     * @param employeeClient client used to verify responsible employees
     */
    public ProjectService(
            final ProjectRepository projectRepository,
            final EmployeeClient employeeClient) {
        this.projectRepository = projectRepository;
        this.employeeClient = employeeClient;
    }

    /**
     * Validates and persists a project.
     *
     * @param project the project to create
     * @return the persisted project
     * @throws InvalidProjectPeriodException if the start date is after the planned end date
     * @throws EmployeeNotFoundException if the responsible employee does not exist
     */
    public ProjectEntity create(final ProjectEntity project) {
        if (project.getStartDate().isAfter(project.getPlannedEndDate())) {
            throw new InvalidProjectPeriodException(project.getStartDate(), project.getPlannedEndDate());
        }

        employeeClient.findById(project.getResponsibleEmployeeId())
                .orElseThrow(() -> new EmployeeNotFoundException(project.getResponsibleEmployeeId()));
        return projectRepository.save(project);
    }
}
