package de.szut.lf8_starter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    // Jeder Client erhält einen eigenen veränderbaren Builder statt eines gemeinsam genutzten.
    @Bean
    @Scope("prototype")
    RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
