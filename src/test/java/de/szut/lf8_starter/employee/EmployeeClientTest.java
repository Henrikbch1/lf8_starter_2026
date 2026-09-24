package de.szut.lf8_starter.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.time.Instant;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class EmployeeClientTest {

    private MockRestServiceServer server;
    private EmployeeClient client;

    @BeforeEach
    void setup() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new EmployeeClient(builder, "http://localhost:8089");
        Jwt jwt = new Jwt("student-token", Instant.now(), Instant.now().plusSeconds(3600),
                Map.of("alg", "none"), Map.of("sub", "student"));
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
        server.verify();
    }

    @Test
    void forwardsTokenAndMapsSkills() {
        // Arrange
        server.expect(requestTo("http://localhost:8089/employees/1"))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer student-token"))
                .andRespond(withSuccess("""
                        {"id":1,"firstName":"Max","lastName":"Mustermann",
                         "skillSet":[{"id":2,"skill":"Java"}],"extra":true}
                        """, MediaType.APPLICATION_JSON));

        // Act
        EmployeeDto employee = client.findById(1).orElseThrow();

        // Assert
        assertThat(employee.skillSet()).containsExactly(new QualificationDto(2L, "Java"));
        assertThat(employee.firstName()).isEqualTo("Max");
    }

    @Test
    void maps404ToEmpty() {
        // Arrange
        server.expect(requestTo("http://localhost:8089/employees/999"))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer student-token"))
                .andRespond(withResourceNotFound());

        // Act
        var employee = client.findById(999);

        // Assert
        assertThat(employee).isEmpty();
    }
}
