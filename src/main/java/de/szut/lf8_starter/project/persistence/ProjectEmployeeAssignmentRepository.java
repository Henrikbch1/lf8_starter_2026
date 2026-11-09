package de.szut.lf8_starter.project.persistence;

import de.szut.lf8_starter.project.domain.ProjectEmployeeAssignment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provides persistence operations for employee assignments of projects.
 */
public interface ProjectEmployeeAssignmentRepository
        extends JpaRepository<ProjectEmployeeAssignment, Long> {

    /**
     * Finds all employee assignments of the given project.
     *
     * @param projectId the ID of the project
     * @return the assignments of the project, or an empty list if it has none
     */
    List<ProjectEmployeeAssignment> findAllByProjectId(Long projectId);

    /**
     * Checks whether an employee is already assigned to a specific project.
     * Used to prevent duplicate assignments within the same project.
     *
     * @param projectId the ID of the project
     * @param employeeId the ID of the employee
     * @return true if the employee is already assigned, otherwise false
     */
    boolean existsByProjectIdAndEmployeeId(Long projectId, Long employeeId);

    /**
     * Finds all project assignments of a specific employee.
     * Used to check whether project periods overlap before creating a new assignment.
     *
     * @param employeeId the ID of the employee
     * @return all existing project assignments of the employee, or an empty list
     */
    List<ProjectEmployeeAssignment> findAllByEmployeeId(Long employeeId);
}
