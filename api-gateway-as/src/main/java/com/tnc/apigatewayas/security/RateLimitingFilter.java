package com.tnc.apigatewayas.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
// import org.springframework.data.redis.core.RedisTemplate; // For future Redis implementation
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Rate Limiting Filter for API Gateway
 * Implements rate limiting and DDoS protection using sliding window algorithm
 */
@Slf4j
@Component
public class RateLimitingFilter implements GlobalFilter, Ordered {

    // In-memory rate limiting (for development)
    // In production, use Redis for distributed rate limiting
    private final ConcurrentHashMap<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();
    
    // Rate limiting configuration
    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private static final int MAX_REQUESTS_PER_HOUR = 1000;
    private static final int MAX_REQUESTS_PER_DAY = 10000;
    private static final int BURST_LIMIT = 10; // Max requests in 10 seconds
    private static final Duration BURST_WINDOW = Duration.ofSeconds(10);
    
    // Cleanup interval for expired entries
    private static final Duration CLEANUP_INTERVAL = Duration.ofMinutes(5);
    private volatile Instant lastCleanup = Instant.now();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String clientIp = getClientIpAddress(request);
        String path = request.getPath().value();
        
        // Skip rate limiting for certain endpoints
        if (shouldSkipRateLimiting(path)) {
            return chain.filter(exchange);
        }
        
        // Cleanup expired entries periodically
        cleanupExpiredEntries();
        
        // Check rate limits
        if (isRateLimited(clientIp, path)) {
            log.warn("Rate limit exceeded for IP: {} on path: {}", clientIp, path);
            return createRateLimitResponse(exchange);
        }
        
        // Update rate limit counters
        updateRateLimitCounters(clientIp);
        
        log.debug("Rate limiting passed for IP: {} on path: {}", clientIp, path);
        
        return chain.filter(exchange);
    }

    private boolean shouldSkipRateLimiting(String path) {
        // Skip rate limiting for health checks and static resources
        return path.startsWith("/actuator/health") || 
               path.startsWith("/swagger-ui/") || 
               path.startsWith("/v3/api-docs/") ||
               path.contains("/favicon.ico");
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

    private boolean isRateLimited(String clientIp, String path) {
        RateLimitInfo info = rateLimitMap.computeIfAbsent(clientIp, k -> new RateLimitInfo());
        Instant now = Instant.now();
        
        // Check burst limit (max requests in 10 seconds)
        if (info.isBurstLimited(now)) {
            return true;
        }
        
        // Check per-minute limit
        if (info.getRequestsLastMinute(now) > MAX_REQUESTS_PER_MINUTE) {
            return true;
        }
        
        // Check per-hour limit
        if (info.getRequestsLastHour(now) > MAX_REQUESTS_PER_HOUR) {
            return true;
        }
        
        // Check per-day limit
        if (info.getRequestsLastDay(now) > MAX_REQUESTS_PER_DAY) {
            return true;
        }
        
        return false;
    }

    private void updateRateLimitCounters(String clientIp) {
        RateLimitInfo info = rateLimitMap.computeIfAbsent(clientIp, k -> new RateLimitInfo());
        info.addRequest(Instant.now());
    }

    private void cleanupExpiredEntries() {
        Instant now = Instant.now();
        if (now.isAfter(lastCleanup.plus(CLEANUP_INTERVAL))) {
            rateLimitMap.entrySet().removeIf(entry -> 
                entry.getValue().isExpired(now));
            lastCleanup = now;
        }
    }

    private Mono<Void> createRateLimitResponse(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().add("Content-Type", "application/json");
        response.getHeaders().add("Retry-After", "60"); // Retry after 60 seconds
        
        String errorBody = String.format(
            "{\"error\":\"%s\",\"message\":\"%s\",\"retryAfter\":%d,\"timestamp\":\"%s\"}", 
            "RATE_LIMIT_EXCEEDED", 
            "Too many requests. Please try again later.",
            60,
            java.time.Instant.now().toString()
        );
        
        return response.writeWith(
            Mono.just(response.bufferFactory().wrap(errorBody.getBytes()))
        );
    }

    @Override
    public int getOrder() {
        return -25; // Execute after input sanitization
    }

    /**
     * Rate limit information for a client IP
     */
    private static class RateLimitInfo {
        private final ConcurrentHashMap<Instant, Integer> requestTimes = new ConcurrentHashMap<>();
        private volatile Instant lastBurstWindow = Instant.now();
        private volatile int burstCount = 0;

        public void addRequest(Instant now) {
            requestTimes.put(now, 1);
            
            // Update burst counter
            if (now.isAfter(lastBurstWindow.plus(BURST_WINDOW))) {
                burstCount = 1;
                lastBurstWindow = now;
            } else {
                burstCount++;
            }
        }

        public boolean isBurstLimited(Instant now) {
            if (now.isAfter(lastBurstWindow.plus(BURST_WINDOW))) {
                return false; // Reset burst window
            }
            return burstCount > BURST_LIMIT;
        }

        public int getRequestsLastMinute(Instant now) {
            return (int) requestTimes.keySet().stream()
                .filter(time -> time.isAfter(now.minus(Duration.ofMinutes(1))))
                .count();
        }

        public int getRequestsLastHour(Instant now) {
            return (int) requestTimes.keySet().stream()
                .filter(time -> time.isAfter(now.minus(Duration.ofHours(1))))
                .count();
        }

        public int getRequestsLastDay(Instant now) {
            return (int) requestTimes.keySet().stream()
                .filter(time -> time.isAfter(now.minus(Duration.ofDays(1))))
                .count();
        }

        public boolean isExpired(Instant now) {
            return requestTimes.keySet().stream()
                .allMatch(time -> time.isBefore(now.minus(Duration.ofDays(1))));
        }
    }
}
