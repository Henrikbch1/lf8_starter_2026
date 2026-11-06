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
}
