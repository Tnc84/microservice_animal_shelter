package com.tnc.shelter.service;

import com.tnc.shelter.repository.entities.Shelter;
import com.tnc.shelter.repository.interfaces.ShelterRepository;
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
@Service
@RequiredArgsConstructor
public class CircuitBreakerService {
    
    private final ShelterRepository shelterRepository;
    
    /**
     * Get all shelters with circuit breaker protection.
     */
    @CircuitBreaker(name = "database", fallbackMethod = "getAllSheltersFallback")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public CompletableFuture<List<Shelter>> getAllShelters() {
        log.info("Fetching all shelters from database");
        return CompletableFuture.completedFuture(shelterRepository.findAll());
    }
    
    /**
     * Get shelter by ID with circuit breaker protection.
     */
    @CircuitBreaker(name = "database", fallbackMethod = "getShelterByIdFallback")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public CompletableFuture<Optional<Shelter>> getShelterById(Long id) {
        log.info("Fetching shelter by ID: {}", id);
        return CompletableFuture.completedFuture(shelterRepository.findById(id));
    }
    
    /**
     * Save shelter with circuit breaker protection.
     */
    @CircuitBreaker(name = "database", fallbackMethod = "saveShelterFallback")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public CompletableFuture<Shelter> saveShelter(Shelter shelter) {
        log.info("Saving shelter: {}", shelter.getName());
        return CompletableFuture.completedFuture(shelterRepository.save(shelter));
    }
    
    /**
     * Delete shelter with circuit breaker protection.
     */
    @CircuitBreaker(name = "database", fallbackMethod = "deleteShelterFallback")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public CompletableFuture<Boolean> deleteShelter(Long id) {
        log.info("Deleting shelter with ID: {}", id);
        try {
            shelterRepository.deleteById(id);
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(false);
        }
    }
    
    // Fallback methods
    public CompletableFuture<List<Shelter>> getAllSheltersFallback(Exception ex) {
        log.warn("Database circuit breaker open, returning empty list for getAllShelters. Error: {}", ex.getMessage());
        return CompletableFuture.completedFuture(Collections.emptyList());
    }
    
    public CompletableFuture<Optional<Shelter>> getShelterByIdFallback(Long id, Exception ex) {
        log.warn("Database circuit breaker open, returning empty for shelter ID {}. Error: {}", id, ex.getMessage());
        return CompletableFuture.completedFuture(Optional.empty());
    }
    
    public CompletableFuture<Shelter> saveShelterFallback(Shelter shelter, Exception ex) {
        log.warn("Database circuit breaker open, returning null for save. Error: {}", ex.getMessage());
        return CompletableFuture.completedFuture(null);
    }
    
    public CompletableFuture<Boolean> deleteShelterFallback(Long id, Exception ex) {
        log.warn("Database circuit breaker open, returning false for delete. Error: {}", ex.getMessage());
        return CompletableFuture.completedFuture(false);
    }
}
