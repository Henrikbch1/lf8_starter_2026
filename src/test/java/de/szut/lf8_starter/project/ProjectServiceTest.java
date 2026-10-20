package de.szut.lf8_starter.project;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.szut.lf8_starter.project.api.ProjectGetDto;
import de.szut.lf8_starter.project.application.ProjectService;
import de.szut.lf8_starter.project.domain.ProjectEntity;
import de.szut.lf8_starter.project.persistence.ProjectRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
  @Mock private ProjectRepository projectRepository;
  @InjectMocks private ProjectService projectService;

  /** Verifies that an empty list is returned when no projects exist. */
  @Test
  void findAllProjekte_shouldReturnEmptyList() {
    // Arrange
    when(projectRepository.findAll()).thenReturn(List.of());
    // Act
    List<ProjectGetDto> result = projectService.findAllProjekte();
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
    List<ProjectGetDto> result = projectService.findAllProjekte();

    // Assert: Verify that two projects are returned
    assertEquals(2, result.size());

    // Verify that the first project is correctly mapped to a DTO
    assertEquals(1L, result.get(0).id());
    assertEquals("Project Alpha", result.get(0).designation());

    // Verify that the second project is correctly mapped to a DTO
    assertEquals(2L, result.get(1).id());
    assertEquals("Project Beta", result.get(1).designation());

    // Verify that the repository method is called exactly once
    verify(projectRepository, times(1)).findAll();
  }
}
