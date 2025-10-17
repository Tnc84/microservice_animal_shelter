package com.tnc.shelter.controller.mapper;

import com.tnc.shelter.controller.dto.ShelterDTO;
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
 * Unit tests for ShelterDTOMapper
 * Tests MapStruct mapping between ShelterDomain and ShelterDTO
 */
@ExtendWith(MockitoExtension.class)
class ShelterDTOMapperTest {

    private ShelterDTOMapper mapper;
    private ShelterDTO testShelterDTO;
    private ShelterDomain testShelterDomain;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ShelterDTOMapper.class);
        
        testShelterDTO = new ShelterDTO(1L, "Test Shelter", "Test City", 10, 50, 5, java.time.LocalDateTime.now());

        testShelterDomain = new ShelterDomain(1L, "Test Shelter", "Test City");
    }

    @Test
    void toDTO_WithValidShelterDomain_ShouldMapCorrectly() {
        // Act
        ShelterDTO result = mapper.toDTO(testShelterDomain);

        // Assert
        assertNotNull(result);
        assertEquals(testShelterDomain.getId(), result.getId());
        assertEquals(testShelterDomain.getName(), result.getName());
        assertEquals(testShelterDomain.getCity(), result.getCity());
    }

    @Test
    void toDomain_WithValidShelterDTO_ShouldMapCorrectly() {
        // Act
        ShelterDomain result = mapper.toDomain(testShelterDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testShelterDTO.getId(), result.getId());
        assertEquals(testShelterDTO.getName(), result.getName());
        assertEquals(testShelterDTO.getCity(), result.getCity());
    }

    @Test
    void toDTOList_WithValidShelterDomainList_ShouldMapCorrectly() {
        // Arrange
        List<ShelterDomain> shelterDomains = Arrays.asList(testShelterDomain);

        // Act
        List<ShelterDTO> result = mapper.toDTOList(shelterDomains);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testShelterDomain.getId(), result.get(0).getId());
        assertEquals(testShelterDomain.getName(), result.get(0).getName());
        assertEquals(testShelterDomain.getCity(), result.get(0).getCity());
    }

    @Test
    void toDomainList_WithValidShelterDTOList_ShouldMapCorrectly() {
        // Arrange
        List<ShelterDTO> shelterDTOs = Arrays.asList(testShelterDTO);

        // Act
        List<ShelterDomain> result = mapper.toDomainList(shelterDTOs);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testShelterDTO.getId(), result.get(0).getId());
        assertEquals(testShelterDTO.getName(), result.get(0).getName());
        assertEquals(testShelterDTO.getCity(), result.get(0).getCity());
    }

    @Test
    void toDTO_WithNullShelterDomain_ShouldReturnNull() {
        // Act
        ShelterDTO result = mapper.toDTO(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toDomain_WithNullShelterDTO_ShouldReturnNull() {
        // Act
        ShelterDomain result = mapper.toDomain(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toDTOList_WithNullList_ShouldReturnNull() {
        // Act
        List<ShelterDTO> result = mapper.toDTOList(null);

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
    void toDTO_WithEmptyShelterDomain_ShouldMapCorrectly() {
        // Arrange
        ShelterDomain emptyShelterDomain = new ShelterDomain(null, null, null);

        // Act
        ShelterDTO result = mapper.toDTO(emptyShelterDomain);

        // Assert
        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getName());
        assertNull(result.getCity());
    }

    @Test
    void toDomain_WithEmptyShelterDTO_ShouldMapCorrectly() {
        // Arrange
        ShelterDTO emptyShelterDTO = new ShelterDTO(null, null, null, null, null, null, null);

        // Act
        ShelterDomain result = mapper.toDomain(emptyShelterDTO);

        // Assert
        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getName());
        assertNull(result.getCity());
    }

    @Test
    void toDTOList_WithEmptyList_ShouldReturnEmptyList() {
        // Arrange
        List<ShelterDomain> emptyShelterDomains = Arrays.asList();

        // Act
        List<ShelterDTO> result = mapper.toDTOList(emptyShelterDomains);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toDomainList_WithEmptyList_ShouldReturnEmptyList() {
        // Arrange
        List<ShelterDTO> emptyShelterDTOs = Arrays.asList();

        // Act
        List<ShelterDomain> result = mapper.toDomainList(emptyShelterDTOs);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toDTO_WithDifferentShelterDomain_ShouldMapCorrectly() {
        // Arrange
        ShelterDomain differentShelterDomain = new ShelterDomain(2L, "Different Shelter", "Different City");

        // Act
        ShelterDTO result = mapper.toDTO(differentShelterDomain);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Different Shelter", result.getName());
        assertEquals("Different City", result.getCity());
    }

    @Test
    void toDomain_WithDifferentShelterDTO_ShouldMapCorrectly() {
        // Arrange
        ShelterDTO differentShelterDTO = new ShelterDTO(2L, "Different Shelter", "Different City", 15, 60, 8, java.time.LocalDateTime.now());

        // Act
        ShelterDomain result = mapper.toDomain(differentShelterDTO);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Different Shelter", result.getName());
        assertEquals("Different City", result.getCity());
    }

    @Test
    void toDTOList_WithMultipleShelterDomains_ShouldMapCorrectly() {
        // Arrange
        ShelterDomain domain1 = new ShelterDomain(1L, "Shelter 1", "City 1");
        
        ShelterDomain domain2 = new ShelterDomain(2L, "Shelter 2", "City 2");

        List<ShelterDomain> shelterDomains = Arrays.asList(domain1, domain2);

        // Act
        List<ShelterDTO> result = mapper.toDTOList(shelterDomains);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Shelter 1", result.get(0).getName());
        assertEquals("Shelter 2", result.get(1).getName());
    }

    @Test
    void toDomainList_WithMultipleShelterDTOs_ShouldMapCorrectly() {
        // Arrange
        ShelterDTO dto1 = new ShelterDTO(1L, "DTO 1", "City 1", 5, 30, 2, java.time.LocalDateTime.now());
        ShelterDTO dto2 = new ShelterDTO(2L, "DTO 2", "City 2", 8, 40, 3, java.time.LocalDateTime.now());

        List<ShelterDTO> shelterDTOs = Arrays.asList(dto1, dto2);

        // Act
        List<ShelterDomain> result = mapper.toDomainList(shelterDTOs);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("DTO 1", result.get(0).getName());
        assertEquals("DTO 2", result.get(1).getName());
    }
}
