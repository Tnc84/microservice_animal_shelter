package com.tnc.apigatewayas.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Security Headers Filter for API Gateway
 * Adds comprehensive security headers to prevent XSS, clickjacking, and other attacks
 */
@Slf4j
@Component
public class SecurityHeadersFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = response.getHeaders();
        
        // XSS Protection
        headers.add("X-XSS-Protection", "1; mode=block");
        headers.add("X-Content-Type-Options", "nosniff");
        
        // Clickjacking Protection
        headers.add("X-Frame-Options", "DENY");
        
        // Content Security Policy
        headers.add("Content-Security-Policy", 
            "default-src 'self'; " +
            "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
            "style-src 'self' 'unsafe-inline'; " +
            "img-src 'self' data: https:; " +
            "font-src 'self' data:; " +
            "connect-src 'self'; " +
            "frame-ancestors 'none'; " +
            "base-uri 'self'; " +
            "form-action 'self'");
        
        // Referrer Policy
        headers.add("Referrer-Policy", "strict-origin-when-cross-origin");
        
        // Permissions Policy
        headers.add("Permissions-Policy", 
            "geolocation=(), " +
            "microphone=(), " +
            "camera=(), " +
            "payment=(), " +
            "usb=(), " +
            "magnetometer=(), " +
            "gyroscope=(), " +
            "speaker=(), " +
            "vibrate=(), " +
            "fullscreen=(self), " +
            "sync-xhr=()");
        
        // Strict Transport Security (HTTPS only)
        headers.add("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");
        
        // Cache Control for sensitive endpoints
        String path = exchange.getRequest().getPath().value();
        if (path.contains("/auth/") || path.contains("/user-management/")) {
            headers.add("Cache-Control", "no-store, no-cache, must-revalidate, private");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
        }
        
        log.debug("Security headers added to response for path: {}", path);
        
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100; // High priority - execute early
    }
}
