package com.tnc.animals.service.mapper;

import com.tnc.animals.repository.entities.Animal;
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
 * Unit tests for AnimalDomainMapper
 * Tests MapStruct mapping between Animal entity and AnimalDomain
 */
@ExtendWith(MockitoExtension.class)
class AnimalDomainMapperTest {

    private AnimalDomainMapper mapper;
    private Animal testAnimal;
    private AnimalDomain testAnimalDomain;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(AnimalDomainMapper.class);
        
        testAnimal = new Animal();
        testAnimal.setId(1L);
        testAnimal.setName("Buddy");
        testAnimal.setBreed("Golden Retriever");
        testAnimal.setSpecies("Dog");
        testAnimal.setPhoto("buddy.jpg");

        testAnimalDomain = new AnimalDomain();
        testAnimalDomain.setId(1L);
        testAnimalDomain.setName("Buddy");
        testAnimalDomain.setBreed("Golden Retriever");
        testAnimalDomain.setSpecies("Dog");
        testAnimalDomain.setPhoto("buddy.jpg");
    }

    @Test
    void toDomain_WithValidAnimal_ShouldMapCorrectly() {
        // Act
        AnimalDomain result = mapper.toDomain(testAnimal);

        // Assert
        assertNotNull(result);
        assertEquals(testAnimal.getId(), result.getId());
        assertEquals(testAnimal.getName(), result.getName());
        assertEquals(testAnimal.getBreed(), result.getBreed());
        assertEquals(testAnimal.getSpecies(), result.getSpecies());
        assertEquals(testAnimal.getPhoto(), result.getPhoto());
    }

    @Test
    void toEntity_WithValidAnimalDomain_ShouldMapCorrectly() {
        // Act
        Animal result = mapper.toEntity(testAnimalDomain);

        // Assert
        assertNotNull(result);
        assertEquals(testAnimalDomain.getId(), result.getId());
        assertEquals(testAnimalDomain.getName(), result.getName());
        assertEquals(testAnimalDomain.getBreed(), result.getBreed());
        assertEquals(testAnimalDomain.getSpecies(), result.getSpecies());
        assertEquals(testAnimalDomain.getPhoto(), result.getPhoto());
    }

    @Test
    void toDomainList_WithValidAnimalList_ShouldMapCorrectly() {
        // Arrange
        List<Animal> animals = Arrays.asList(testAnimal);

        // Act
        List<AnimalDomain> result = mapper.toDomainList(animals);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAnimal.getId(), result.get(0).getId());
        assertEquals(testAnimal.getName(), result.get(0).getName());
        assertEquals(testAnimal.getBreed(), result.get(0).getBreed());
        assertEquals(testAnimal.getSpecies(), result.get(0).getSpecies());
    }

    @Test
    void toEntityList_WithValidAnimalDomainList_ShouldMapCorrectly() {
        // Arrange
        List<AnimalDomain> animalDomains = Arrays.asList(testAnimalDomain);

        // Act
        List<Animal> result = mapper.toEntityList(animalDomains);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAnimalDomain.getId(), result.get(0).getId());
        assertEquals(testAnimalDomain.getName(), result.get(0).getName());
        assertEquals(testAnimalDomain.getBreed(), result.get(0).getBreed());
        assertEquals(testAnimalDomain.getSpecies(), result.get(0).getSpecies());
    }

    @Test
    void toDomain_WithNullAnimal_ShouldReturnNull() {
        // Act
        AnimalDomain result = mapper.toDomain(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toEntity_WithNullAnimalDomain_ShouldReturnNull() {
        // Act
        Animal result = mapper.toEntity(null);

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
    void toEntityList_WithNullList_ShouldReturnNull() {
        // Act
        List<Animal> result = mapper.toEntityList(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toDomain_WithEmptyAnimal_ShouldMapCorrectly() {
        // Arrange
        Animal emptyAnimal = new Animal();

        // Act
        AnimalDomain result = mapper.toDomain(emptyAnimal);

        // Assert
        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getName());
        assertNull(result.getBreed());
        assertNull(result.getSpecies());
        assertNull(result.getPhoto());
    }

    @Test
    void toEntity_WithEmptyAnimalDomain_ShouldMapCorrectly() {
        // Arrange
        AnimalDomain emptyAnimalDomain = new AnimalDomain();

        // Act
        Animal result = mapper.toEntity(emptyAnimalDomain);

        // Assert
        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getName());
        assertNull(result.getBreed());
        assertNull(result.getSpecies());
        assertNull(result.getPhoto());
    }

    @Test
    void toDomainList_WithEmptyList_ShouldReturnEmptyList() {
        // Arrange
        List<Animal> emptyAnimals = Arrays.asList();

        // Act
        List<AnimalDomain> result = mapper.toDomainList(emptyAnimals);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toEntityList_WithEmptyList_ShouldReturnEmptyList() {
        // Arrange
        List<AnimalDomain> emptyAnimalDomains = Arrays.asList();

        // Act
        List<Animal> result = mapper.toEntityList(emptyAnimalDomains);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toDomain_WithDifferentAnimal_ShouldMapCorrectly() {
        // Arrange
        testAnimal.setName("Whiskers");
        testAnimal.setBreed("Persian");
        testAnimal.setSpecies("Cat");
        testAnimal.setPhoto("whiskers.jpg");

        // Act
        AnimalDomain result = mapper.toDomain(testAnimal);

        // Assert
        assertNotNull(result);
        assertEquals("Whiskers", result.getName());
        assertEquals("Persian", result.getBreed());
        assertEquals("Cat", result.getSpecies());
        assertEquals("whiskers.jpg", result.getPhoto());
    }

    @Test
    void toEntity_WithDifferentAnimalDomain_ShouldMapCorrectly() {
        // Arrange
        AnimalDomain differentAnimalDomain = new AnimalDomain();
        differentAnimalDomain.setId(2L);
        differentAnimalDomain.setName("Rex");
        differentAnimalDomain.setBreed("German Shepherd");
        differentAnimalDomain.setSpecies("Dog");
        differentAnimalDomain.setPhoto("rex.jpg");

        // Act
        Animal result = mapper.toEntity(differentAnimalDomain);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Rex", result.getName());
        assertEquals("German Shepherd", result.getBreed());
        assertEquals("Dog", result.getSpecies());
        assertEquals("rex.jpg", result.getPhoto());
    }

    @Test
    void toDomainList_WithMultipleAnimals_ShouldMapCorrectly() {
        // Arrange
        Animal animal1 = new Animal();
        animal1.setId(1L);
        animal1.setName("Buddy");
        animal1.setBreed("Golden Retriever");
        animal1.setSpecies("Dog");
        animal1.setPhoto("buddy.jpg");

        Animal animal2 = new Animal();
        animal2.setId(2L);
        animal2.setName("Whiskers");
        animal2.setBreed("Persian");
        animal2.setSpecies("Cat");
        animal2.setPhoto("whiskers.jpg");

        List<Animal> animals = Arrays.asList(animal1, animal2);

        // Act
        List<AnimalDomain> result = mapper.toDomainList(animals);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Buddy", result.get(0).getName());
        assertEquals("Whiskers", result.get(1).getName());
    }

    @Test
    void toEntityList_WithMultipleAnimalDomains_ShouldMapCorrectly() {
        // Arrange
        AnimalDomain domain1 = new AnimalDomain();
        domain1.setId(1L);
        domain1.setName("Buddy");
        domain1.setBreed("Golden Retriever");
        domain1.setSpecies("Dog");
        domain1.setPhoto("buddy.jpg");

        AnimalDomain domain2 = new AnimalDomain();
        domain2.setId(2L);
        domain2.setName("Whiskers");
        domain2.setBreed("Persian");
        domain2.setSpecies("Cat");
        domain2.setPhoto("whiskers.jpg");

        List<AnimalDomain> animalDomains = Arrays.asList(domain1, domain2);

        // Act
        List<Animal> result = mapper.toEntityList(animalDomains);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Buddy", result.get(0).getName());
        assertEquals("Whiskers", result.get(1).getName());
    }

    @Test
    void toDomain_WithNullFields_ShouldMapCorrectly() {
        // Arrange
        Animal animalWithNulls = new Animal();
        animalWithNulls.setId(1L);
        animalWithNulls.setName(null);
        animalWithNulls.setBreed(null);
        animalWithNulls.setSpecies(null);
        animalWithNulls.setPhoto(null);

        // Act
        AnimalDomain result = mapper.toDomain(animalWithNulls);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNull(result.getName());
        assertNull(result.getBreed());
        assertNull(result.getSpecies());
        assertNull(result.getPhoto());
    }

    @Test
    void toEntity_WithNullFields_ShouldMapCorrectly() {
        // Arrange
        AnimalDomain animalDomainWithNulls = new AnimalDomain();
        animalDomainWithNulls.setId(1L);
        animalDomainWithNulls.setName(null);
        animalDomainWithNulls.setBreed(null);
        animalDomainWithNulls.setSpecies(null);
        animalDomainWithNulls.setPhoto(null);

        // Act
        Animal result = mapper.toEntity(animalDomainWithNulls);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNull(result.getName());
        assertNull(result.getBreed());
        assertNull(result.getSpecies());
        assertNull(result.getPhoto());
    }
}
