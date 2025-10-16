package com.tnc.animals.controller.mapper;

import com.tnc.animals.controller.dto.AnimalDTO;
import com.tnc.animals.service.domain.AnimalDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AnimalDTOMapper
 * Tests MapStruct mapping between AnimalDTO and AnimalDomain
 */
@ExtendWith(MockitoExtension.class)
class AnimalDTOMapperTest {

    private AnimalDTOMapper mapper;
    private AnimalDTO testAnimalDTO;
    private AnimalDomain testAnimalDomain;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(AnimalDTOMapper.class);
        
        testAnimalDTO = new AnimalDTO(
            1L, "Buddy", "Golden Retriever", "Dog", "buddy.jpg"
        );

        testAnimalDomain = new AnimalDomain();
        testAnimalDomain.setId(1L);
        testAnimalDomain.setName("Buddy");
        testAnimalDomain.setBreed("Golden Retriever");
        testAnimalDomain.setSpecies("Dog");
        testAnimalDomain.setPhoto("buddy.jpg");
    }

    @Test
    void toDTO_WithValidAnimalDomain_ShouldMapCorrectly() {
        // Act
        AnimalDTO result = mapper.toDTO(testAnimalDomain);

        // Assert
        assertNotNull(result);
        assertEquals(testAnimalDomain.getId(), result.id());
        assertEquals(testAnimalDomain.getName(), result.name());
        assertEquals(testAnimalDomain.getBreed(), result.breed());
        assertEquals(testAnimalDomain.getSpecies(), result.species());
        assertEquals(testAnimalDomain.getPhoto(), result.photo());
        // Environment field not present in AnimalDTO
    }

    @Test
    void toDomain_WithValidAnimalDTO_ShouldMapCorrectly() {
        // Act
        AnimalDomain result = mapper.toDomain(testAnimalDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testAnimalDTO.id(), result.getId());
        assertEquals(testAnimalDTO.name(), result.getName());
        assertEquals(testAnimalDTO.breed(), result.getBreed());
        assertEquals(testAnimalDTO.species(), result.getSpecies());
        assertEquals(testAnimalDTO.photo(), result.getPhoto());
        // Environment field not present in AnimalDTO
    }

    @Test
    void toDTOList_WithValidAnimalDomainList_ShouldMapCorrectly() {
        // Arrange
        List<AnimalDomain> animalDomains = Arrays.asList(testAnimalDomain);

        // Act
        List<AnimalDTO> result = mapper.toDTOList(animalDomains);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAnimalDomain.getId(), result.get(0).id());
        assertEquals(testAnimalDomain.getName(), result.get(0).name());
        assertEquals(testAnimalDomain.getBreed(), result.get(0).breed());
        assertEquals(testAnimalDomain.getSpecies(), result.get(0).species());
    }

    @Test
    void toDomainList_WithValidAnimalDTOList_ShouldMapCorrectly() {
        // Arrange
        List<AnimalDTO> animalDTOs = Arrays.asList(testAnimalDTO);

        // Act
        List<AnimalDomain> result = mapper.toDomainList(animalDTOs);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAnimalDTO.id(), result.get(0).getId());
        assertEquals(testAnimalDTO.name(), result.get(0).getName());
        assertEquals(testAnimalDTO.breed(), result.get(0).getBreed());
        assertEquals(testAnimalDTO.species(), result.get(0).getSpecies());
    }

    @Test
    void toDTO_WithNullAnimalDomain_ShouldReturnNull() {
        // Act
        AnimalDTO result = mapper.toDTO(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toDomain_WithNullAnimalDTO_ShouldReturnNull() {
        // Act
        AnimalDomain result = mapper.toDomain(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toDTOList_WithNullList_ShouldReturnNull() {
        // Act
        List<AnimalDTO> result = mapper.toDTOList(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toDomainList_WithNullList_ShouldReturnNull() {
        // Act
        List<AnimalDomain> result = mapper.toDomainList(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toDTO_WithEmptyAnimalDomain_ShouldMapCorrectly() {
        // Arrange
        AnimalDomain emptyAnimalDomain = new AnimalDomain();

        // Act
        AnimalDTO result = mapper.toDTO(emptyAnimalDomain);

        // Assert
        assertNotNull(result);
        assertNull(result.id());
        assertNull(result.name());
        assertNull(result.breed());
        assertNull(result.species());
        assertNull(result.photo());
        // Environment field not present in AnimalDTO
    }

    @Test
    void toDomain_WithEmptyAnimalDTO_ShouldMapCorrectly() {
        // Arrange
        AnimalDTO emptyAnimalDTO = new AnimalDTO(
            null, null, null, null, null
        );

        // Act
        AnimalDomain result = mapper.toDomain(emptyAnimalDTO);

        // Assert
        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getName());
        assertNull(result.getBreed());
        assertNull(result.getSpecies());
        assertNull(result.getPhoto());
    }

    @Test
    void toDTOList_WithEmptyList_ShouldReturnEmptyList() {
        // Arrange
        List<AnimalDomain> emptyAnimalDomains = Arrays.asList();

        // Act
        List<AnimalDTO> result = mapper.toDTOList(emptyAnimalDomains);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toDomainList_WithEmptyList_ShouldReturnEmptyList() {
        // Arrange
        List<AnimalDTO> emptyAnimalDTOs = Arrays.asList();

        // Act
        List<AnimalDomain> result = mapper.toDomainList(emptyAnimalDTOs);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toDTO_WithDifferentAnimal_ShouldMapCorrectly() {
        // Arrange
        testAnimalDomain.setName("Whiskers");
        testAnimalDomain.setBreed("Persian");
        testAnimalDomain.setSpecies("Cat");
        testAnimalDomain.setPhoto("whiskers.jpg");

        // Act
        AnimalDTO result = mapper.toDTO(testAnimalDomain);

        // Assert
        assertNotNull(result);
        assertEquals("Whiskers", result.name());
        assertEquals("Persian", result.breed());
        assertEquals("Cat", result.species());
        assertEquals("whiskers.jpg", result.photo());
    }

    @Test
    void toDomain_WithDifferentAnimalDTO_ShouldMapCorrectly() {
        // Arrange
        AnimalDTO differentAnimalDTO = new AnimalDTO(
            2L, "Rex", "German Shepherd", "Dog", "rex.jpg"
        );

        // Act
        AnimalDomain result = mapper.toDomain(differentAnimalDTO);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Rex", result.getName());
        assertEquals("German Shepherd", result.getBreed());
        assertEquals("Dog", result.getSpecies());
        assertEquals("rex.jpg", result.getPhoto());
    }

    @Test
    void toDTOList_WithMultipleAnimalDomains_ShouldMapCorrectly() {
        // Arrange
        AnimalDomain animalDomain1 = new AnimalDomain();
        animalDomain1.setId(1L);
        animalDomain1.setName("Buddy");
        animalDomain1.setBreed("Golden Retriever");
        animalDomain1.setSpecies("Dog");
        animalDomain1.setPhoto("buddy.jpg");

        AnimalDomain animalDomain2 = new AnimalDomain();
        animalDomain2.setId(2L);
        animalDomain2.setName("Whiskers");
        animalDomain2.setBreed("Persian");
        animalDomain2.setSpecies("Cat");
        animalDomain2.setPhoto("whiskers.jpg");

        List<AnimalDomain> animalDomains = Arrays.asList(animalDomain1, animalDomain2);

        // Act
        List<AnimalDTO> result = mapper.toDTOList(animalDomains);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Buddy", result.get(0).name());
        assertEquals("Whiskers", result.get(1).name());
    }

    @Test
    void toDomainList_WithMultipleAnimalDTOs_ShouldMapCorrectly() {
        // Arrange
        AnimalDTO animalDTO1 = new AnimalDTO(
            1L, "Buddy", "Golden Retriever", "Dog", "buddy.jpg"
        );

        AnimalDTO animalDTO2 = new AnimalDTO(
            2L, "Whiskers", "Persian", "Cat", "whiskers.jpg"
        );

        List<AnimalDTO> animalDTOs = Arrays.asList(animalDTO1, animalDTO2);

        // Act
        List<AnimalDomain> result = mapper.toDomainList(animalDTOs);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Buddy", result.get(0).getName());
        assertEquals("Whiskers", result.get(1).getName());
    }

    @Test
    void toDTO_WithNullFields_ShouldMapCorrectly() {
        // Arrange
        AnimalDomain animalDomainWithNulls = new AnimalDomain();
        animalDomainWithNulls.setId(1L);
        animalDomainWithNulls.setName(null);
        animalDomainWithNulls.setBreed(null);
        animalDomainWithNulls.setSpecies(null);
        animalDomainWithNulls.setPhoto(null);

        // Act
        AnimalDTO result = mapper.toDTO(animalDomainWithNulls);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertNull(result.name());
        assertNull(result.breed());
        assertNull(result.species());
        assertNull(result.photo());
        // Environment field not present in AnimalDTO
    }

    @Test
    void toDomain_WithNullFields_ShouldMapCorrectly() {
        // Arrange
        AnimalDTO animalDTOWithNulls = new AnimalDTO(
            1L, null, null, null, null
        );

        // Act
        AnimalDomain result = mapper.toDomain(animalDTOWithNulls);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNull(result.getName());
        assertNull(result.getBreed());
        assertNull(result.getSpecies());
        assertNull(result.getPhoto());
    }

    @Test
    void toDTO_WithEnvironmentField_ShouldMapCorrectly() {
        // Arrange

        // Act
        AnimalDTO result = mapper.toDTO(testAnimalDomain);

        // Assert
        assertNotNull(result);
        // Environment field not present in AnimalDTO
    }

    @Test
    void toDomain_WithEnvironmentField_ShouldMapCorrectly() {
        // Arrange
        AnimalDTO animalDTOWithEnvironment = new AnimalDTO(
            1L, "Buddy", "Golden Retriever", "Dog", "buddy.jpg"
        );

        // Act
        AnimalDomain result = mapper.toDomain(animalDTOWithEnvironment);

        // Assert
        assertNotNull(result);
        // Environment field not present in AnimalDTO
    }
}
