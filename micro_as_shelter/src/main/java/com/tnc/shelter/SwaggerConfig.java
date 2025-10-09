package com.tnc.shelter;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI Configuration for Shelter Microservice
 * Provides API documentation for shelter management operations
 */
@Configuration
public class SwaggerConfig {

    @Value("${server.port:8092}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Shelter Management Microservice API")
                        .description("Comprehensive API for managing shelter operations and animal integration. " +
                                "This service handles shelter CRUD operations, integrates with Animal service " +
                                "via Feign client, and implements circuit breaker patterns for resilience.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("TNC Development Team")
                                .email("support@animalshelter.com")
                                .url("https://animalshelter.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Shelter Microservice Server"),
                        new Server()
                                .url("http://localhost:8092")
                                .description("Production Shelter Service")
                ));
    }
}
