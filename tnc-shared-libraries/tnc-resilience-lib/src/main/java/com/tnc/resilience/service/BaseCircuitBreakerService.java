package com.tnc.resilience.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import com.tnc.resilience.util.ResilienceExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Base service providing circuit breaker, retry, and timeout functionality for database operations.
 * This abstract class can be extended by microservices to add resilience patterns to their services.
 *
 * @param <T> the entity type
 * @param <ID> the ID type
 */
@Slf4j
public abstract class BaseCircuitBreakerService<T, ID> {

    @Autowired
    protected CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    protected RetryRegistry retryRegistry;

    @Autowired
    protected TimeLimiterRegistry timeLimiterRegistry;

    protected abstract CrudRepository<T, ID> getRepository();

    /**
     * Find entity by ID with circuit breaker, retry, and timeout protection.
     *
     * @param id the entity ID
     * @return Optional containing the entity if found
     */
    public Optional<T> findById(ID id) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("database");
        Retry retry = retryRegistry.retry("database");
        // TimeLimiter not applied for sync path

        Supplier<Optional<T>> supplier = () -> {
            log.debug("Attempting to find entity with ID: {}", id);
            return getRepository().findById(id);
        };

        Supplier<Optional<T>> decoratedSupplier = ResilienceExecutor
                .decorateSupplier(circuitBreaker, retry, supplier);
        return decoratedSupplier.get();
    }

    /**
     * Save entity with circuit breaker, retry, and timeout protection.
     *
     * @param entity the entity to save
     * @return the saved entity
     */
    public T save(T entity) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("database");
        Retry retry = retryRegistry.retry("database");
        // TimeLimiter not applied for sync path

        Supplier<T> supplier = () -> {
            log.debug("Attempting to save entity: {}", entity);
            return getRepository().save(entity);
        };

        Supplier<T> decoratedSupplier = ResilienceExecutor
                .decorateSupplier(circuitBreaker, retry, supplier);
        return decoratedSupplier.get();
    }

    /**
     * Delete entity by ID with circuit breaker, retry, and timeout protection.
     *
     * @param id the entity ID
     */
    public void deleteById(ID id) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("database");
        Retry retry = retryRegistry.retry("database");
        // TimeLimiter not applied for sync path

        Runnable runnable = () -> {
            log.debug("Attempting to delete entity with ID: {}", id);
            getRepository().deleteById(id);
        };

        Runnable decoratedRunnable = CircuitBreaker.decorateRunnable(circuitBreaker, runnable);
        decoratedRunnable = Retry.decorateRunnable(retry, decoratedRunnable);
        decoratedRunnable.run();
    }

    /**
     * Execute a custom operation with circuit breaker, retry, and timeout protection.
     *
     * @param operation the operation to execute
     * @param <R> the return type
     * @return the result of the operation
     */
    public <R> R executeDatabase(Supplier<R> operation) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("database");
        Retry retry = retryRegistry.retry("database");
        // TimeLimiter not applied for sync path

        Supplier<R> decoratedSupplier = ResilienceExecutor
                .decorateSupplier(circuitBreaker, retry, operation);
        return decoratedSupplier.get();
    }

    /**
     * Execute an async operation with circuit breaker, retry, and timeout protection.
     *
     * @param operation the async operation to execute
     * @param <R> the return type
     * @return CompletableFuture containing the result
     */
    public <R> CompletableFuture<R> executeDatabaseAsync(Supplier<CompletableFuture<R>> operation) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("database");
        Retry retry = retryRegistry.retry("database");
        TimeLimiter timeLimiter = timeLimiterRegistry.timeLimiter("database");

        Supplier<CompletableFuture<R>> decoratedSupplier = ResilienceExecutor
                .decorateAsync(timeLimiter, circuitBreaker, retry, operation);
        return decoratedSupplier.get();
    }
}
