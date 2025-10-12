package com.tnc.apigatewayas;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for SecurityConfig
 * Tests security configuration and bean creation
 */
@SpringBootTest
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void securityConfig_ShouldBeLoaded() {
        // Assert
        assertNotNull(applicationContext);
        
        // Verify that security configuration is properly loaded
        assertTrue(applicationContext.containsBean("securityWebFilterChain"));
    }

    @Test
    void jwtAuthenticationFilter_ShouldBeRegistered() {
        // Assert
        assertTrue(applicationContext.containsBean("jwtAuthenticationFilter"));
        
        JwtAuthenticationFilter filter = applicationContext.getBean(JwtAuthenticationFilter.class);
        assertNotNull(filter);
    }

    @Test
    void swaggerConfig_ShouldBeLoaded() {
        // Assert
        assertTrue(applicationContext.containsBean("customOpenAPI"));
    }

    @Test
    void applicationContext_ShouldContainRequiredBeans() {
        // Assert
        String[] expectedBeans = {
            "apiGatewayAsApplication",
            "jwtAuthenticationFilter",
            "customOpenAPI"
        };

        for (String beanName : expectedBeans) {
            assertTrue(applicationContext.containsBean(beanName), 
                    "Bean " + beanName + " should be present in application context");
        }
    }

    @Test
    void securityConfiguration_ShouldBeValid() {
        // This test verifies that the security configuration is syntactically correct
        // and can be loaded without errors
        
        // Assert
        assertNotNull(applicationContext.getBean("securityWebFilterChain"));
    }
}
