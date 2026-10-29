package de.szut.lf8_starter.project.domain;
import lombok.Getter;

/**
 * Exception thrown when a project with the specified ID does not exist.
 */
@Getter
public final class ProjectNotFoundException extends RuntimeException {

  private static final String ERROR_MESSAGE = "Project with ID %d not found";
  private final Long projectId;
  /**
   * Creates an exception for a project that does not exist.
   *
   * @param projectId the ID of the project that was not found
   */
  public ProjectNotFoundException(final Long projectId) {
    super(ERROR_MESSAGE.formatted(projectId));
    this.projectId = projectId;
  }
}

