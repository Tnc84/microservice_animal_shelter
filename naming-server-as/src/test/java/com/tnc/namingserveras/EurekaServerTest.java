package com.tnc.namingserveras;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Eureka Naming Server
 * Tests service discovery configuration and server functionality
 */
@SpringBootTest
@ActiveProfiles("test")
class EurekaServerTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void eurekaServer_ShouldBeConfigured() {
        // Assert
        assertNotNull(applicationContext);
        assertTrue(applicationContext.containsBean("namingServerAsApplication"));
    }

    @Test
    void eurekaServerConfiguration_ShouldBeLoaded() {
        // Assert
        assertTrue(applicationContext.containsBean("namingServerAsApplication"));
        
        // Verify that Eureka server configuration is properly loaded
        assertNotNull(applicationContext.getEnvironment());
    }

    @Test
    void swaggerConfiguration_ShouldBeActive() {
        // Assert
        assertTrue(applicationContext.containsBean("customOpenAPI"));
    }

    @Test
    void applicationContext_ShouldContainRequiredBeans() {
        // Assert
        String[] expectedBeans = {
            "namingServerAsApplication",
            "customOpenAPI"
        };

        for (String beanName : expectedBeans) {
            assertTrue(applicationContext.containsBean(beanName), 
                    "Bean " + beanName + " should be present in application context");
        }
    }

    @Test
    void eurekaServer_ShouldStartWithoutErrors() {
        // This test verifies that the Eureka server can start successfully
        // with all required configurations
        
        // Assert
        assertNotNull(applicationContext);
        
        // Verify that all critical beans are loaded
        String[] criticalBeans = {
            "namingServerAsApplication",
            "customOpenAPI"
        };

        for (String beanName : criticalBeans) {
            assertTrue(applicationContext.containsBean(beanName), 
                    "Critical bean " + beanName + " should be loaded");
        }
    }

    @Test
    void configurationProperties_ShouldBeLoaded() {
        // Assert
        assertNotNull(applicationContext.getEnvironment());
        
        // Verify that the application can access configuration properties
        String applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");
        assertNotNull(applicationName);
        assertEquals("eureka-server-as", applicationName);
    }

    @Test
    void eurekaServerPort_ShouldBeConfigured() {
        // Assert
        String serverPort = applicationContext.getEnvironment().getProperty("server.port");
        assertNotNull(serverPort);
        assertEquals("8761", serverPort);
    }
}
