package com.tnc.userManagement.controller;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for monitoring circuit breaker status.
 * Provides endpoints to check circuit breaker health and metrics.
 */
@Slf4j
@RestController
@RequestMapping("/circuit-breaker")
@RequiredArgsConstructor
public class CircuitBreakerController {
    
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;
    private final TimeLimiterRegistry timeLimiterRegistry;
    
    /**
     * Get circuit breaker status for all instances.
     */
    @GetMapping("/status")
    @PreAuthorize("hasRole('INTERNAL_SERVICE')")
    public ResponseEntity<Map<String, Object>> getCircuitBreakerStatus() {
        log.info("Retrieving circuit breaker status");
        
        Map<String, Object> status = new HashMap<>();
        
        // Database circuit breaker status
        CircuitBreaker databaseCircuitBreaker = circuitBreakerRegistry.circuitBreaker("database");
        Map<String, Object> databaseStatus = new HashMap<>();
        databaseStatus.put("state", databaseCircuitBreaker.getState());
        databaseStatus.put("failureRate", databaseCircuitBreaker.getMetrics().getFailureRate());
        databaseStatus.put("numberOfBufferedCalls", databaseCircuitBreaker.getMetrics().getNumberOfBufferedCalls());
        databaseStatus.put("numberOfSuccessfulCalls", databaseCircuitBreaker.getMetrics().getNumberOfSuccessfulCalls());
        databaseStatus.put("numberOfFailedCalls", databaseCircuitBreaker.getMetrics().getNumberOfFailedCalls());
        status.put("database", databaseStatus);
        
        // RabbitMQ circuit breaker status
        CircuitBreaker rabbitmqCircuitBreaker = circuitBreakerRegistry.circuitBreaker("rabbitmq");
        Map<String, Object> rabbitmqStatus = new HashMap<>();
        rabbitmqStatus.put("state", rabbitmqCircuitBreaker.getState());
        rabbitmqStatus.put("failureRate", rabbitmqCircuitBreaker.getMetrics().getFailureRate());
        rabbitmqStatus.put("numberOfBufferedCalls", rabbitmqCircuitBreaker.getMetrics().getNumberOfBufferedCalls());
        rabbitmqStatus.put("numberOfSuccessfulCalls", rabbitmqCircuitBreaker.getMetrics().getNumberOfSuccessfulCalls());
        rabbitmqStatus.put("numberOfFailedCalls", rabbitmqCircuitBreaker.getMetrics().getNumberOfFailedCalls());
        status.put("rabbitmq", rabbitmqStatus);
        
        return ResponseEntity.ok(status);
    }
    
    /**
     * Get retry status for all instances.
     */
    @GetMapping("/retry/status")
    @PreAuthorize("hasRole('INTERNAL_SERVICE')")
    public ResponseEntity<Map<String, Object>> getRetryStatus() {
        log.info("Retrieving retry status");
        
        Map<String, Object> status = new HashMap<>();
        
        // Database retry status
        Retry databaseRetry = retryRegistry.retry("database");
        Map<String, Object> databaseRetryStatus = new HashMap<>();
        databaseRetryStatus.put("numberOfSuccessfulCallsWithoutRetryAttempt", 
                databaseRetry.getMetrics().getNumberOfSuccessfulCallsWithoutRetryAttempt());
        databaseRetryStatus.put("numberOfSuccessfulCallsWithRetryAttempt", 
                databaseRetry.getMetrics().getNumberOfSuccessfulCallsWithRetryAttempt());
        databaseRetryStatus.put("numberOfFailedCallsWithoutRetryAttempt", 
                databaseRetry.getMetrics().getNumberOfFailedCallsWithoutRetryAttempt());
        databaseRetryStatus.put("numberOfFailedCallsWithRetryAttempt", 
                databaseRetry.getMetrics().getNumberOfFailedCallsWithRetryAttempt());
        status.put("database", databaseRetryStatus);
        
        // RabbitMQ retry status
        Retry rabbitmqRetry = retryRegistry.retry("rabbitmq");
        Map<String, Object> rabbitmqRetryStatus = new HashMap<>();
        rabbitmqRetryStatus.put("numberOfSuccessfulCallsWithoutRetryAttempt", 
                rabbitmqRetry.getMetrics().getNumberOfSuccessfulCallsWithoutRetryAttempt());
        rabbitmqRetryStatus.put("numberOfSuccessfulCallsWithRetryAttempt", 
                rabbitmqRetry.getMetrics().getNumberOfSuccessfulCallsWithRetryAttempt());
        rabbitmqRetryStatus.put("numberOfFailedCallsWithoutRetryAttempt", 
                rabbitmqRetry.getMetrics().getNumberOfFailedCallsWithoutRetryAttempt());
        rabbitmqRetryStatus.put("numberOfFailedCallsWithRetryAttempt", 
                rabbitmqRetry.getMetrics().getNumberOfFailedCallsWithRetryAttempt());
        status.put("rabbitmq", rabbitmqRetryStatus);
        
        return ResponseEntity.ok(status);
    }
    
    /**
     * Get time limiter status for all instances.
     */
    @GetMapping("/time-limiter/status")
    @PreAuthorize("hasRole('INTERNAL_SERVICE')")
    public ResponseEntity<Map<String, Object>> getTimeLimiterStatus() {
        log.info("Retrieving time limiter status");
        
        Map<String, Object> status = new HashMap<>();
        
        // Database time limiter status
        TimeLimiter databaseTimeLimiter = timeLimiterRegistry.timeLimiter("database");
        Map<String, Object> databaseTimeLimiterStatus = new HashMap<>();
        databaseTimeLimiterStatus.put("timeoutDuration", databaseTimeLimiter.getTimeLimiterConfig().getTimeoutDuration());
        status.put("database", databaseTimeLimiterStatus);
        
        // RabbitMQ time limiter status
        TimeLimiter rabbitmqTimeLimiter = timeLimiterRegistry.timeLimiter("rabbitmq");
        Map<String, Object> rabbitmqTimeLimiterStatus = new HashMap<>();
        rabbitmqTimeLimiterStatus.put("timeoutDuration", rabbitmqTimeLimiter.getTimeLimiterConfig().getTimeoutDuration());
        status.put("rabbitmq", rabbitmqTimeLimiterStatus);
        
        return ResponseEntity.ok(status);
    }
    
    /**
     * Get overall health status of all circuit breakers.
     */
    @GetMapping("/health")
    @PreAuthorize("hasRole('INTERNAL_SERVICE')")
    public ResponseEntity<Map<String, String>> getHealth() {
        log.info("Retrieving circuit breaker health");
        
        Map<String, String> health = new HashMap<>();
        
        CircuitBreaker databaseCircuitBreaker = circuitBreakerRegistry.circuitBreaker("database");
        CircuitBreaker rabbitmqCircuitBreaker = circuitBreakerRegistry.circuitBreaker("rabbitmq");
        
        boolean allHealthy = databaseCircuitBreaker.getState() == CircuitBreaker.State.CLOSED && 
                            rabbitmqCircuitBreaker.getState() == CircuitBreaker.State.CLOSED;
        
        health.put("overall", allHealthy ? "HEALTHY" : "UNHEALTHY");
        health.put("database", databaseCircuitBreaker.getState().toString());
        health.put("rabbitmq", rabbitmqCircuitBreaker.getState().toString());
        
        return ResponseEntity.ok(health);
    }
}
