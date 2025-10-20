package com.tnc.shelter.integration;

import com.tnc.shelter.controller.dto.ShelterDTO;
import com.tnc.shelter.repository.entities.Shelter;
import com.tnc.shelter.repository.interfaces.ShelterRepository;
import com.tnc.shelter.service.domain.ShelterDomain;
import com.tnc.shelter.service.exception.ShelterAddressException;
import com.tnc.shelter.service.exception.ShelterNameException;
import com.tnc.shelter.service.interfaces.ShelterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Shelter service
 * Tests database interactions and service layer integration
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Disabled("Integration tests disabled - can be enabled when needed")
class ShelterIntegrationTest {

    @Autowired
    private ShelterService shelterService;

    @Autowired
    private ShelterRepository shelterRepository;

    private Shelter testShelter;
    private ShelterDomain testShelterDomain;

    @BeforeEach
    void setUp() {
        // Clean up database before each test
        shelterRepository.deleteAll();
        
        testShelter = new Shelter();
        testShelter.setName("Integration Test Shelter");
        testShelter.setCity("Test City");

        testShelterDomain = new ShelterDomain(null, "Integration Test Shelter", "Test City");
    }

    @Test
    void addShelter_ShouldPersistToDatabase() throws ShelterAddressException, ShelterNameException {
        // Act
        ShelterDomain result = shelterService.add(testShelterDomain);

        // Assert
        assertNotNull(result);
        assertEquals("Integration Test Shelter", result.getName());
        
        // Verify persistence
        List<Shelter> savedShelters = shelterRepository.findAll();
        assertEquals(1, savedShelters.size());
        assertEquals("Integration Test Shelter", savedShelters.get(0).getName());
    }

    @Test
    void getAllShelters_ShouldReturnAllPersistedShelters() throws ShelterAddressException, ShelterNameException {
        // Arrange - Add multiple shelters
        shelterService.add(testShelterDomain);
        
        ShelterDomain anotherShelter = new ShelterDomain(null, "Another Shelter", "Test City");
        shelterService.add(anotherShelter);

        // Act
        List<ShelterDomain> result = shelterService.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(s -> s.getName().equals("Integration Test Shelter")));
        assertTrue(result.stream().anyMatch(s -> s.getName().equals("Another Shelter")));
    }

    @Test
    void getShelterByName_ShouldReturnSpecificShelter() {
        // Arrange - Create and persist shelter with name "Bucium" as expected by service
        Shelter buciumShelter = new Shelter();
        buciumShelter.setName("Bucium");
        buciumShelter.setCity("Test City");
        shelterRepository.save(buciumShelter);

        // Act
        ShelterDomain result = shelterService.getShelterByName();

        // Assert
        assertNotNull(result);
        assertEquals("Bucium", result.getName());
    }

    @Test
    void updateShelter_ShouldModifyExistingShelter() throws ShelterAddressException, ShelterNameException {
        // Arrange - Add shelter first
        ShelterDomain addedShelter = shelterService.add(testShelterDomain);
        
        // Modify the shelter
        ShelterDomain updatedShelter = new ShelterDomain(addedShelter.getId(), "Updated Shelter Name", "Test City");

        // Act
        ShelterDomain result = shelterService.update(updatedShelter);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Shelter Name", result.getName());
        assertEquals("Test City", result.getCity());
    }

    @Test
    void addShelter_WithInvalidData_ShouldThrowException() {
        // Arrange - Create invalid shelter domain
        ShelterDomain invalidShelter = new ShelterDomain(null, "", ""); // Invalid data

        // Act & Assert
        assertThrows(Exception.class, () -> shelterService.add(invalidShelter));
    }

    @Test
    void getAllShelters_WithEmptyDatabase_ShouldReturnEmptyList() {
        // Act
        List<ShelterDomain> result = shelterService.getAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void addShelter_ShouldReturnAddedShelter() throws ShelterAddressException, ShelterNameException {
        // Act
        ShelterDomain result = shelterService.add(testShelterDomain);

        // Assert
        assertNotNull(result);
        assertEquals("Integration Test Shelter", result.getName());
        assertEquals("Test City", result.getCity());
    }
}
