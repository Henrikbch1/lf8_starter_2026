package de.szut.lf8_starter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

class WelcomeIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void welcomeWithoutTokenReturns200() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/welcome"))
                .andExpect(status().isOk());
    }

    @Test
    void apiDocsWithoutTokenReturn200() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    void swaggerWithoutTokenReturns200() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/swagger"))
                .andExpect(status().isOk());
    }
}
