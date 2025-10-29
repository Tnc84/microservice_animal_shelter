package com.tnc.security.config;

import com.tnc.security.InternalTokenAuthorizationFilter;
import com.tnc.security.InternalTokenWebFluxFilter;
import com.tnc.security.InternalTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.reactive.DispatcherHandler;

/**
 * Auto-configuration for TNC Security components.
 * Provides default security configuration that can be customized by microservices.
 * Supports both servlet-based (Spring MVC) and reactive (WebFlux) applications.
 */
@AutoConfiguration
@ComponentScan(basePackages = "com.tnc.security")
public class SecurityAutoConfiguration {

    @Autowired(required = false)
    private InternalTokenService internalTokenService;

    /**
     * Default security filter chain configuration for servlet-based applications.
     * Can be overridden by microservices by providing their own SecurityFilterChain bean.
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(name = "jakarta.servlet.Filter")
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
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
     * WebFlux filter for reactive applications.
     * Automatically registered when running in a WebFlux context.
     */
    @Bean
    @ConditionalOnClass(DispatcherHandler.class)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    public InternalTokenWebFluxFilter internalTokenWebFluxFilter() {
        return new InternalTokenWebFluxFilter(internalTokenService);
    }
}
