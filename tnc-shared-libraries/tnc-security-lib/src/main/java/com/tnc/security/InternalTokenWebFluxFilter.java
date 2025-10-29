package com.tnc.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

/**
 * WebFlux-compatible security filter for validating internal tokens in inter-microservice communication.
 * This filter validates internal tokens and sets up the security context for authenticated requests.
 * Used in reactive applications like Spring Cloud Gateway.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InternalTokenWebFluxFilter implements GlobalFilter {

    private final InternalTokenService internalTokenService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestPath = request.getPath().value();
        log.debug("Processing request: {} {}", request.getMethod(), requestPath);

        // Skip authentication for public endpoints
        if (isPublicEndpoint(requestPath)) {
            log.debug("Skipping authentication for public endpoint: {}", requestPath);
            return chain.filter(exchange);
        }

        // Extract token from Authorization header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for request: {}", requestPath);
            return unauthorizedResponse(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        log.debug("Extracted token for validation");

        // Validate internal token
        if (!internalTokenService.validateInternalToken(token)) {
            log.warn("Invalid internal token for request: {}", requestPath);
            return unauthorizedResponse(exchange, "Invalid internal token");
        }

        // Extract service name and set up security context
        String serviceName = internalTokenService.extractServiceName(token);
        if (serviceName != null) {
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(serviceName, null, 
                    List.of(new SimpleGrantedAuthority("ROLE_INTERNAL_SERVICE")));
            
            SecurityContext securityContext = org.springframework.security.core.context.SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            
            return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
        }

        return chain.filter(exchange);
    }

    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/actuator/health") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/auth/");
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");
        
        String body = String.format("{\"error\": \"Unauthorized\", \"message\": \"%s\"}", message);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }
}
