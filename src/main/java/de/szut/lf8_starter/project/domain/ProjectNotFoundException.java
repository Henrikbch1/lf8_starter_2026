package de.szut.lf8_starter.project.domain;

/**
 * Signals that a project with the specified ID does not exist.
 */

public class ProjectNotFoundException extends RuntimeException {

  /**
   * Creates an exception when a project cannot be found.
   *
   * @param id the ID of the project that was not found
   */
  public ProjectNotFoundException (final Long id){
    super("Project with ID" + id + " not found");
  }
}
