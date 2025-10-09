package com.tnc.apigatewayas;

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
 * Swagger/OpenAPI Configuration for API Gateway
 * Provides centralized API documentation for all microservices
 */
@Configuration
public class SwaggerConfig {

    @Value("${server.port:8765}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Animal Shelter Microservices API Gateway")
                        .description("Centralized API Gateway for Animal Shelter Management System. " +
                                "This gateway provides access to all microservices including Animal Management, " +
                                "Shelter Management, and User Management services.")
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
                                .description("API Gateway Server"),
                        new Server()
                                .url("http://localhost:8765")
                                .description("Production API Gateway")
                ));
    }
}
