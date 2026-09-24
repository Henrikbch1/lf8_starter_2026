package de.szut.lf8_starter.hello;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.ConnectException;
import java.util.List;
import java.util.Optional;

import de.szut.lf8_starter.AbstractIntegrationTest;
import de.szut.lf8_starter.employee.EmployeeDto;
import de.szut.lf8_starter.employee.EmployeeServiceUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

class HelloGreetingIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void greetingForKnownEmployeeReturns200() throws Exception {
        // Arrange
        EmployeeDto employee = new EmployeeDto(1, "Max", "Mustermann", List.of());
        when(employeeClient.findById(1)).thenReturn(Optional.of(employee));

        // Act & Assert
        mockMvc.perform(get("/hello/greeting/1").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hallo Max Mustermann"));
    }

    @Test
    void greetingForUnknownEmployeeReturns404() throws Exception {
        // Arrange
        when(employeeClient.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/hello/greeting/999").with(jwt()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Mitarbeiter 999 nicht gefunden"));
    }

    @Test
    void greetingWhenEmployeeServiceIsUnavailableReturns503() throws Exception {
        // Arrange
        when(employeeClient.findById(1))
                .thenThrow(new EmployeeServiceUnavailableException(new ConnectException()));

        // Act & Assert
        mockMvc.perform(get("/hello/greeting/1").with(jwt()))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.detail")
                        .value("Employee-Service nicht erreichbar – läuft docker compose?"));
    }

    @Test
    void greetingWithoutTokenReturns401() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/hello/greeting/1"))
                .andExpect(status().isUnauthorized());
    }
}
