package de.szut.lf8_starter.project;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.szut.lf8_starter.project.application.ProjectMapper;
import de.szut.lf8_starter.project.application.ProjectService;
import de.szut.lf8_starter.project.domain.ProjectEntity;
import de.szut.lf8_starter.project.persistence.ProjectRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
  @Mock private ProjectRepository projectRepository;
  @InjectMocks private ProjectService projectService;
  @Spy private ProjectMapper projectMapper = new ProjectMapper();
  /** Verifies that an empty list is returned when no projects exist. */
  @Test
  void findAllProjekte_shouldReturnEmptyList() {
    // Arrange
    when(projectRepository.findAll()).thenReturn(List.of());
    // Act
    List<ProjectEntity> result = projectService.findAllProjects();
    // Assert
    assertTrue(result.isEmpty());
  }

  /** Verifies that all existing projects are correctly returned as DTOs. */
  @Test
  void findAllProjekte_shouldReturnAllProjects() {

    // Arrange: Create two test projects
    ProjectEntity project1 = new ProjectEntity();
    project1.setId(1L);
    project1.setDesignation("Project Alpha");

    ProjectEntity project2 = new ProjectEntity();
    project2.setId(2L);
    project2.setDesignation("Project Beta");

    // Mock the repository to return both projects
    when(projectRepository.findAll()).thenReturn(List.of(project1, project2));

    // Act: Call the service method
    List<ProjectEntity> result = projectService.findAllProjects();

    // Assert: Verify that two projects are returned
    assertEquals(2, result.size());

    // Verify that the first project is returned unchanged
    assertEquals(1L, result.getFirst().getId());
    assertEquals("Project Alpha", result.getFirst().getDesignation());

    // Verify that the second project is returned unchanged
    assertEquals(2L, result.get(1).getId());
    assertEquals("Project Beta", result.get(1).getDesignation());

    // Verify that the repository method is called exactly once
    verify(projectRepository, times(1)).findAll();
  }
}
