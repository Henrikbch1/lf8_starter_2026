package de.szut.lf8_starter.hello;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.szut.lf8_starter.AbstractIntegrationTest;
import de.szut.lf8_starter.employee.EmployeeDto;
import de.szut.lf8_starter.employee.EmployeeServiceUnavailableException;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

class HelloIntegrationTest extends AbstractIntegrationTest {
    @Autowired MockMvc mvc;

    @Test void create201AndValidation400() throws Exception {
        mvc.perform(post("/hello").with(jwt()).contentType(MediaType.APPLICATION_JSON).content("{\"message\":\"Hallo\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.message").value("Hallo"));
        mvc.perform(post("/hello").with(jwt()).contentType(MediaType.APPLICATION_JSON).content("{\"message\":\"Hi\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.message").exists());
    }

    @Test void everyHelloRouteRequiresJwt() throws Exception {
        mvc.perform(post("/hello").contentType(MediaType.APPLICATION_JSON).content("{\"message\":\"Hallo\"}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/hello")).andExpect(status().isUnauthorized());
        mvc.perform(get("/hello").param("message", "Hallo")).andExpect(status().isUnauthorized());
        mvc.perform(get("/hello/findByMessage").param("message", "Hallo")).andExpect(status().isUnauthorized());
        mvc.perform(delete("/hello/123")).andExpect(status().isUnauthorized());
        mvc.perform(get("/hello/greeting/1")).andExpect(status().isUnauthorized());
    }

    @Test void readsFiltersAndDeletes() throws Exception {
        String result = mvc.perform(post("/hello").with(jwt()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"Eindeutig123\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = Long.parseLong(result.replaceAll(".*\\\"id\\\":([0-9]+).*", "$1"));
        mvc.perform(get("/hello").with(jwt())).andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.message=='Eindeutig123')]").exists());
        mvc.perform(get("/hello").param("message", "Eindeutig123").with(jwt()))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(id));
        mvc.perform(get("/hello/findByMessage").param("message", "Eindeutig123").with(jwt()))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(id));
        mvc.perform(delete("/hello/{id}", id).with(jwt())).andExpect(status().isNoContent());
        mvc.perform(delete("/hello/{id}", id).with(jwt())).andExpect(status().isNotFound());
    }

    @Test void greetingSuccessMissingAndUnavailable() throws Exception {
        when(employeeClient.findById(1)).thenReturn(Optional.of(new EmployeeDto(1, "Max", "Mustermann", List.of())));
        when(employeeClient.findById(999)).thenReturn(Optional.empty());
        when(employeeClient.findById(2)).thenThrow(new EmployeeServiceUnavailableException(new java.net.ConnectException()));
        mvc.perform(get("/hello/greeting/1").with(jwt())).andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hallo Max Mustermann"));
        mvc.perform(get("/hello/greeting/999").with(jwt())).andExpect(status().isNotFound());
        mvc.perform(get("/hello/greeting/2").with(jwt())).andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.detail").value("Employee-Service nicht erreichbar – läuft docker compose?"));
    }

    @Test void anonymousWelcomeAndDocs() throws Exception {
        mvc.perform(get("/welcome")).andExpect(status().isOk());
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
        mvc.perform(get("/swagger")).andExpect(status().isOk());
    }
}
