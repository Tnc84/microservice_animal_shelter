package org.tnc.pethotelmicroservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI Configuration for Pet Hotel Microservice.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI petHotelOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pet Hotel Microservice API")
                        .version("1.0.0")
                        .description("REST API for Pet Hotel room bookings and management")
                        .contact(new Contact()
                                .name("TNC Team")
                                .email("support@tnc.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT Authorization header using the Bearer scheme")));
    }
}
