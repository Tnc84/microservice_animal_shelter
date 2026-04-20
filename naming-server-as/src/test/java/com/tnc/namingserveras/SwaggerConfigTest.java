package com.tnc.namingserveras;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SwaggerConfig
 * Tests OpenAPI configuration for Eureka Naming Server
 */
@SpringBootTest
@ActiveProfiles("test")
class SwaggerConfigTest {

    @Autowired
    private OpenAPI openAPI;

    @Test
    void openAPI_ShouldBeConfigured() {
        // Assert
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertNotNull(openAPI.getServers());
    }

    @Test
    void openAPI_ShouldHaveCorrectInfo() {
        // Act
        Info info = openAPI.getInfo();

        // Assert
        assertNotNull(info);
        assertEquals("Eureka Service Discovery Server API", info.getTitle());
        assertTrue(info.getDescription().contains("Service discovery"));
        assertTrue(info.getDescription().contains("Eureka server"));
        assertTrue(info.getDescription().contains("Animal Shelter"));
        assertTrue(info.getDescription().contains("microservices"));
        assertEquals("1.0.0", info.getVersion());
        assertNotNull(info.getContact());
        assertNotNull(info.getLicense());
    }

    @Test
    void openAPI_ShouldHaveContactInformation() {
        // Act
        Info info = openAPI.getInfo();

        // Assert
        assertNotNull(info.getContact());
        assertEquals("TNC Development Team", info.getContact().getName());
        assertEquals("support@animalshelter.com", info.getContact().getEmail());
        assertEquals("https://animalshelter.com", info.getContact().getUrl());
    }

    @Test
    void openAPI_ShouldHaveLicenseInformation() {
        // Act
        Info info = openAPI.getInfo();

        // Assert
        assertNotNull(info.getLicense());
        assertEquals("MIT License", info.getLicense().getName());
        assertEquals("https://opensource.org/licenses/MIT", info.getLicense().getUrl());
    }

    @Test
    void openAPI_ShouldHaveServerConfiguration() {
        // Act
        var servers = openAPI.getServers();

        // Assert
        assertNotNull(servers);
        assertFalse(servers.isEmpty());
        
        Server server = servers.get(0);
        assertNotNull(server);
        assertNotNull(server.getUrl());
        assertTrue(server.getUrl().contains("localhost"));
    }

    @Test
    void openAPI_ShouldHaveEurekaSpecificDescription() {
        // Act
        Info info = openAPI.getInfo();

        // Assert
        String description = info.getDescription();
        assertTrue(description.contains("Eureka"));
        assertTrue(description.contains("service discovery"));
        assertTrue(description.contains("microservices"));
        assertTrue(description.contains("Animal Shelter"));
    }

    @Test
    void openAPI_ShouldBeProperlyFormatted() {
        // Assert
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo().getTitle());
        assertNotNull(openAPI.getInfo().getDescription());
        assertNotNull(openAPI.getInfo().getVersion());
        assertFalse(openAPI.getInfo().getTitle().isEmpty());
        assertFalse(openAPI.getInfo().getDescription().isEmpty());
        assertFalse(openAPI.getInfo().getVersion().isEmpty());
    }

    @Test
    void openAPI_ShouldContainServiceDiscoveryKeywords() {
        // Act
        Info info = openAPI.getInfo();

        // Assert
        String description = info.getDescription();
        assertTrue(description.contains("service registration"));
        assertTrue(description.contains("health monitoring"));
        assertTrue(description.contains("service discovery"));
    }
}
