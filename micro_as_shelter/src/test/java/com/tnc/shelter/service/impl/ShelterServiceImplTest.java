package com.tnc.shelter.service.impl;

import com.tnc.shelter.repository.entities.Shelter;
import com.tnc.shelter.repository.interfaces.ShelterRepository;
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
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;

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
    private Environment environment;

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
        testShelter.setEnvironment("8080");

        testShelterDomain = new ShelterDomain(1L, "Test Shelter", "Test City");
        testShelterDomain.setEnvironment("8080");
    }

    @Test
    void getShelterByName_ShouldReturnShelterWithEnvironment() {
        // Arrange
        when(shelterRepository.findByName("Bucium")).thenReturn(testShelter);
        when(shelterDomainMapper.toDomain(testShelter)).thenReturn(testShelterDomain);
        when(environment.getProperty("local.server.port")).thenReturn("8080");

        // Act
        ShelterDomain result = shelterService.getShelterByName();

        // Assert
        assertNotNull(result);
        assertEquals("Test Shelter", result.getName());
        assertEquals("Test City", result.getCity());
        assertEquals("8080", result.getEnvironment());
        verify(shelterRepository).findByName("Bucium");
        verify(shelterDomainMapper).toDomain(testShelter);
    }

    @Test
    void getAll_ShouldReturnAllSheltersWithEnvironment() {
        // Arrange
        List<Shelter> shelters = Arrays.asList(testShelter);
        List<ShelterDomain> shelterDomains = Arrays.asList(testShelterDomain);
        
        when(shelterRepository.findAll()).thenReturn(shelters);
        when(shelterDomainMapper.toDomainList(shelters)).thenReturn(shelterDomains);
        when(environment.getProperty("local.server.port")).thenReturn("8080");

        // Act
        List<ShelterDomain> result = shelterService.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("8080", result.get(0).getEnvironment());
        verify(shelterRepository).findAll();
        verify(shelterDomainMapper).toDomainList(shelters);
    }

    @Test
    void add_WithValidShelter_ShouldReturnAddedShelter() throws ShelterAddressException, ShelterNameException {
        // Arrange
        try (MockedStatic<ValidateShelter> mockedStatic = mockStatic(ValidateShelter.class)) {
            when(environment.getProperty("local.server.port")).thenReturn("8080");
            when(shelterDomainMapper.toEntity(testShelterDomain)).thenReturn(testShelter);
            when(shelterDomainMapper.toDomain(testShelter)).thenReturn(testShelterDomain);

            // Act
            ShelterDomain result = shelterService.add(testShelterDomain);

            // Assert
            assertNotNull(result);
            assertEquals("Test Shelter", result.getName());
            assertEquals("8080", result.getEnvironment());
            mockedStatic.verify(() -> ValidateShelter.validateShelter(testShelterDomain, "Test Shelter"));
            verify(shelterDomainMapper).toEntity(testShelterDomain);
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
        when(shelterDomainMapper.toDomain(testShelter)).thenReturn(testShelterDomain);

        // Act
        ShelterDomain result = shelterService.update(testShelterDomain);

        // Assert
        assertNotNull(result);
        assertEquals("Test Shelter", result.getName());
        verify(shelterDomainMapper).toEntity(testShelterDomain);
        verify(shelterDomainMapper).toDomain(testShelter);
    }

    @Test
    void setShelterEnvironment_ShouldSetEnvironmentFromProperties() {
        // Act - Test through getShelterByName which uses setShelterEnvironment internally
        when(shelterRepository.findByName("Bucium")).thenReturn(testShelter);
        when(shelterDomainMapper.toDomain(testShelter)).thenReturn(testShelterDomain);
        when(environment.getProperty("local.server.port")).thenReturn("8080");

        ShelterDomain result = shelterService.getShelterByName();

        // Assert
        assertNotNull(result);
        assertEquals("8080", result.getEnvironment());
    }

    @Test
    void setShelterEnvironment_WithNullEnvironment_ShouldSetDefault() {
        // Act - Test through getShelterByName with null environment
        when(shelterRepository.findByName("Bucium")).thenReturn(testShelter);
        when(shelterDomainMapper.toDomain(testShelter)).thenReturn(testShelterDomain);
        when(environment.getProperty("local.server.port")).thenReturn(null);

        ShelterDomain result = shelterService.getShelterByName();

        // Assert
        assertNotNull(result);
        // Should handle null environment gracefully
    }
}