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
import java.util.Collections;

/**
 * Security filter for validating internal tokens in inter-microservice communication.
 * This filter validates internal tokens and sets up the security context for authenticated requests.
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

        // Extract token from Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for request: {}", requestPath);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        log.debug("Extracted token for validation");

        // Validate internal token
        if (!internalTokenService.validateInternalToken(token)) {
            log.warn("Invalid internal token for request: {}", requestPath);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Extract service name and set up security context
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
