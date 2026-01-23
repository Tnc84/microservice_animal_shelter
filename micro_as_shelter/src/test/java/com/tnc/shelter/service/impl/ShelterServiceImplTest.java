package com.tnc.shelter.service.impl;

import com.tnc.resilience.util.ResilienceExecutor;
import com.tnc.shelter.repository.entities.Shelter;
import com.tnc.shelter.repository.interfaces.ShelterRepository;
import com.tnc.shelter.service.domain.ShelterDomain;
import com.tnc.shelter.service.exception.ShelterAddressException;
import com.tnc.shelter.service.exception.ShelterNameException;
import com.tnc.shelter.service.mapper.ShelterDomainMapper;
import com.tnc.shelter.service.validation.ValidateShelter;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ShelterServiceImpl
 * Tests business logic, validation, and error handling
 */
@ExtendWith(MockitoExtension.class)
class ShelterServiceImplTest {

    @Mock
    private ShelterRepository shelterRepository;

    @Mock
    private ShelterDomainMapper shelterDomainMapper;

    @Mock
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Mock
    private RetryRegistry retryRegistry;

    @Mock
    private CircuitBreaker circuitBreaker;

    @Mock
    private Retry retry;

    private ShelterServiceImpl shelterService;

    private Shelter testShelter;
    private ShelterDomain testShelterDomain;

    @BeforeEach
    void setUp() {
        // Setup resilience mocks
        when(circuitBreakerRegistry.circuitBreaker("database")).thenReturn(circuitBreaker);
        when(retryRegistry.retry("database")).thenReturn(retry);

        shelterService = new ShelterServiceImpl(
                shelterRepository, shelterDomainMapper,
                circuitBreakerRegistry, retryRegistry);

        testShelter = new Shelter();
        testShelter.setId(1L);
        testShelter.setName("Test Shelter");
        testShelter.setCity("Test City");

        testShelterDomain = new ShelterDomain(1L, "Test Shelter", "Test City");
    }

    @Test
    void getShelterByName_ShouldReturnShelter() {
        // Arrange & Act & Assert with mocked static
        try (MockedStatic<ResilienceExecutor> mockedExecutor = mockStatic(ResilienceExecutor.class)) {
            mockedExecutor.when(() -> ResilienceExecutor.decorateSupplier(any(), any(), any()))
                    .thenAnswer(inv -> inv.getArgument(2));

            when(shelterRepository.findByName("Bucium")).thenReturn(testShelter);
            when(shelterDomainMapper.toDomain(testShelter)).thenReturn(testShelterDomain);

            ShelterDomain result = shelterService.getShelterByName();

            assertNotNull(result);
            assertEquals("Test Shelter", result.getName());
            assertEquals("Test City", result.getCity());
            verify(shelterRepository).findByName("Bucium");
            verify(shelterDomainMapper).toDomain(testShelter);
        }
    }

    @Test
    void getAll_ShouldReturnAllShelters() {
        try (MockedStatic<ResilienceExecutor> mockedExecutor = mockStatic(ResilienceExecutor.class)) {
            mockedExecutor.when(() -> ResilienceExecutor.decorateSupplier(any(), any(), any()))
                    .thenAnswer(inv -> inv.getArgument(2));

            List<Shelter> shelters = Arrays.asList(testShelter);
            List<ShelterDomain> shelterDomains = Arrays.asList(testShelterDomain);

            when(shelterRepository.findAll()).thenReturn(shelters);
            when(shelterDomainMapper.toDomainList(shelters)).thenReturn(shelterDomains);

            List<ShelterDomain> result = shelterService.getAll();

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(shelterRepository).findAll();
            verify(shelterDomainMapper).toDomainList(shelters);
        }
    }

    @Test
    void add_WithValidShelter_ShouldReturnAddedShelter() throws ShelterAddressException, ShelterNameException {
        try (MockedStatic<ResilienceExecutor> mockedExecutor = mockStatic(ResilienceExecutor.class);
             MockedStatic<ValidateShelter> mockedValidator = mockStatic(ValidateShelter.class)) {

            mockedExecutor.when(() -> ResilienceExecutor.decorateSupplier(any(), any(), any()))
                    .thenAnswer(inv -> inv.getArgument(2));

            when(shelterDomainMapper.toEntity(testShelterDomain)).thenReturn(testShelter);
            when(shelterRepository.save(testShelter)).thenReturn(testShelter);
            when(shelterDomainMapper.toDomain(testShelter)).thenReturn(testShelterDomain);

            ShelterDomain result = shelterService.add(testShelterDomain);

            assertNotNull(result);
            assertEquals("Test Shelter", result.getName());
            mockedValidator.verify(() -> ValidateShelter.validateShelter(testShelterDomain, "Test Shelter"));
            verify(shelterDomainMapper).toEntity(testShelterDomain);
            verify(shelterRepository).save(testShelter);
            verify(shelterDomainMapper).toDomain(testShelter);
        }
    }

    @Test
    void add_WithInvalidShelter_ShouldThrowException() {
        try (MockedStatic<ValidateShelter> mockedValidator = mockStatic(ValidateShelter.class)) {
            mockedValidator.when(() -> ValidateShelter.validateShelter(any(ShelterDomain.class), anyString()))
                    .thenThrow(new ShelterNameException("Invalid name"));

            assertThrows(ShelterNameException.class, () -> shelterService.add(testShelterDomain));
            mockedValidator.verify(() -> ValidateShelter.validateShelter(testShelterDomain, "Test Shelter"));
        }
    }

    @Test
    void update_ShouldReturnUpdatedShelter() {
        try (MockedStatic<ResilienceExecutor> mockedExecutor = mockStatic(ResilienceExecutor.class)) {
            mockedExecutor.when(() -> ResilienceExecutor.decorateSupplier(any(), any(), any()))
                    .thenAnswer(inv -> inv.getArgument(2));

            when(shelterDomainMapper.toEntity(testShelterDomain)).thenReturn(testShelter);
            when(shelterRepository.save(testShelter)).thenReturn(testShelter);
            when(shelterDomainMapper.toDomain(testShelter)).thenReturn(testShelterDomain);

            ShelterDomain result = shelterService.update(testShelterDomain);

            assertNotNull(result);
            assertEquals("Test Shelter", result.getName());
            verify(shelterDomainMapper).toEntity(testShelterDomain);
            verify(shelterRepository).save(testShelter);
            verify(shelterDomainMapper).toDomain(testShelter);
        }
    }
}
