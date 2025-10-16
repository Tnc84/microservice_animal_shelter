package com.tnc.apigatewayas;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for Security Configuration
 * Tests that the security configuration is properly loaded and configured
 */
@SpringBootTest
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testSecurityConfigBean_ShouldBeLoaded() {
        // Test that SecurityConfig is properly loaded
        SecurityConfig securityConfig = applicationContext.getBean(SecurityConfig.class);
        assertNotNull(securityConfig, "SecurityConfig should be loaded");
    }

    @Test
    void testSecurityWebFilterChain_ShouldBeConfigured() {
        // Test that SecurityWebFilterChain is properly configured
        SecurityWebFilterChain securityWebFilterChain = applicationContext.getBean(SecurityWebFilterChain.class);
        assertNotNull(securityWebFilterChain, "SecurityWebFilterChain should be configured");
    }

    @Test
    void testCorsConfigurationSource_ShouldBeConfigured() {
        // Test that CORS configuration is properly set up
        org.springframework.web.cors.reactive.CorsConfigurationSource corsConfig = 
            applicationContext.getBean(org.springframework.web.cors.reactive.CorsConfigurationSource.class);
        assertNotNull(corsConfig, "CorsConfigurationSource should be configured");
    }

    @Test
    void testSecurityConfigAnnotation_ShouldBePresent() {
        // Test that the SecurityConfig class has the correct annotations
        Class<?> securityConfigClass = SecurityConfig.class;
        assertNotNull(securityConfigClass.getAnnotation(org.springframework.context.annotation.Configuration.class),
            "SecurityConfig should be annotated with @Configuration");
    }
}