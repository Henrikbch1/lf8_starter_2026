package de.szut.lf8_starter.project.persistence;

import de.szut.lf8_starter.project.domain.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provides persistence operations for projects.
 */
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
}
