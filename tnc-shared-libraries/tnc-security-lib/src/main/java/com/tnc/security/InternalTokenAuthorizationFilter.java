package com.tnc.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Security filter for handling authentication from two sources:
 * 1. User requests from API Gateway (via X-User-* headers) - sets user roles
 * 2. Inter-service communication (via internal tokens) - sets ROLE_INTERNAL_SERVICE
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InternalTokenAuthorizationFilter extends OncePerRequestFilter {

    private final InternalTokenService internalTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        log.debug("Processing request: {} {}", request.getMethod(), requestPath);

        // Skip authentication for public endpoints
        if (isPublicEndpoint(requestPath)) {
            log.debug("Skipping authentication for public endpoint: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        // PRIORITY 1: Check if request comes from API Gateway (has X-User headers)
        String userAuthorities = request.getHeader("X-User-Authorities");
        String userName = request.getHeader("X-User-Name");
        String userId = request.getHeader("X-User-Id");
        
        if (userAuthorities != null && userName != null) {
            // Request from API Gateway - set user context with actual user roles
            List<SimpleGrantedAuthority> authorities = Arrays.stream(userAuthorities.split(","))
                .map(String::trim)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
            
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(userName, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            log.debug("Set user context from API Gateway: {} (userId: {}) with roles: {}", 
                userName, userId, authorities);
            filterChain.doFilter(request, response);
            return;
        }

        // PRIORITY 2: Check for internal token (inter-service communication)
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for request: {}", requestPath);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        log.debug("Extracted internal token for validation");

        // Validate internal token
        if (!internalTokenService.validateInternalToken(token)) {
            log.warn("Invalid internal token for request: {}", requestPath);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Extract service name and set up security context with ROLE_INTERNAL_SERVICE
        String serviceName = internalTokenService.extractServiceName(token);
        if (serviceName != null) {
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(
                    serviceName, 
                    null, 
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_INTERNAL_SERVICE"))
                );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Set up authentication for internal service: {}", serviceName);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Check if the request path is a public endpoint that doesn't require authentication.
     *
     * @param requestPath the request path
     * @return true if the endpoint is public, false otherwise
     */
    private boolean isPublicEndpoint(String requestPath) {
        return requestPath.startsWith("/actuator/") ||
               requestPath.startsWith("/swagger-ui/") ||
               requestPath.startsWith("/v3/api-docs/") ||
               requestPath.equals("/animals/getAll"); // Specific public endpoint for animals
    }
}
