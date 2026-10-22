package de.szut.lf8_starter.project.api;

import de.szut.lf8_starter.project.domain.ProjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Handles exceptions related to project operations. */
@RestControllerAdvice
public class ProjectExceptionHandler {

  /**
   * Handles requests for projects that do not exist.
   *
   * @param exception the exception containing the missing project ID
   * @return a problem detail response with HTTP status 404
   */
  @ExceptionHandler(ProjectNotFoundException.class)
  public ProblemDetail handleProjectNotFound(final ProjectNotFoundException exception) {

    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
    problem.setTitle("Project not found");
    problem.setDetail(exception.getMessage());
    return problem;
  }
}
