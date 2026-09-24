package de.szut.lf8_starter.hello;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.szut.lf8_starter.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

class HelloGetIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HelloRepository repository;

    @Test
    void getAllReturns200AndSavedHello() throws Exception {
        // Arrange
        HelloEntity saved = repository.save(new HelloEntity("Alle abrufen"));

        // Act & Assert
        mockMvc.perform(get("/hello").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id==" + saved.getId() + ")].message")
                        .value(hasItem("Alle abrufen")));
    }

    @Test
    void getAllWithoutTokenReturns401() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/hello"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getByMessageReturns200AndMatchingHello() throws Exception {
        // Arrange
        HelloEntity saved = repository.save(new HelloEntity("Gesuchte Nachricht"));
        repository.save(new HelloEntity("Andere Nachricht"));

        // Act & Assert
        mockMvc.perform(get("/hello")
                        .param("message", "Gesuchte Nachricht")
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(saved.getId()));
    }

    @Test
    void getByMessageWithoutTokenReturns401() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/hello")
                        .param("message", "Gesuchte Nachricht"))
                .andExpect(status().isUnauthorized());
    }
}
