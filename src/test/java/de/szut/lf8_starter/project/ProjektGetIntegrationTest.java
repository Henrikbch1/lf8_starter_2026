package de.szut.lf8_starter.project;

import de.szut.lf8_starter.AbstractIntegrationTest;
import de.szut.lf8_starter.project.domain.ProjectEntity;
import de.szut.lf8_starter.project.persistence.ProjectRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProjectGetIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    /**
     * Verifies that GET /projects returns HTTP 200
     * and all existing projects as JSON.
     */
    @Test
    void getAllProjects_shouldReturn200AndProjects() throws Exception {

        // Arrange: Create a project with all required fields
        ProjectEntity project = new ProjectEntity();
        project.setDesignation("Project Alpha");
        project.setCustomerId(1L);
        project.setResponsibleEmployeeId(1L);
        project.setStartDate(LocalDate.of(2026, 10, 1));
        project.setPlannedEndDate(LocalDate.of(2026, 12, 31));

        projectRepository.saveAndFlush(project);

        // Act & Assert: Perform an authenticated GET request
        mockMvc.perform(get("/projects")
                        .with(jwt())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].designation").value("Project Alpha"))
                .andExpect(jsonPath("$[0].customerId").value(1))
                .andExpect(jsonPath("$[0].responsibleEmployeeId").value(1));
    }

    /**
     * Verifies that GET /projects returns HTTP 200
     * and an empty JSON array when no projects exist.
     */
    @Test
    void getAllProjects_shouldReturn200AndEmptyList() throws Exception {

        // Arrange: Ensure that no projects exist
        projectRepository.deleteAll();
        projectRepository.flush();

        // Act & Assert: Perform an authenticated GET request
        mockMvc.perform(get("/projects")
                        .with(jwt())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
    /**
     * Verifies that GET /projects returns HTTP 401
     * when the request does not contain a JWT token.
     */
    @Test
    void getAllProjects_withoutToken_shouldReturn401() throws Exception {

        // Act & Assert: Perform a GET request without authentication
        mockMvc.perform(get("/projects")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}