package com.tnc.namingserveras;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Service Discovery functionality
 * Tests Eureka server configuration and service registration capabilities
 */
@SpringBootTest
@ActiveProfiles("test")
class ServiceDiscoveryTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void eurekaServer_ShouldBeEnabled() {
        // Assert
        assertNotNull(applicationContext);
        
        // Verify that Eureka server is properly configured
        String eurekaClientRegister = applicationContext.getEnvironment()
                .getProperty("eureka.client.register-with-eureka");
        String eurekaClientFetch = applicationContext.getEnvironment()
                .getProperty("eureka.client.fetch-registry");
        
        assertNotNull(eurekaClientRegister);
        assertNotNull(eurekaClientFetch);
    }

    @Test
    void eurekaServerConfiguration_ShouldBeValid() {
        // Assert
        assertNotNull(applicationContext.getEnvironment());
        
        // Verify Eureka server specific configurations
        String serverPort = applicationContext.getEnvironment().getProperty("server.port");
        String applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");
        
        assertNotNull(serverPort);
        assertEquals("8761", serverPort);
        assertNotNull(applicationName);
        assertEquals("eureka-server-as", applicationName);
    }

    @Test
    void eurekaServerProperties_ShouldBeConfigured() {
        // Assert
        String waitTime = applicationContext.getEnvironment()
                .getProperty("eureka.server.wait-time-in-ms-when-sync-empty");
        String registerWithEureka = applicationContext.getEnvironment()
                .getProperty("eureka.client.register-with-eureka");
        String fetchRegistry = applicationContext.getEnvironment()
                .getProperty("eureka.client.fetch-registry");
        
        assertNotNull(waitTime);
        assertNotNull(registerWithEureka);
        assertNotNull(fetchRegistry);
        
        // Verify that server is configured to not register with itself
        assertEquals("false", registerWithEureka);
        assertEquals("false", fetchRegistry);
    }

    @Test
    void swaggerDocumentation_ShouldBeAvailable() {
        // Assert
        assertTrue(applicationContext.containsBean("customOpenAPI"));
        
        // Verify that Swagger configuration is properly loaded
        String swaggerPath = applicationContext.getEnvironment()
                .getProperty("springdoc.api-docs.path");
        String swaggerUiPath = applicationContext.getEnvironment()
                .getProperty("springdoc.swagger-ui.path");
        
        assertNotNull(swaggerPath);
        assertNotNull(swaggerUiPath);
    }

    @Test
    void applicationContext_ShouldLoadAllRequiredComponents() {
        // Assert
        String[] requiredBeans = {
            "namingServerAsApplication",
            "customOpenAPI"
        };

        for (String beanName : requiredBeans) {
            assertTrue(applicationContext.containsBean(beanName), 
                    "Required bean " + beanName + " should be loaded");
        }
    }

    @Test
    void eurekaServer_ShouldHandleServiceRegistration() {
        // This test verifies that the Eureka server is configured to handle
        // service registration and discovery operations
        
        // Assert
        assertNotNull(applicationContext);
        
        // Verify that the server is configured as a standalone Eureka server
        String registerWithEureka = applicationContext.getEnvironment()
                .getProperty("eureka.client.register-with-eureka");
        String fetchRegistry = applicationContext.getEnvironment()
                .getProperty("eureka.client.fetch-registry");
        
        assertEquals("false", registerWithEureka);
        assertEquals("false", fetchRegistry);
    }

    @Test
    void serverConfiguration_ShouldBeProductionReady() {
        // Assert
        String serverPort = applicationContext.getEnvironment().getProperty("server.port");
        String applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");
        
        // Verify production-ready configurations
        assertNotNull(serverPort);
        assertNotNull(applicationName);
        assertFalse(serverPort.isEmpty());
        assertFalse(applicationName.isEmpty());
        
        // Verify that the server is configured on the standard Eureka port
        assertEquals("8761", serverPort);
    }
}
