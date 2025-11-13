package com.tnc.shelter.service.impl;

import com.tnc.shelter.repository.entities.Shelter;
import com.tnc.shelter.service.CircuitBreakerService;
import com.tnc.shelter.service.domain.ShelterDomain;
import com.tnc.shelter.service.exception.ShelterAddressException;
import com.tnc.shelter.service.exception.ShelterNameException;
import com.tnc.shelter.service.mapper.ShelterDomainMapper;
import com.tnc.shelter.service.validation.ValidateShelter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    private CircuitBreakerService circuitBreakerService;

    @Mock
    private ShelterDomainMapper shelterDomainMapper;

    @InjectMocks
    private ShelterServiceImpl shelterService;

    private Shelter testShelter;
    private ShelterDomain testShelterDomain;

    @BeforeEach
    void setUp() {
        testShelter = new Shelter();
        testShelter.setId(1L);
        testShelter.setName("Test Shelter");
        testShelter.setCity("Test City");

        testShelterDomain = new ShelterDomain(1L, "Test Shelter", "Test City");
    }

    @Test
    void getShelterByName_ShouldReturnShelter() {
        // Arrange
        when(circuitBreakerService.findByName("Bucium")).thenReturn(Optional.of(testShelter));
        when(shelterDomainMapper.toDomain(testShelter)).thenReturn(testShelterDomain);

        // Act
        ShelterDomain result = shelterService.getShelterByName();

        // Assert
        assertNotNull(result);
        assertEquals("Test Shelter", result.getName());
        assertEquals("Test City", result.getCity());
        verify(circuitBreakerService).findByName("Bucium");
        verify(shelterDomainMapper).toDomain(testShelter);
    }

    @Test
    void getAll_ShouldReturnAllShelters() {
        // Arrange
        List<Shelter> shelters = Arrays.asList(testShelter);
        List<ShelterDomain> shelterDomains = Arrays.asList(testShelterDomain);
        
        when(circuitBreakerService.getAllShelters()).thenReturn(shelters);
        when(shelterDomainMapper.toDomainList(shelters)).thenReturn(shelterDomains);

        // Act
        List<ShelterDomain> result = shelterService.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(circuitBreakerService).getAllShelters();
        verify(shelterDomainMapper).toDomainList(shelters);
    }

    @Test
    void add_WithValidShelter_ShouldReturnAddedShelter() throws ShelterAddressException, ShelterNameException {
        // Arrange
        try (MockedStatic<ValidateShelter> mockedStatic = mockStatic(ValidateShelter.class)) {
            when(shelterDomainMapper.toEntity(testShelterDomain)).thenReturn(testShelter);
            when(circuitBreakerService.saveShelter(testShelter)).thenReturn(testShelter);
            when(shelterDomainMapper.toDomain(testShelter)).thenReturn(testShelterDomain);

            // Act
            ShelterDomain result = shelterService.add(testShelterDomain);

            // Assert
            assertNotNull(result);
            assertEquals("Test Shelter", result.getName());
            mockedStatic.verify(() -> ValidateShelter.validateShelter(testShelterDomain, "Test Shelter"));
            verify(shelterDomainMapper).toEntity(testShelterDomain);
            verify(circuitBreakerService).saveShelter(testShelter);
            verify(shelterDomainMapper).toDomain(testShelter);
        }
    }

    @Test
    void add_WithInvalidShelter_ShouldThrowException() throws ShelterAddressException, ShelterNameException {
        // Arrange
        try (MockedStatic<ValidateShelter> mockedStatic = mockStatic(ValidateShelter.class)) {
            mockedStatic.when(() -> ValidateShelter.validateShelter(any(ShelterDomain.class), anyString()))
                    .thenThrow(new ShelterNameException("Invalid name"));

            // Act & Assert
            assertThrows(ShelterNameException.class, () -> shelterService.add(testShelterDomain));
            mockedStatic.verify(() -> ValidateShelter.validateShelter(testShelterDomain, "Test Shelter"));
        }
    }

    @Test
    void update_ShouldReturnUpdatedShelter() {
        // Arrange
        when(shelterDomainMapper.toEntity(testShelterDomain)).thenReturn(testShelter);
        when(circuitBreakerService.saveShelter(testShelter)).thenReturn(testShelter);
        when(shelterDomainMapper.toDomain(testShelter)).thenReturn(testShelterDomain);

        // Act
        ShelterDomain result = shelterService.update(testShelterDomain);

        // Assert
        assertNotNull(result);
        assertEquals("Test Shelter", result.getName());
        verify(shelterDomainMapper).toEntity(testShelterDomain);
        verify(circuitBreakerService).saveShelter(testShelter);
        verify(shelterDomainMapper).toDomain(testShelter);
    }

}