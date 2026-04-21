package com.tnc.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Translates the authenticated user context forwarded by the API Gateway
 * (headers: X-User-Name, X-User-Authorities, X-User-Id) into a Spring
 * Authentication stored in the SecurityContext.
 *
 * JWT validation is performed once at the Gateway. Microservices are
 * expected to trust these headers on the internal Docker network.
 */
@Slf4j
public class GatewayUserAuthenticationFilter extends OncePerRequestFilter {

    public static final String HEADER_USER_NAME = "X-User-Name";
    public static final String HEADER_USER_AUTHORITIES = "X-User-Authorities";
    public static final String HEADER_USER_ID = "X-User-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String username = request.getHeader(HEADER_USER_NAME);
        String authoritiesHeader = request.getHeader(HEADER_USER_AUTHORITIES);

        if (username != null && !username.isBlank()) {
            List<SimpleGrantedAuthority> authorities = (authoritiesHeader == null || authoritiesHeader.isBlank())
                    ? Collections.emptyList()
                    : Arrays.stream(authoritiesHeader.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (log.isDebugEnabled()) {
                log.debug("Authenticated gateway user='{}' userId='{}' authorities={}",
                        username, request.getHeader(HEADER_USER_ID), authorities);
            }
        }

        filterChain.doFilter(request, response);
    }
}
