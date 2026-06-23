package com.tnc.apigatewayas.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Input Sanitization Filter for API Gateway
 * Sanitizes and validates input to prevent XSS, SQL injection, and other attacks
 */
@Slf4j
@Component
public class InputSanitizationFilter implements GlobalFilter, Ordered {

    // Dangerous patterns for XSS and injection attacks
    private static final List<Pattern> DANGEROUS_PATTERNS = Arrays.asList(
        Pattern.compile("(?i)<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)<iframe[^>]*>.*?</iframe>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)javascript:", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)vbscript:", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)onload\\s*=", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)onerror\\s*=", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)onclick\\s*=", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)onmouseover\\s*=", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)eval\\s*\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)expression\\s*\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)alert\\s*\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)confirm\\s*\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)prompt\\s*\\(", Pattern.CASE_INSENSITIVE)
    );

    // SQL injection patterns
    private static final List<Pattern> SQL_INJECTION_PATTERNS = Arrays.asList(
        Pattern.compile("(?i)(union|select|insert|update|delete|drop|create|alter|exec|execute)\\s+", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)'\\s*(or|and)\\s*'", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i);\\s*drop\\s+table", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)--", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)/\\*.*?\\*/", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)waitfor\\s+delay", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)benchmark\\s*\\(", Pattern.CASE_INSENSITIVE)
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        
        // Skip sanitization for certain endpoints
        if (shouldSkipSanitization(path)) {
            return chain.filter(exchange);
        }
        
        try {
            // Check query parameters
            if (request.getQueryParams() != null && !request.getQueryParams().isEmpty()) {
                for (String paramName : request.getQueryParams().keySet()) {
                    List<String> paramValues = request.getQueryParams().get(paramName);
                    if (paramValues != null) {
                        for (String value : paramValues) {
                            if (containsDangerousPattern(value)) {
                                log.warn("Dangerous pattern detected in query parameter '{}': {}", paramName, value);
                                return createErrorResponse(exchange, "Invalid input detected");
                            }
                        }
                    }
                }
            }
            
            // Check headers for dangerous content
            HttpHeaders headers = request.getHeaders();
            for (String headerName : headers.keySet()) {
                List<String> headerValues = headers.get(headerName);
                if (headerValues != null) {
                    for (String value : headerValues) {
                        if (containsDangerousPattern(value)) {
                            log.warn("Dangerous pattern detected in header '{}': {}", headerName, value);
                            return createErrorResponse(exchange, "Invalid input detected");
                        }
                    }
                }
            }
            
            log.debug("Input sanitization passed for path: {}", path);
            
        } catch (Exception e) {
            log.error("Error during input sanitization: {}", e.getMessage());
            return createErrorResponse(exchange, "Input validation error");
        }
        
        return chain.filter(exchange);
    }

    private boolean shouldSkipSanitization(String path) {
        // Skip sanitization for static resources and health checks
        return path.startsWith("/actuator/") || 
               path.startsWith("/swagger-ui/") || 
               path.startsWith("/v3/api-docs/") ||
               path.contains("/favicon.ico");
    }

    private boolean containsDangerousPattern(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        
        try {
            // URL decode the input to check for encoded attacks
            String decodedInput = URLDecoder.decode(input, StandardCharsets.UTF_8);
            
            // Check for XSS patterns
            for (Pattern pattern : DANGEROUS_PATTERNS) {
                if (pattern.matcher(decodedInput).find()) {
                    return true;
                }
            }
            
            // Check for SQL injection patterns
            for (Pattern pattern : SQL_INJECTION_PATTERNS) {
                if (pattern.matcher(decodedInput).find()) {
                    return true;
                }
            }
            
        } catch (Exception e) {
            log.warn("Error decoding input: {}", e.getMessage());
            // If decoding fails, check the original input
            for (Pattern pattern : DANGEROUS_PATTERNS) {
                if (pattern.matcher(input).find()) {
                    return true;
                }
            }
        }
        
        return false;
    }

    private Mono<Void> createErrorResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.BAD_REQUEST);
        response.getHeaders().add("Content-Type", "application/json");
        
        String errorBody = String.format(
            "{\"error\":\"%s\",\"message\":\"%s\",\"timestamp\":\"%s\"}", 
            "VALIDATION_ERROR", 
            message, 
            java.time.Instant.now().toString()
        );
        
        return response.writeWith(
            Mono.just(response.bufferFactory().wrap(errorBody.getBytes()))
        );
    }

    @Override
    public int getOrder() {
        return -50; // Execute after security headers but before other filters
    }
}
