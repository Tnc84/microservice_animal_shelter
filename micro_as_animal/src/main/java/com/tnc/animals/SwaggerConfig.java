package com.tnc.animals;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI Configuration for Animal Microservice
 * Extends the base configuration from TNC shared library with custom settings
 */
@Configuration
public class SwaggerConfig {

    @Value("${server.port:8093}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Animal Management Microservice API")
                        .description("Comprehensive API for managing animal records in the shelter system. " +
                                "This service handles CRUD operations for animals including registration, " +
                                "updates, photo management, and breed information.")
                        .version("1.0.0"))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Animal Microservice Server"),
                        new Server()
                                .url("http://localhost:8093")
                                .description("Production Animal Service")
                ));
    }
}
