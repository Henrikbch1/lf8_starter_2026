package de.szut.lf8_starter;

import de.szut.lf8_starter.employee.EmployeeClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
@AutoConfigureMockMvc
@Import(IntegrationDatabaseConfig.class)
public abstract class AbstractIntegrationTest {
    @MockitoBean
    protected EmployeeClient employeeClient;
}

@TestConfiguration
class IntegrationDatabaseConfig {
    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgres() {
        return new PostgreSQLContainer<>("postgres:17.6");
    }
}
