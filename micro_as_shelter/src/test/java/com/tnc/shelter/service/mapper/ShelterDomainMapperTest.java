package com.tnc.shelter.service.mapper;

import com.tnc.shelter.repository.entities.Shelter;
import com.tnc.shelter.service.domain.ShelterDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ShelterDomainMapper
 * Tests MapStruct mapping between Shelter entity and ShelterDomain
 */
@ExtendWith(MockitoExtension.class)
class ShelterDomainMapperTest {

    private ShelterDomainMapper mapper;
    private Shelter testShelter;
    private ShelterDomain testShelterDomain;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ShelterDomainMapper.class);
        
        testShelter = new Shelter();
        testShelter.setId(1L);
        testShelter.setName("Test Shelter");
        testShelter.setCity("Test City");

        testShelterDomain = new ShelterDomain(1L, "Test Shelter", "Test City");
    }

    @Test
    void toDomain_WithValidShelter_ShouldMapCorrectly() {
        // Act
        ShelterDomain result = mapper.toDomain(testShelter);

        // Assert
        assertNotNull(result);
        assertEquals(testShelter.getId(), result.getId());
        assertEquals(testShelter.getName(), result.getName());
        assertEquals(testShelter.getCity(), result.getCity());
    }

    @Test
    void toEntity_WithValidShelterDomain_ShouldMapCorrectly() {
        // Act
        Shelter result = mapper.toEntity(testShelterDomain);

        // Assert
        assertNotNull(result);
        assertEquals(testShelterDomain.getId(), result.getId());
        assertEquals(testShelterDomain.getName(), result.getName());
        assertEquals(testShelterDomain.getCity(), result.getCity());
    }

    @Test
    void toDomainList_WithValidShelterList_ShouldMapCorrectly() {
        // Arrange
        List<Shelter> shelters = Arrays.asList(testShelter);

        // Act
        List<ShelterDomain> result = mapper.toDomainList(shelters);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testShelter.getId(), result.get(0).getId());
        assertEquals(testShelter.getName(), result.get(0).getName());
        assertEquals(testShelter.getCity(), result.get(0).getCity());
    }

    @Test
    void toEntityList_WithValidShelterDomainList_ShouldMapCorrectly() {
        // Arrange
        List<ShelterDomain> shelterDomains = Arrays.asList(testShelterDomain);

        // Act
        List<Shelter> result = mapper.toEntityList(shelterDomains);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testShelterDomain.getId(), result.get(0).getId());
        assertEquals(testShelterDomain.getName(), result.get(0).getName());
        assertEquals(testShelterDomain.getCity(), result.get(0).getCity());
    }

    @Test
    void toDomain_WithNullShelter_ShouldReturnNull() {
        // Act
        ShelterDomain result = mapper.toDomain(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toEntity_WithNullShelterDomain_ShouldReturnNull() {
        // Act
        Shelter result = mapper.toEntity(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toDomainList_WithNullList_ShouldReturnNull() {
        // Act
        List<ShelterDomain> result = mapper.toDomainList(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toEntityList_WithNullList_ShouldReturnNull() {
        // Act
        List<Shelter> result = mapper.toEntityList(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toDomain_WithEmptyShelter_ShouldMapCorrectly() {
        // Arrange
        Shelter emptyShelter = new Shelter();

        // Act
        ShelterDomain result = mapper.toDomain(emptyShelter);

        // Assert
        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getName());
        assertNull(result.getCity());
    }

    @Test
    void toEntity_WithEmptyShelterDomain_ShouldMapCorrectly() {
        // Arrange
        ShelterDomain emptyShelterDomain = new ShelterDomain(null, null, null);

        // Act
        Shelter result = mapper.toEntity(emptyShelterDomain);

        // Assert
        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getName());
        assertNull(result.getCity());
    }

    @Test
    void toDomainList_WithEmptyList_ShouldReturnEmptyList() {
        // Arrange
        List<Shelter> emptyShelters = Arrays.asList();

        // Act
        List<ShelterDomain> result = mapper.toDomainList(emptyShelters);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toEntityList_WithEmptyList_ShouldReturnEmptyList() {
        // Arrange
        List<ShelterDomain> emptyShelterDomains = Arrays.asList();

        // Act
        List<Shelter> result = mapper.toEntityList(emptyShelterDomains);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toDomain_WithDifferentShelter_ShouldMapCorrectly() {
        // Arrange
        testShelter.setName("Different Shelter");
        testShelter.setCity("Different City");

        // Act
        ShelterDomain result = mapper.toDomain(testShelter);

        // Assert
        assertNotNull(result);
        assertEquals("Different Shelter", result.getName());
        assertEquals("Different City", result.getCity());
    }

    @Test
    void toEntity_WithDifferentShelterDomain_ShouldMapCorrectly() {
        // Arrange
        ShelterDomain differentShelterDomain = new ShelterDomain(2L, "Different Shelter", "Different City");

        // Act
        Shelter result = mapper.toEntity(differentShelterDomain);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Different Shelter", result.getName());
        assertEquals("Different City", result.getCity());
    }

    @Test
    void toDomainList_WithMultipleShelters_ShouldMapCorrectly() {
        // Arrange
        Shelter shelter1 = new Shelter();
        shelter1.setId(1L);
        shelter1.setName("Shelter 1");
        shelter1.setCity("City 1");

        Shelter shelter2 = new Shelter();
        shelter2.setId(2L);
        shelter2.setName("Shelter 2");
        shelter2.setCity("City 2");

        List<Shelter> shelters = Arrays.asList(shelter1, shelter2);

        // Act
        List<ShelterDomain> result = mapper.toDomainList(shelters);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Shelter 1", result.get(0).getName());
        assertEquals("Shelter 2", result.get(1).getName());
    }

    @Test
    void toEntityList_WithMultipleShelterDomains_ShouldMapCorrectly() {
        // Arrange
        ShelterDomain domain1 = new ShelterDomain(1L, "Domain 1", "City 1");
        
        ShelterDomain domain2 = new ShelterDomain(2L, "Domain 2", "City 2");

        List<ShelterDomain> shelterDomains = Arrays.asList(domain1, domain2);

        // Act
        List<Shelter> result = mapper.toEntityList(shelterDomains);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Domain 1", result.get(0).getName());
        assertEquals("Domain 2", result.get(1).getName());
    }
}
