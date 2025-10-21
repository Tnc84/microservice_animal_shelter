package com.tnc.apigatewayas.config;

import com.tnc.security.config.SecurityAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

/**
 * Test configuration to import Security Auto-Configuration
 * This ensures that JwtService and InternalTokenService beans are available during tests
 */
@TestConfiguration
@Import(SecurityAutoConfiguration.class)
public class TestSecurityConfig {
}

