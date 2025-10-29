package com.tnc.resilience.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

/**
 * Auto-configuration for TNC Resilience components.
 * Provides default resilience configuration that can be customized by microservices.
 */
@AutoConfiguration
@ComponentScan(basePackages = "com.tnc.resilience")
public class ResilienceAutoConfiguration {

    /**
     * Default RestTemplate bean for external API calls.
     * Can be overridden by microservices by providing their own RestTemplate bean.
     */
    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
