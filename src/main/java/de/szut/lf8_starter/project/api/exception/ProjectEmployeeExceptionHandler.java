package de.szut.lf8_starter.project.api.exception;

import de.szut.lf8_starter.project.domain.exception.EmployeeQualificationException;
import de.szut.lf8_starter.project.domain.exception.ProjectEmployeeConflictException;
import de.szut.lf8_starter.project.domain.exception.ProjectNotFoundException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Getter
@RestControllerAdvice
public class ProjectEmployeeExceptionHandler {

  private static final String PROJECT_NOT_FOUND_TITLE = "Project not found";

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
    problem.setTitle(PROJECT_NOT_FOUND_TITLE);
    problem.setProperty("projectId", exception.getProjectId());
    return problem;
  }

  /**
   * Handles conflicts caused by duplicate or overlapping employee assignments.
   *
   * @param exception the exception describing the assignment conflict
   * @return a problem detail response with HTTP status 409
   */
  @ExceptionHandler(ProjectEmployeeConflictException.class)
  public ProblemDetail handleEmployeeConflict(
          final ProjectEmployeeConflictException exception) {

    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, exception.getMessage());

    problem.setTitle("Employee assignment conflict");
    return problem;
  }

  /**
   * Handles employee assignments with a missing required qualification.
   *
   * @param exception the exception describing the missing qualification
   * @return a problem detail response with HTTP status 422
   */
  @ExceptionHandler(EmployeeQualificationException.class)
  public ProblemDetail handleEmployeeQualification(
          final EmployeeQualificationException exception) {

    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());

    problem.setTitle("Employee qualification missing");
    return problem;
  }
}
