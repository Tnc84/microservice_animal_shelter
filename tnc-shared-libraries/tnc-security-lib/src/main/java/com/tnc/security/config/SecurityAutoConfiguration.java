package com.tnc.security.config;

import com.tnc.security.InternalTokenAuthorizationFilter;
import com.tnc.security.InternalTokenWebFluxFilter;
import com.tnc.security.InternalTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Auto-configuration for TNC Security components.
 * Provides default security configuration that can be customized by microservices.
 * Supports both servlet-based (Spring MVC) and reactive (WebFlux) applications.
 */
@AutoConfiguration
public class SecurityAutoConfiguration {

    /**
     * Creates InternalTokenService bean if not already defined.
     * This ensures the service is available for dependency injection.
     */
    @Bean
    @ConditionalOnMissingBean
    public InternalTokenService internalTokenService() {
        return new InternalTokenService();
    }

    /**
     * Default security filter chain configuration for servlet-based applications.
     * Can be overridden by microservices by providing their own SecurityFilterChain bean.
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(name = "jakarta.servlet.Filter")
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, InternalTokenService internalTokenService) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new InternalTokenAuthorizationFilter(internalTokenService), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Nested configuration for WebFlux/Gateway security.
     * Only loaded when Spring Cloud Gateway is on the classpath.
     * This isolation prevents ClassNotFoundException in servlet applications.
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(GlobalFilter.class)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    protected static class WebFluxSecurityConfiguration {

        /**
         * WebFlux filter for reactive applications.
         * Automatically registered when running in a WebFlux Gateway context.
         */
        @Bean
        public InternalTokenWebFluxFilter internalTokenWebFluxFilter(InternalTokenService internalTokenService) {
            return new InternalTokenWebFluxFilter(internalTokenService);
        }
    }
}
