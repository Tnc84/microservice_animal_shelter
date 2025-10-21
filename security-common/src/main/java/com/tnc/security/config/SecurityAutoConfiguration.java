package com.tnc.security.config;

import com.tnc.security.InternalTokenService;
import com.tnc.security.JwtService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration for security components
 * Automatically configures security beans when the security-common library is included
 */
@Configuration
public class SecurityAutoConfiguration {
    
    /**
     * Configure JWT Service if not already configured
     */
    @Bean
    @ConditionalOnMissingBean
    public JwtService jwtService() {
        return new JwtService();
    }
    
    /**
     * Configure Internal Token Service if not already configured
     */
    @Bean
    @ConditionalOnMissingBean
    public InternalTokenService internalTokenService() {
        return new InternalTokenService();
    }
}
