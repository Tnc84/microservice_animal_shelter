package com.tnc.animals.service;

import com.tnc.animals.repository.entities.Animal;
import com.tnc.animals.repository.interfaces.AnimalRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Service with circuit breaker patterns for database operations.
 * Provides fallback mechanisms for database failures.
 */
@Slf4j
// @Service  // Temporarily disabled to fix startup issue
@RequiredArgsConstructor
public class CircuitBreakerService {
    
    private final AnimalRepository animalRepository;
    
    /**
     * Get all animals with circuit breaker protection.
     */
    @CircuitBreaker(name = "database", fallbackMethod = "getAllAnimalsFallback")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public CompletableFuture<List<Animal>> getAllAnimals() {
        log.info("Fetching all animals from database");
        return CompletableFuture.completedFuture(animalRepository.findAll());
    }
    
    /**
     * Get animal by ID with circuit breaker protection.
     */
    @CircuitBreaker(name = "database", fallbackMethod = "getAnimalByIdFallback")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public CompletableFuture<Optional<Animal>> getAnimalById(Long id) {
        log.info("Fetching animal by ID: {}", id);
        return CompletableFuture.completedFuture(animalRepository.findById(id));
    }
    
    /**
     * Save animal with circuit breaker protection.
     */
    @CircuitBreaker(name = "database", fallbackMethod = "saveAnimalFallback")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public CompletableFuture<Animal> saveAnimal(Animal animal) {
        log.info("Saving animal: {}", animal.getName());
        return CompletableFuture.completedFuture(animalRepository.save(animal));
    }
    
    /**
     * Delete animal with circuit breaker protection.
     */
    @CircuitBreaker(name = "database", fallbackMethod = "deleteAnimalFallback")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public CompletableFuture<Boolean> deleteAnimal(Long id) {
        log.info("Deleting animal with ID: {}", id);
        try {
            animalRepository.deleteById(id);
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(false);
        }
    }
    
    // Fallback methods
    public CompletableFuture<List<Animal>> getAllAnimalsFallback(Exception ex) {
        log.warn("Database circuit breaker open, returning empty list for getAllAnimals. Error: {}", ex.getMessage());
        return CompletableFuture.completedFuture(Collections.emptyList());
    }
    
    public CompletableFuture<Optional<Animal>> getAnimalByIdFallback(Long id, Exception ex) {
        log.warn("Database circuit breaker open, returning empty for animal ID {}. Error: {}", id, ex.getMessage());
        return CompletableFuture.completedFuture(Optional.empty());
    }
    
    public CompletableFuture<Animal> saveAnimalFallback(Animal animal, Exception ex) {
        log.warn("Database circuit breaker open, returning null for save. Error: {}", ex.getMessage());
        return CompletableFuture.completedFuture(null);
    }
    
    public CompletableFuture<Boolean> deleteAnimalFallback(Long id, Exception ex) {
        log.warn("Database circuit breaker open, returning false for delete. Error: {}", ex.getMessage());
        return CompletableFuture.completedFuture(false);
    }
}
