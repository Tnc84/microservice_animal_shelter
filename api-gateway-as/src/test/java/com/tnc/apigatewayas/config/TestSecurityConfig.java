package com.tnc.apigatewayas.config;

import com.tnc.apigatewayas.security.JwtService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Test configuration for API Gateway tests
 * Provides mock JWT service for testing
 */
@TestConfiguration
public class TestSecurityConfig {

    @Bean
    @Primary
    public JwtService mockJwtService() {
        return new JwtService() {
            @Override
            public Boolean validateJwtToken(String token) {
                return true; // Always return true for tests
            }
            
            @Override
            public String getUsernameFromJwtToken(String token) {
                return "testuser";
            }
            
            @Override
            public String getUserIdFromJwtToken(String token) {
                return "1";
            }
            
            @Override
            public String getAuthoritiesFromJwtToken(String token) {
                return "ROLE_USER";
            }
        };
    }
}

