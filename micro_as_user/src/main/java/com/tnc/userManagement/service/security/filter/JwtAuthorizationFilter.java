package com.tnc.userManagement.service.security.filter;

import com.tnc.userManagement.service.constant.SecurityConstant;
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

import com.tnc.common.security.JwtService;
/**
 * JWT Authorization Filter for Shelter Microservice
 * Validates JWT tokens from X-User-Token header (set by API Gateway)
 */
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private static final String TOKEN_HEADER = "X-User-Token";
    private static final String OPTIONS_HTTP_METHOD = "OPTIONS";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        if (request.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD)) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            String token = request.getHeader(TOKEN_HEADER);
            
            if (token != null && !token.trim().isEmpty()) {
                String username = jwtService.getUsernameFromJwtToken(token);
                
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    if (jwtService.validateJwtToken(token)) {
                        String authorities = jwtService.getAuthoritiesFromJwtToken(token);
                        List<SimpleGrantedAuthority> grantedAuthorities = Arrays.stream(authorities.split(","))
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());
                        
                        UsernamePasswordAuthenticationToken authenticationToken = 
                                new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    } else {
                        SecurityContextHolder.clearContext();
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
