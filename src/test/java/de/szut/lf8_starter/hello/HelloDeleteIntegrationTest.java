package de.szut.lf8_starter.hello;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.szut.lf8_starter.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

class HelloDeleteIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HelloRepository repository;

    @Test
    void deleteReturns204AndRemovesEntry() throws Exception {
        // Arrange
        HelloEntity saved = repository.save(new HelloEntity("Zum Löschen"));

        // Act
        mockMvc.perform(delete("/hello/{id}", saved.getId()).with(jwt()))
                .andExpect(status().isNoContent());

        // Assert: 204 allein beweist nicht, dass wirklich gelöscht wurde.
        assertThat(repository.findById(saved.getId())).isEmpty();
    }

    @Test
    void deleteUnknownIdReturns404() throws Exception {
        // Arrange: Es gibt keine Hello-Entität mit einer negativen ID.
        long unknownId = -1;

        // Act & Assert
        mockMvc.perform(delete("/hello/{id}", unknownId).with(jwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteWithoutTokenReturns401() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/hello/{id}", -1))
                .andExpect(status().isUnauthorized());
    }
}
