package com.tnc.animals.config;

import com.tnc.security.GatewayUserAuthenticationFilter;
import com.tnc.security.util.JwtUtil;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    @Primary
    public GatewayUserAuthenticationFilter gatewayUserAuthenticationFilter() {
        return new GatewayUserAuthenticationFilter();
    }

    @Bean
    @Primary
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }
}
