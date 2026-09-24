package de.szut.lf8_starter.hello;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.szut.lf8_starter.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

class HelloPostIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createReturns201() throws Exception {
        // Arrange
        String request = """
                {"message":"Hallo"}
                """;

        // Act & Assert
        mockMvc.perform(post("/hello")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.message").value("Hallo"));
    }

    @Test
    void createWithTooShortMessageReturns400() throws Exception {
        // Arrange
        String request = """
                {"message":"Hi"}
                """;

        // Act & Assert
        mockMvc.perform(post("/hello")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.message").exists());
    }

    @Test
    void createWithoutTokenReturns401() throws Exception {
        // Arrange
        String request = """
                {"message":"Hallo"}
                """;

        // Act & Assert
        mockMvc.perform(post("/hello")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized());
    }
}
