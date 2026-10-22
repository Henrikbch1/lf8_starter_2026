package de.szut.lf8_starter.project;

import de.szut.lf8_starter.project.application.ProjectService;
import de.szut.lf8_starter.project.domain.ProjectEntity;
import de.szut.lf8_starter.project.persistence.ProjectRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

  @Mock
  private ProjectRepository projectRepository;

  @InjectMocks
  private ProjectService projectService;

  /**
   * Verifies that an empty list is returned when no projects exist.
   */
  @Test
  void findAllProjects_shouldReturnEmptyList() {

    // Arrange
    when(projectRepository.findAll()).thenReturn(List.of());

    // Act
    List<ProjectEntity> result = projectService.findAllProjects();

    // Assert
    assertTrue(result.isEmpty());

    verify(projectRepository, times(1)).findAll();
  }

  /**
   * Verifies that all existing projects are correctly returned.
   */
  @Test
  void findAllProjects_shouldReturnAllProjects() {

    // Arrange: Create two test projects
    ProjectEntity project1 = new ProjectEntity();
    project1.setId(1L);
    project1.setDesignation("Project Alpha");

    ProjectEntity project2 = new ProjectEntity();
    project2.setId(2L);
    project2.setDesignation("Project Beta");

    // Mock the repository to return both projects
    when(projectRepository.findAll())
            .thenReturn(List.of(project1, project2));

    // Act: Call the service method
    List<ProjectEntity> result = projectService.findAllProjects();

    // Assert: Verify that two projects are returned
    assertEquals(2, result.size());

    // Verify the first project
    assertEquals(1L, result.get(0).getId());
    assertEquals("Project Alpha", result.get(0).getDesignation());

    // Verify the second project
    assertEquals(2L, result.get(1).getId());
    assertEquals("Project Beta", result.get(1).getDesignation());

    // Verify that the repository method is called exactly once
    verify(projectRepository, times(1)).findAll();
  }
}