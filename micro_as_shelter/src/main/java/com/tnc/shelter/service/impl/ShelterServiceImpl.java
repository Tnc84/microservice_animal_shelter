package com.tnc.shelter.service.impl;

import com.tnc.resilience.util.ResilienceExecutor;
import com.tnc.shelter.repository.interfaces.ShelterRepository;
import com.tnc.shelter.service.domain.ShelterDomain;
import com.tnc.shelter.service.exception.ShelterAddressException;
import com.tnc.shelter.service.exception.ShelterNameException;
import com.tnc.shelter.service.interfaces.ShelterService;
import com.tnc.shelter.service.mapper.ShelterDomainMapper;
import com.tnc.shelter.service.validation.ValidateShelter;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

/**
 * Shelter service implementation with integrated circuit breaker and retry patterns.
 * Works with domain models and uses mapper for entity conversion.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShelterServiceImpl implements ShelterService {

    private final ShelterRepository shelterRepository;
    private final ShelterDomainMapper shelterDomainMapper;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;

    /**
     * Execute a database operation with circuit breaker and retry protection.
     */
    private <R> R executeDatabase(Supplier<R> operation) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("database");
        Retry retry = retryRegistry.retry("database");
        Supplier<R> decoratedSupplier = ResilienceExecutor.decorateSupplier(circuitBreaker, retry, operation);
        return decoratedSupplier.get();
    }

    @Override
    public ShelterDomain getShelterByName() {
        log.debug("Getting shelter by name: Bucium");
        return executeDatabase(() -> 
            shelterDomainMapper.toDomain(shelterRepository.findByName("Bucium"))
        );
    }

    @Override
    public ShelterDomain getShelterById(Long id) {
        log.debug("Getting shelter by ID: {}", id);
        return executeDatabase(() -> 
            shelterDomainMapper.toDomain(shelterRepository.findById(id).orElse(null))
        );
    }

    @Override
    public List<ShelterDomain> getAll() {
        log.debug("Getting all shelters");
        return executeDatabase(() -> 
            shelterDomainMapper.toDomainList(shelterRepository.findAll())
        );
    }

    @Override
    public ShelterDomain add(ShelterDomain shelterDomain) throws ShelterAddressException, ShelterNameException {
        log.info("Adding new shelter: {}", shelterDomain.getName());
        ValidateShelter.validateShelter(shelterDomain, shelterDomain.getName());
        
        return executeDatabase(() -> {
            var shelter = shelterDomainMapper.toEntity(shelterDomain);
            var savedShelter = shelterRepository.save(shelter);
            log.info("Shelter created with ID: {}", savedShelter.getId());
            return shelterDomainMapper.toDomain(savedShelter);
        });
    }

    @Override
    public ShelterDomain update(ShelterDomain shelterDomain) {
        log.info("Updating shelter: {}", shelterDomain.getName());
        
        return executeDatabase(() -> {
            var shelter = shelterDomainMapper.toEntity(shelterDomain);
            var updatedShelter = shelterRepository.save(shelter);
            log.info("Shelter updated: {}", updatedShelter.getId());
            return shelterDomainMapper.toDomain(updatedShelter);
        });
    }

    public ShelterDomain findByName(String name) {
        log.debug("Finding shelter by name: {}", name);
        return executeDatabase(() -> 
            shelterDomainMapper.toDomain(shelterRepository.findByName(name))
        );
    }

    public void delete(Long id) {
        log.info("Deleting shelter with ID: {}", id);
        executeDatabase(() -> {
            shelterRepository.deleteById(id);
            log.info("Shelter deleted: {}", id);
            return null;
        });
    }
}
