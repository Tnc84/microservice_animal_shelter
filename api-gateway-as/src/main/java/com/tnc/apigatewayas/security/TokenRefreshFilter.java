package com.tnc.apigatewayas.security;

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
 * Global filter to handle token refresh for expired access tokens
 */
@Slf4j
@Component
public class TokenRefreshFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        
        // If no authorization header, continue to next filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }
        
        String token = authHeader.substring(7);
        
        // Check if token is expired (this would need JWT validation logic)
        // For now, we'll let the downstream service handle token validation
        // In a production environment, you might want to validate the token here
        
        // Add token to request headers for downstream services
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-User-Token", token)
                .build();
        
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }
}
