package com.tnc.apigatewayas.security;

import com.tnc.common.security.JwtService;
import com.tnc.common.security.InternalTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * JWT Authentication Filter for API Gateway
 * Validates JWT tokens and forwards user context to downstream services
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter {

    private final JwtService jwtService;
    private final InternalTokenService internalTokenService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        
        // Skip validation for public endpoints
        if (isPublicEndpoint(path)) {
            return chain.filter(exchange);
        }
        
        // Extract JWT token from Authorization header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            return unauthorizedResponse(exchange, "Missing or invalid Authorization header");
        }
        
        String token = authHeader.substring(7);
        
        if (token.trim().isEmpty()) {
            log.warn("Empty token provided for path: {}", path);
            return unauthorizedResponse(exchange, "Empty token provided");
        }
        
        // Validate JWT token
        if (!jwtService.validateJwtToken(token)) {
            log.warn("Invalid JWT token for path: {}", path);
            return unauthorizedResponse(exchange, "Invalid or expired token");
        }
        
        try {
            // Extract user information from client JWT token
            String username = jwtService.getUsernameFromJwtToken(token);
            String authorities = jwtService.getAuthoritiesFromJwtToken(token);
            String userId = jwtService.getUserIdFromJwtToken(token);
            
            log.debug("Valid client token for user: {} with authorities: {}", username, authorities);
            
            // Generate internal token for microservice communication
            String internalToken = internalTokenService.generateInternalToken(userId, username, authorities);
            
            log.debug("Generated internal token for user: {}", username);
            
            // Forward internal token and user context to downstream services
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-Internal-Token", internalToken)
                    .header("X-User-Name", username)
                    .header("X-User-Authorities", authorities)
                    .header("X-User-Id", userId)
                    .build();
            
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
            
        } catch (Exception e) {
            log.error("Error processing JWT token: {}", e.getMessage());
            return unauthorizedResponse(exchange, "Error processing token");
        }
    }
    
    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/swagger-ui") || 
               path.startsWith("/v3/api-docs") || 
               path.startsWith("/actuator/health") ||
               path.startsWith("/user-management/auth/");
    }
    
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");
        
        String body = String.format("{\"error\": \"Unauthorized\", \"message\": \"%s\"}", message);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }
}
