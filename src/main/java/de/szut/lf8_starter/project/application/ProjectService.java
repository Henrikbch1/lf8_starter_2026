package de.szut.lf8_starter.project.application;

import de.szut.lf8_starter.employee.EmployeeClient;
import de.szut.lf8_starter.employee.EmployeeNotFoundException;
import de.szut.lf8_starter.project.domain.ProjectEmployeeAssignment;
import de.szut.lf8_starter.project.domain.ProjectEntity;
import de.szut.lf8_starter.project.domain.exception.InvalidProjectPeriodException;
import de.szut.lf8_starter.project.domain.exception.ProjectNotFoundException;
import de.szut.lf8_starter.project.persistence.ProjectEmployeeAssignmentRepository;
import de.szut.lf8_starter.project.persistence.ProjectRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Coordinates project creation and its required validations. */
@Service
@RequiredArgsConstructor
public class ProjectService {

  private final ProjectRepository projectRepository;
  private final EmployeeClient employeeClient;
  private final ProjectMapper projectMapper;
  private final ProjectEmployeeAssignmentRepository projectEmployeeAssignmentRepository;

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

    employeeClient
        .findById(project.getResponsibleEmployeeId())
        .orElseThrow(() -> new EmployeeNotFoundException(project.getResponsibleEmployeeId()));
    return projectRepository.save(project);
  }

  /**
   * Retrieves all projects from the database.
   *
   * @return all projects as entities, or an empty list if no projects exist
   */
  public List<ProjectEntity> findAllProjects() {
    return projectRepository.findAll();
  }

  /**
   * Finds a project by its ID.
   *
   * @param projectId the ID of the project to find
   * @return the project entity if it exists
   * @throws ProjectNotFoundException if no project exists with the given ID
   */
  public ProjectEntity findProjectById(Long projectId) {
    return projectRepository
        .findById(projectId)
        .orElseThrow(
            () ->
                // Throw an exception if no project is found with the given ID.
                new ProjectNotFoundException(projectId));
  }

  /**
   * Retrieves the employee assignments of an existing project.
   *
   * @param project the project whose assignments are requested
   * @return the assignments of the project, or an empty list if it has none
   */
  public List<ProjectEmployeeAssignment> findEmployeeAssignments(final ProjectEntity project) {
    return projectEmployeeAssignmentRepository.findAllByProjectId(project.getId());
  }

  /**
   * Deletes a project by its ID.
   *
   * @param projectId the ID of the project to delete
   * @throws ProjectNotFoundException if the project does not exist
   */
  public void deleteById(final Long projectId){
    // Find the project and throw an exception if it does not exist.
    ProjectEntity project = findProjectById(projectId);
    // Delete the existing project from the database.
    projectRepository.delete(project);
  }
}
