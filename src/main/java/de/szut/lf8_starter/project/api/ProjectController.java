package de.szut.lf8_starter.project.api;

import de.szut.lf8_starter.employee.EmployeeNotFoundException;
import de.szut.lf8_starter.employee.EmployeeServiceUnavailableException;
import de.szut.lf8_starter.project.api.dto.*;
import de.szut.lf8_starter.project.application.ProjectMapper;
import de.szut.lf8_starter.project.application.ProjectService;
import de.szut.lf8_starter.project.domain.ProjectEntity;
import de.szut.lf8_starter.project.domain.exception.EmployeeQualificationException;
import de.szut.lf8_starter.project.domain.exception.InvalidProjectPeriodException;
import de.szut.lf8_starter.project.domain.exception.ProjectEmployeeConflictException;
import de.szut.lf8_starter.project.domain.exception.ProjectNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/** Exposes REST endpoints for creating projects. */
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

  private final ProjectService projectService;
  private final ProjectMapper projectMapper;

  /**
   * Creates a project and returns its representation and resource location.
   *
   * @param request validated project creation request
   * @return the created project with its Location header
   * @throws InvalidProjectPeriodException if the start date is after the planned end date
   * @throws EmployeeNotFoundException if the responsible employee does not exist
   * @throws EmployeeServiceUnavailableException if the employee service is unavailable
   */
  @Operation(
      summary = "Create a project",
      description = "Creates a project and verifies the responsible employee.")
  @ApiResponse(responseCode = "201", description = "Project created successfully")
  @ApiResponse(responseCode = "400", description = "Invalid input data")
  @ApiResponse(responseCode = "401", description = "Authentication required")
  @ApiResponse(responseCode = "404", description = "Responsible employee not found")
  @ApiResponse(responseCode = "422", description = "Start date is after the planned end date")
  @ApiResponse(responseCode = "503", description = "Employee service unavailable")
  @PostMapping
  public ResponseEntity<ProjectGetDto> create(@Valid @RequestBody final ProjectCreateDto request) {
    final ProjectGetDto createdProject =
        projectMapper.toDto(projectService.create(projectMapper.fromCreate(request)));
    final URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(createdProject.id())
            .toUri();

    return ResponseEntity.created(location).body(createdProject);
  }

  /**
   * Retrieves all projects.
   *
   * @return a list of all projects, or an empty list if none exist
   */
  @Operation(
      summary = "Get all projects",
      description = "Returns all projects. If no projects exist, an empty list is returned.")
  @ApiResponse(responseCode = "200", description = "Projects retrieved successfully")
  @ApiResponse(responseCode = "401", description = "Authentication required")
  @GetMapping
  public ResponseEntity<List<ProjectGetDto>> findAllProjects() {
    List<ProjectGetDto> projects =
        projectService.findAllProjects().stream().map(projectMapper::toDto).toList();
    return ResponseEntity.ok(projects);
  }

  /**
   * Retrieves a project by its ID.
   *
   * @param id the ID of the project to retrieve
   * @return the project as a ProjectGetDto with HTTP status 200 OK
   * @throws ProjectNotFoundException if no project exists with the given ID
   */
  @Operation(summary = "Get a project by ID", description = "Returns a single project by its ID.")
  @ApiResponse(responseCode = "200", description = "Project retrieved successfully")
  @ApiResponse(responseCode = "401", description = "Authentication required")
  @ApiResponse(responseCode = "404", description = "Project with the specified ID not found")
  @GetMapping("/{id}")
  public ResponseEntity<ProjectGetDto> findProjectById(@PathVariable Long id) {
    ProjectEntity project = projectService.findProjectById(id);
    ProjectGetDto projectDto = projectMapper.toDto(project);
    return ResponseEntity.ok(projectDto);
  }

  /**
   * Assigns an employee to an existing project with a specified qualification.
   *
   * @param projectId the ID of the project
   * @param request the validated employee assignment request
   * @return HTTP 201 Created if the employee was successfully assigned
   * @throws ProjectNotFoundException if the project does not exist
   * @throws EmployeeNotFoundException if the employee does not exist
   * @throws EmployeeQualificationException if the employee lacks the required qualification
   * @throws ProjectEmployeeConflictException if the assignment already exists or overlaps with
   *     another project assignment
   * @throws EmployeeServiceUnavailableException if the employee service is unavailable
   */
  @Operation(
      summary = "Assign an employee to a project",
      description =
          "Assigns an employee to an existing project using a qualification "
              + "as the employee's role. Duplicate assignments and overlapping "
              + "project periods are not allowed.")
  @ApiResponse(responseCode = "201", description = "Employee successfully assigned to the project")
  @ApiResponse(responseCode = "400", description = "Invalid request data")
  @ApiResponse(responseCode = "401", description = "Authentication required")
  @ApiResponse(responseCode = "404", description = "Project or employee not found")
  @ApiResponse(
      responseCode = "409",
      description = "Employee already assigned or project periods overlap")
  @ApiResponse(
      responseCode = "422",
      description = "Employee does not have the required qualification")
  @ApiResponse(responseCode = "503", description = "Employee service unavailable")
  @PostMapping("/{projectId}/employees")
  public ResponseEntity<Void> assignEmployee(
      @PathVariable final Long projectId,
      @Valid @RequestBody final ProjectEmployeeCreateDto request) {

    // Validate and persist the employee assignment.
    projectService.assignEmployee(projectId, request.employeeId(), request.qualificationId());

    // Return HTTP 201 Created after the assignment has been saved.
    return ResponseEntity.status(201).build();
  }

  /**
   * Retrieves the employees assigned to a project.
   *
   * @param id the ID of the project whose employees are requested
   * @return the project ID, its designation and its assigned employees with HTTP status 200 OK
   * @throws ProjectNotFoundException if no project exists with the given ID
   */
  @Operation(
      summary = "Get the employees of a project",
      description =
          "Returns the employees assigned to a project with their role. "
              + "If the project has no assigned employees, an empty list is returned.")
  @ApiResponse(
      responseCode = "200",
      description = "Project employees retrieved successfully",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ProjectEmployeesGetDto.class)))
  @ApiResponse(responseCode = "401", description = "Authentication required")
  @ApiResponse(
      responseCode = "404",
      description = "Project with the specified ID not found",
      content =
          @Content(
              mediaType = "application/problem+json",
              schema = @Schema(implementation = ProblemDetail.class)))
  @GetMapping("/{id}/employees")
  public ResponseEntity<ProjectEmployeesGetDto> findProjectEmployees(@PathVariable final Long id) {
    final ProjectEntity project = projectService.findProjectById(id);
    final List<ProjectEmployeeGetDto> employees =
        projectService.findEmployeeAssignments(project).stream()
            .map(ProjectEmployeeGetDto::from)
            .toList();
    return ResponseEntity.ok(
        new ProjectEmployeesGetDto(project.getId(), project.getDesignation(), employees));
  }

  /**
   * Deletes a project by its ID.
   *
   * @param id the ID of the project to delete
   * @return HTTP 204 No Content if the project was deleted successfully
   * @throws ProjectNotFoundException if no project exists with the given ID
   */
  @Operation(
      summary = "Delete a project by ID",
      description = "Deletes an existing project by its ID.")
  @ApiResponse(responseCode = "204", description = "Project deleted successfully")
  @ApiResponse(responseCode = "401", description = "Authentication required")
  @ApiResponse(responseCode = "404", description = "Project with the specified ID not found")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProjectById(@PathVariable Long id) {
    // Delete the project using the service layer.
    projectService.deleteById(id);
    // Return HTTP 204 without a response body.
    return ResponseEntity.noContent().build();
  }
}
