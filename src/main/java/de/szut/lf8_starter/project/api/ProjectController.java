package de.szut.lf8_starter.project.api;

import java.net.URI;
import java.util.List;

import de.szut.lf8_starter.employee.EmployeeNotFoundException;
import de.szut.lf8_starter.employee.EmployeeServiceUnavailableException;
import de.szut.lf8_starter.project.application.ProjectMapper;
import de.szut.lf8_starter.project.application.ProjectService;
import de.szut.lf8_starter.project.domain.InvalidProjectPeriodException;
import de.szut.lf8_starter.project.domain.ProjectEntity;
import de.szut.lf8_starter.project.domain.ProjectNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Exposes REST endpoints for creating projects.
 */
@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectMapper projectMapper;

    /**
     * Creates a controller using the project service and mapper.
     *
     * @param projectService service that validates and persists projects
     * @param projectMapper mapper between project requests, entities, and responses
     */
    public ProjectController(final ProjectService projectService, final ProjectMapper projectMapper) {
        this.projectService = projectService;
        this.projectMapper = projectMapper;
    }

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
        final ProjectGetDto createdProject = projectMapper.toDto(
                projectService.create(projectMapper.fromCreate(request)));
        final URI location = ServletUriComponentsBuilder.fromCurrentRequest()
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
            description = "Returns all projects. If no projects exist, an empty list is returned."
    )
    @ApiResponse(responseCode = "200", description = "Projects retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")

    @GetMapping
    public ResponseEntity<List<ProjectGetDto>> findAllProjects() {
        List<ProjectGetDto> projects = projectService.findAllProjects()
                .stream()
                .map(projectMapper :: toDto)
                .toList();
        return ResponseEntity.ok(projects);
    }

    /**
     * Retrieves a project by its ID.
     *
     * @param id the ID of the project to retrieve
     * @return the project as a ProjectGetDto with HTTP status 200 OK
     * @throws ProjectNotFoundException if no project exists with the given ID
     */
    @Operation(
            summary = "Get a project by ID",
            description = "Returns a single project by its ID."
    )
    @ApiResponse(responseCode = "200", description = "Project retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Project with the specified ID not found")
    @GetMapping("/{id}")
    public ResponseEntity<ProjectGetDto> findProjectById(@PathVariable Long id){
        ProjectEntity project = projectService.findProjectById(id);
        ProjectGetDto projectDto = projectMapper.toDto(project);
        return ResponseEntity.ok(projectDto);
    }
}
