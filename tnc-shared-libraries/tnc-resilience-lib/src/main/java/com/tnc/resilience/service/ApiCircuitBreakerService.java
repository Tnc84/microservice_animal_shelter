package com.tnc.resilience.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Service providing circuit breaker, retry, and timeout functionality for external API calls.
 * This service can be used by microservices to make resilient HTTP calls to external services.
 */
@Slf4j
@Service
public class ApiCircuitBreakerService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    private RetryRegistry retryRegistry;

    @Autowired
    private TimeLimiterRegistry timeLimiterRegistry;

    /**
     * Make a GET request with circuit breaker, retry, and timeout protection.
     *
     * @param url the URL to call
     * @param responseType the expected response type
     * @param <T> the response type
     * @return ResponseEntity containing the response
     */
    public <T> ResponseEntity<T> get(String url, Class<T> responseType) {
        return executeWithResilience("api", () -> {
            log.debug("Making GET request to: {}", url);
            return restTemplate.getForEntity(url, responseType);
        });
    }

    /**
     * Make a POST request with circuit breaker, retry, and timeout protection.
     *
     * @param url the URL to call
     * @param request the request body
     * @param responseType the expected response type
     * @param <T> the response type
     * @param <R> the request type
     * @return ResponseEntity containing the response
     */
    public <T, R> ResponseEntity<T> post(String url, R request, Class<T> responseType) {
        return executeWithResilience("api", () -> {
            log.debug("Making POST request to: {}", url);
            return restTemplate.postForEntity(url, request, responseType);
        });
    }

    /**
     * Make a PUT request with circuit breaker, retry, and timeout protection.
     *
     * @param url the URL to call
     * @param request the request body
     * @param responseType the expected response type
     * @param <T> the response type
     * @param <R> the request type
     * @return ResponseEntity containing the response
     */
    public <T, R> ResponseEntity<T> put(String url, R request, Class<T> responseType) {
        return executeWithResilience("api", () -> {
            log.debug("Making PUT request to: {}", url);
            HttpEntity<R> entity = new HttpEntity<>(request);
            return restTemplate.exchange(url, HttpMethod.PUT, entity, responseType);
        });
    }

    /**
     * Make a DELETE request with circuit breaker, retry, and timeout protection.
     *
     * @param url the URL to call
     * @param responseType the expected response type
     * @param <T> the response type
     * @return ResponseEntity containing the response
     */
    public <T> ResponseEntity<T> delete(String url, Class<T> responseType) {
        return executeWithResilience("api", () -> {
            log.debug("Making DELETE request to: {}", url);
            return restTemplate.exchange(url, HttpMethod.DELETE, null, responseType);
        });
    }

    /**
     * Execute a custom HTTP operation with circuit breaker, retry, and timeout protection.
     *
     * @param operation the operation to execute
     * @param <T> the return type
     * @return the result of the operation
     */
    public <T> T executeWithResilience(String circuitBreakerName, Supplier<T> operation) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(circuitBreakerName);
        Retry retry = retryRegistry.retry(circuitBreakerName);
        TimeLimiter timeLimiter = timeLimiterRegistry.timeLimiter(circuitBreakerName);

        Supplier<T> decoratedSupplier = CircuitBreaker
                .decorateSupplier(circuitBreaker, operation);
        decoratedSupplier = Retry.decorateSupplier(retry, decoratedSupplier);

        return decoratedSupplier.get();
    }

    /**
     * Execute an async HTTP operation with circuit breaker, retry, and timeout protection.
     *
     * @param operation the async operation to execute
     * @param <T> the return type
     * @return CompletableFuture containing the result
     */
    public <T> CompletableFuture<T> executeAsyncWithResilience(String circuitBreakerName, Supplier<CompletableFuture<T>> operation) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(circuitBreakerName);
        Retry retry = retryRegistry.retry(circuitBreakerName);
        TimeLimiter timeLimiter = timeLimiterRegistry.timeLimiter(circuitBreakerName);

        Supplier<CompletableFuture<T>> decoratedSupplier = CircuitBreaker
                .decorateSupplier(circuitBreaker, operation);
        decoratedSupplier = Retry.decorateSupplier(retry, decoratedSupplier);

        return decoratedSupplier.get();
    }
}
