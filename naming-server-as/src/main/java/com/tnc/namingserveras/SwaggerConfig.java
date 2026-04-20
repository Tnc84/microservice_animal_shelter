package com.tnc.namingserveras;

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
 * Swagger/OpenAPI Configuration for Eureka Naming Server
 * Provides API documentation for service discovery operations
 */
@Configuration
public class SwaggerConfig {

    @Value("${server.port:8761}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Eureka Service Discovery Server API")
                        .description("Service discovery and registration server for the Animal Shelter " +
                                "microservices ecosystem. This Eureka server manages service registration, " +
                                "health monitoring, and service discovery for all microservices.")
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
                                .description("Eureka Server"),
                        new Server()
                                .url("http://localhost:8761")
                                .description("Production Eureka Server")
                ));
    }
}
