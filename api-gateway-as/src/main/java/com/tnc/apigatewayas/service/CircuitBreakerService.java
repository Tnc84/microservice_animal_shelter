package com.tnc.apigatewayas.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Service with circuit breaker patterns for API Gateway.
 * Provides fallback mechanisms for microservice communication failures.
 */
@Slf4j
@Service
public class CircuitBreakerService {
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    /**
     * Call user service with circuit breaker protection.
     */
    @CircuitBreaker(name = "user-service", fallbackMethod = "callUserServiceFallback")
    @Retry(name = "user-service")
    @TimeLimiter(name = "user-service")
    public CompletableFuture<ResponseEntity<String>> callUserService(String url) {
        log.info("Calling user service: {}", url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return CompletableFuture.completedFuture(response);
    }
    
    /**
     * Call animal service with circuit breaker protection.
     */
    @CircuitBreaker(name = "animal-service", fallbackMethod = "callAnimalServiceFallback")
    @Retry(name = "animal-service")
    @TimeLimiter(name = "animal-service")
    public CompletableFuture<ResponseEntity<String>> callAnimalService(String url) {
        log.info("Calling animal service: {}", url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return CompletableFuture.completedFuture(response);
    }
    
    /**
     * Call shelter service with circuit breaker protection.
     */
    @CircuitBreaker(name = "shelter-service", fallbackMethod = "callShelterServiceFallback")
    @Retry(name = "shelter-service")
    @TimeLimiter(name = "shelter-service")
    public CompletableFuture<ResponseEntity<String>> callShelterService(String url) {
        log.info("Calling shelter service: {}", url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return CompletableFuture.completedFuture(response);
    }
    
    // Fallback methods
    public CompletableFuture<ResponseEntity<String>> callUserServiceFallback(String url, Exception ex) {
        log.warn("User service circuit breaker open, returning fallback response. Error: {}", ex.getMessage());
        Map<String, String> fallbackResponse = new HashMap<>();
        fallbackResponse.put("message", "User service is temporarily unavailable. Please try again later.");
        fallbackResponse.put("status", "SERVICE_UNAVAILABLE");
        return CompletableFuture.completedFuture(
            ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallbackResponse.toString())
        );
    }
    
    public CompletableFuture<ResponseEntity<String>> callAnimalServiceFallback(String url, Exception ex) {
        log.warn("Animal service circuit breaker open, returning fallback response. Error: {}", ex.getMessage());
        Map<String, String> fallbackResponse = new HashMap<>();
        fallbackResponse.put("message", "Animal service is temporarily unavailable. Please try again later.");
        fallbackResponse.put("status", "SERVICE_UNAVAILABLE");
        return CompletableFuture.completedFuture(
            ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallbackResponse.toString())
        );
    }
    
    public CompletableFuture<ResponseEntity<String>> callShelterServiceFallback(String url, Exception ex) {
        log.warn("Shelter service circuit breaker open, returning fallback response. Error: {}", ex.getMessage());
        Map<String, String> fallbackResponse = new HashMap<>();
        fallbackResponse.put("message", "Shelter service is temporarily unavailable. Please try again later.");
        fallbackResponse.put("status", "SERVICE_UNAVAILABLE");
        return CompletableFuture.completedFuture(
            ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallbackResponse.toString())
        );
    }
}
