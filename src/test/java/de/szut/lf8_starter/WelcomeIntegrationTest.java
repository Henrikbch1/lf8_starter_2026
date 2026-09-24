package de.szut.lf8_starter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;

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
    void swaggerWithoutTokenRedirectsToUi() throws Exception {
        mockMvc.perform(get("/swagger"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/swagger-ui/index.html*"));
    }

    @Test
    void swaggerUiResourcesWithoutTokenReturn200() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/swagger-ui/swagger-ui.css"))
                .andExpect(status().isOk());
    }
}
