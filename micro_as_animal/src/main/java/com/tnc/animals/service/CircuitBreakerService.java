package com.tnc.animals.service;

import com.tnc.animals.repository.entities.Animal;
import com.tnc.animals.repository.interfaces.AnimalRepository;
import com.tnc.resilience.service.BaseCircuitBreakerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service with circuit breaker patterns for database operations using TNC shared library.
 * Extends BaseCircuitBreakerService to inherit circuit breaker, retry, and timeout protection.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CircuitBreakerService extends BaseCircuitBreakerService<Animal, Long> {
    
    private final AnimalRepository animalRepository;
    
    @Override
    protected CrudRepository<Animal, Long> getRepository() {
        return animalRepository;
    }
    
    /**
     * Get all animals with circuit breaker protection.
     * Uses the inherited resilience patterns from BaseCircuitBreakerService.
     */
    public List<Animal> getAllAnimals() {
        log.info("Fetching all animals from database");
        return executeWithResilience(() -> animalRepository.findAll());
    }
    
    /**
     * Get animal by ID with circuit breaker protection.
     * Uses the inherited findById method from BaseCircuitBreakerService.
     */
    public Optional<Animal> getAnimalById(Long id) {
        log.info("Fetching animal by ID: {}", id);
        return findById(id);
    }
    
    /**
     * Save animal with circuit breaker protection.
     * Uses the inherited save method from BaseCircuitBreakerService.
     */
    public Animal saveAnimal(Animal animal) {
        log.info("Saving animal: {}", animal.getName());
        return save(animal);
    }
    
    /**
     * Delete animal with circuit breaker protection.
     * Uses the inherited deleteById method from BaseCircuitBreakerService.
     */
    public void deleteAnimal(Long id) {
        log.info("Deleting animal with ID: {}", id);
        deleteById(id);
    }
}
