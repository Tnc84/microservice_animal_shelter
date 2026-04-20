package com.tnc.userManagement;

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
 * Swagger/OpenAPI Configuration for User Management Microservice
 * Provides API documentation for user management operations
 */
@Configuration
public class SwaggerConfig {

    @Value("${server.port:8091}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Management Microservice API")
                        .description("Comprehensive API for managing user accounts and authentication in the shelter system. " +
                                "This service handles user registration, authentication, password reset functionality, " +
                                "and role-based access control.")
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
                                .description("User Management Microservice Server"),
                        new Server()
                                .url("http://localhost:8091")
                                .description("Production User Management Service")
                ));
    }
}
