package com.tnc.animals.config;

import com.tnc.security.InternalTokenService;
import com.tnc.security.JwtService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    @Primary
    public InternalTokenService internalTokenService() {
        return new InternalTokenService();
    }

    @Bean
    @Primary
    public JwtService jwtService() {
        return new JwtService();
    }
}
