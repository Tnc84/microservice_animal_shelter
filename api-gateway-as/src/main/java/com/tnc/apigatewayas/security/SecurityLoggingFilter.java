package com.tnc.apigatewayas.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
// import org.springframework.http.server.reactive.ServerHttpResponse; // Not used in this filter
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Security Logging Filter for API Gateway
 * Logs security events, suspicious activities, and request patterns
 */
@Slf4j
@Component
public class SecurityLoggingFilter implements GlobalFilter, Ordered {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneOffset.UTC);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String clientIp = getClientIpAddress(request);
        String userAgent = request.getHeaders().getFirst(HttpHeaders.USER_AGENT);
        String path = request.getPath().value();
        String method = request.getMethod().name();
        
        // Log all requests for security monitoring
        logSecurityEvent("REQUEST_RECEIVED", clientIp, path, method, 
            Map.of("userAgent", userAgent != null ? userAgent : "unknown"));
        
        // Check for suspicious patterns
        if (isSuspiciousRequest(request)) {
            logSecurityEvent("SUSPICIOUS_REQUEST", clientIp, path, method, 
                Map.of("userAgent", userAgent != null ? userAgent : "unknown",
                       "reason", "Suspicious request pattern detected"));
        }
        
        // Check for potential bot activity
        if (isPotentialBot(userAgent)) {
            logSecurityEvent("POTENTIAL_BOT", clientIp, path, method, 
                Map.of("userAgent", userAgent != null ? userAgent : "unknown"));
        }
        
        return chain.filter(exchange).doOnSuccess(aVoid -> {
            // Log successful response
            logSecurityEvent("REQUEST_SUCCESS", clientIp, path, method, null);
        }).doOnError(throwable -> {
            // Log error response
            logSecurityEvent("REQUEST_ERROR", clientIp, path, method, 
                Map.of("error", throwable.getMessage()));
        });
    }

    private String getClientIpAddress(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddress() != null ? 
               request.getRemoteAddress().getAddress().getHostAddress() : "127.0.0.1";
    }

    private boolean isSuspiciousRequest(ServerHttpRequest request) {
        String path = request.getPath().value();
        String method = request.getMethod().name();
        
        // Check for common attack patterns
        String lowerPath = path.toLowerCase();
        
        // Directory traversal attempts
        if (lowerPath.contains("../") || lowerPath.contains("..\\")) {
            return true;
        }
        
        // SQL injection attempts in path
        if (lowerPath.contains("union") || lowerPath.contains("select") || 
            lowerPath.contains("insert") || lowerPath.contains("delete")) {
            return true;
        }
        
        // XSS attempts in path
        if (lowerPath.contains("<script") || lowerPath.contains("javascript:") || 
            lowerPath.contains("vbscript:")) {
            return true;
        }
        
        // Suspicious file extensions
        if (lowerPath.endsWith(".exe") || lowerPath.endsWith(".bat") || 
            lowerPath.endsWith(".cmd") || lowerPath.endsWith(".sh")) {
            return true;
        }
        
        // Unusual HTTP methods
        if (!List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD").contains(method)) {
            return true;
        }
        
        return false;
    }

    private boolean isPotentialBot(String userAgent) {
        if (userAgent == null || userAgent.trim().isEmpty()) {
            return true; // No user agent is suspicious
        }
        
        String lowerUserAgent = userAgent.toLowerCase();
        
        // Known bot patterns
        String[] botPatterns = {
            "bot", "crawler", "spider", "scraper", "curl", "wget", 
            "python-requests", "java/", "go-http", "okhttp"
        };
        
        for (String pattern : botPatterns) {
            if (lowerUserAgent.contains(pattern)) {
                return true;
            }
        }
        
        // Very short user agents are suspicious
        if (userAgent.length() < 10) {
            return true;
        }
        
        return false;
    }

    private void logSecurityEvent(String eventType, String clientIp, String path, 
                                 String method, Map<String, String> additionalInfo) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("SECURITY_EVENT: ").append(eventType);
        logMessage.append(" | IP: ").append(clientIp);
        logMessage.append(" | Path: ").append(path);
        logMessage.append(" | Method: ").append(method);
        logMessage.append(" | Timestamp: ").append(TIMESTAMP_FORMATTER.format(Instant.now()));
        
        if (additionalInfo != null && !additionalInfo.isEmpty()) {
            logMessage.append(" | Details: ");
            additionalInfo.forEach((key, value) -> 
                logMessage.append(key).append("=").append(value).append(" "));
        }
        
        // Use appropriate log level based on event type
        switch (eventType) {
            case "SUSPICIOUS_REQUEST":
            case "POTENTIAL_BOT":
                log.warn(logMessage.toString());
                break;
            case "REQUEST_ERROR":
                log.error(logMessage.toString());
                break;
            default:
                log.info(logMessage.toString());
                break;
        }
    }

    @Override
    public int getOrder() {
        return -10; // Execute after rate limiting
    }
}
