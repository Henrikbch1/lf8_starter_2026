package de.szut.lf8_starter.project.api;

import de.szut.lf8_starter.project.domain.ProjectNotFoundException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Getter
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

    ProblemDetail problem =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    problem.setTitle("Project not found");
    problem.setProperty("projectId", exception.getProjectId());
    return problem;
  }
}
