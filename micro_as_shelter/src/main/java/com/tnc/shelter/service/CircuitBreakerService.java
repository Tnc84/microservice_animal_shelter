package com.tnc.shelter.service;

import com.tnc.shelter.repository.entities.Shelter;
import com.tnc.shelter.repository.interfaces.ShelterRepository;
import com.tnc.resilience.service.BaseCircuitBreakerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service with circuit breaker patterns for database operations.
 * Provides fallback mechanisms for database failures.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CircuitBreakerService extends BaseCircuitBreakerService<Shelter, Long>{
    
    private final ShelterRepository shelterRepository;
    
    @Override
    protected CrudRepository<Shelter, Long> getRepository() {
        return shelterRepository;
    }

    /**
     * Find shelter by name with database circuit breaker protection.
     */
    public Optional<Shelter> findByName(String name) {
        return executeDatabase(() -> Optional.ofNullable(shelterRepository.findByName(name)));
    }

    public List<Shelter> getAllShelters() {
        return executeDatabase(() -> shelterRepository.findAll());
    }

    public Shelter saveShelter(Shelter shelter) {
        return save(shelter);
    }

    public void deleteShelter(Long id) {
        deleteById(id);
    }
    
    // Annotation-based methods and fallbacks removed; using shared base resilience instead
}
