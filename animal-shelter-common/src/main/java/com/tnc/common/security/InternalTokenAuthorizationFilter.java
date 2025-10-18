package com.tnc.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Internal Token Authorization Filter for Microservices
 * Validates internal JWT tokens from X-Internal-Token header (set by API Gateway)
 */
@RequiredArgsConstructor
public class InternalTokenAuthorizationFilter extends OncePerRequestFilter {

    private final InternalTokenService internalTokenService;
    private static final String INTERNAL_TOKEN_HEADER = "X-Internal-Token";
    private static final String OPTIONS_HTTP_METHOD = "OPTIONS";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        if (request.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD)) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            String internalToken = request.getHeader(INTERNAL_TOKEN_HEADER);
            
            if (internalToken != null && !internalToken.trim().isEmpty()) {
                try {
                    // Validate internal token
                    if (internalTokenService.validateInternalToken(internalToken)) {
                        // Extract user information from internal token
                        String username = internalTokenService.getUsernameFromInternalToken(internalToken);
                        String authorities = internalTokenService.getAuthoritiesFromInternalToken(internalToken);
                        
                        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                            // Parse authorities and create authentication token
                            List<SimpleGrantedAuthority> grantedAuthorities = Arrays.stream(authorities.split(","))
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toList());
                            
                            UsernamePasswordAuthenticationToken authenticationToken = 
                                    new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);
                            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        }
                    } else {
                        // Clear security context for invalid internal token
                        SecurityContextHolder.clearContext();
                    }
                } catch (Exception e) {
                    // Clear security context on any error
                    SecurityContextHolder.clearContext();
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
