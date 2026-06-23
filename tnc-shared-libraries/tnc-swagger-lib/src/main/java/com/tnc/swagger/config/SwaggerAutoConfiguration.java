package com.tnc.swagger.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

/**
 * Auto-configuration for TNC Swagger/OpenAPI components.
 * Provides default OpenAPI configuration that can be customized by microservices.
 */
@AutoConfiguration
@ComponentScan(basePackages = "com.tnc.swagger")
public class SwaggerAutoConfiguration {

    /**
     * Default OpenAPI configuration.
     * Can be overridden by microservices by providing their own OpenAPI bean.
     */
    @Bean
    @ConditionalOnMissingBean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TNC Microservice API")
                        .version("1.0.0")
                        .description("API documentation for TNC microservice")
                        .contact(new Contact()
                                .name("TNC Development Team")
                                .email("dev@tnc.com")
                                .url("https://www.tnc.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development server"),
                        new Server()
                                .url("https://api.tnc.com")
                                .description("Production server")
                ));
    }
}
