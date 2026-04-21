package com.tnc.security.config;

import com.tnc.security.GatewayUserAuthenticationFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Auto-configuration for TNC Security components.
 *
 * <p>Servlet-based microservices rely entirely on the JWT user token validated
 * at the API Gateway. The Gateway forwards the authenticated user context via
 * X-User-* headers, and {@link GatewayUserAuthenticationFilter} maps it into
 * the Spring Security context for role-based authorization.</p>
 */
@AutoConfiguration
public class SecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(name = "jakarta.servlet.Filter")
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public GatewayUserAuthenticationFilter gatewayUserAuthenticationFilter() {
        return new GatewayUserAuthenticationFilter();
    }

    /**
     * Default servlet security filter chain. Microservices may override it
     * by defining their own {@link SecurityFilterChain} bean.
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(name = "jakarta.servlet.Filter")
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http,
                                                          GatewayUserAuthenticationFilter gatewayUserAuthenticationFilter) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(gatewayUserAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
