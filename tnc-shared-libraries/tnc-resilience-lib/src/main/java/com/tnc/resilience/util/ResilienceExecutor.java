package com.tnc.resilience.util;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.timelimiter.TimeLimiter;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Small utility to compose Resilience4j decorators consistently.
 * Keeps services DRY and ensures TimeLimiter is applied for async paths.
 */
public final class ResilienceExecutor {

    private ResilienceExecutor() {
    }

    /**
     * Decorate a synchronous supplier with CircuitBreaker and Retry.
     */
    public static <T> Supplier<T> decorateSupplier(
            CircuitBreaker circuitBreaker,
            Retry retry,
            Supplier<T> supplier
    ) {
        Supplier<T> decorated = CircuitBreaker.decorateSupplier(circuitBreaker, supplier);
        return Retry.decorateSupplier(retry, decorated);
    }

    /**
     * Decorate an async supplier with CircuitBreaker, Retry, and TimeLimiter.
     */
    public static <T> Supplier<CompletableFuture<T>> decorateAsync(
            TimeLimiter timeLimiter,
            CircuitBreaker circuitBreaker,
            Retry retry,
            Supplier<CompletableFuture<T>> supplier
    ) {
        final Supplier<CompletableFuture<T>> decorated = Retry.decorateSupplier(
                retry,
                CircuitBreaker.decorateSupplier(circuitBreaker, supplier)
        );
        // Apply time limiter semantics using configured duration
        return () -> {
            CompletableFuture<T> future = decorated.get();
            java.time.Duration timeout = timeLimiter.getTimeLimiterConfig().getTimeoutDuration();
            future.orTimeout(timeout.toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS);
            return future;
        };
    }
}


