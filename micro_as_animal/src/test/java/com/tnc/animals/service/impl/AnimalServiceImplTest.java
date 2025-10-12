package com.tnc.animals.service.impl;

import com.tnc.animals.repository.entities.Animal;
import com.tnc.animals.repository.interfaces.AnimalRepository;
import com.tnc.animals.service.domain.AnimalDomain;
import com.tnc.animals.service.mapper.AnimalDomainMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for AnimalServiceImpl
 * Tests all business logic with proper mocking
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Animal Service Implementation Tests")
class AnimalServiceImplTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private AnimalDomainMapper animalDomainMapper;

    @Mock
    private Environment environment;

    @InjectMocks
    private AnimalServiceImpl animalService;

    private Animal testAnimal;
    private AnimalDomain testAnimalDomain;

    @BeforeEach
    void setUp() {
        testAnimal = createTestAnimal(1L, "Buddy", "Dog", "Golden Retriever");
        testAnimalDomain = createTestAnimalDomain(1L, "Buddy", "Dog", "Golden Retriever");
    }

    @Test
    @DisplayName("get() - Should return animal domain when animal exists")
    void get_WhenAnimalExists_ShouldReturnAnimalDomain() {
        // Given
        Long animalId = 1L;
        when(animalRepository.getById(animalId)).thenReturn(testAnimal);
        when(animalDomainMapper.toDomain(testAnimal)).thenReturn(testAnimalDomain);

        // When
        AnimalDomain result = animalService.get(animalId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Buddy");
        assertThat(result.getSpecies()).isEqualTo("Dog");
        assertThat(result.getBreed()).isEqualTo("Golden Retriever");

        verify(animalRepository).getById(animalId);
        verify(animalDomainMapper).toDomain(testAnimal);
    }

    @Test
    @DisplayName("get() - Should handle null ID")
    void get_WithNullId_ShouldThrowException() {
        // Given
        when(animalRepository.getById(null)).thenThrow(new IllegalArgumentException("ID cannot be null"));

        // When & Then
        assertThatThrownBy(() -> animalService.get(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ID cannot be null");

        verify(animalRepository).getById(null);
    }

    @Test
    @DisplayName("getAll() - Should return all animals with environment set")
    void getAll_ShouldReturnAllAnimalsWithEnvironment() {
        // Given
        List<Animal> animals = Arrays.asList(
                createTestAnimal(1L, "Buddy", "Dog", "Golden Retriever"),
                createTestAnimal(2L, "Whiskers", "Cat", "Persian")
        );
        List<AnimalDomain> animalDomains = Arrays.asList(
                createTestAnimalDomain(1L, "Buddy", "Dog", "Golden Retriever"),
                createTestAnimalDomain(2L, "Whiskers", "Cat", "Persian")
        );

        when(animalRepository.findAll()).thenReturn(animals);
        when(animalDomainMapper.toDomainList(animals)).thenReturn(animalDomains);
        when(environment.getProperty("local.server.port")).thenReturn("8093");

        // When
        List<AnimalDomain> result = animalService.getAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEnvironment()).isEqualTo("8093");
        assertThat(result.get(1).getEnvironment()).isEqualTo("8093");
        assertThat(result.get(0).getName()).isEqualTo("Buddy");
        assertThat(result.get(1).getName()).isEqualTo("Whiskers");

        verify(animalRepository).findAll();
        verify(animalDomainMapper).toDomainList(animals);
        verify(environment).getProperty("local.server.port");
    }

    @Test
    @DisplayName("getAll() - Should return empty list when no animals exist")
    void getAll_WhenNoAnimalsExist_ShouldReturnEmptyList() {
        // Given
        when(animalRepository.findAll()).thenReturn(Arrays.asList());
        when(animalDomainMapper.toDomainList(any())).thenReturn(Arrays.asList());
        when(environment.getProperty("local.server.port")).thenReturn("8093");

        // When
        List<AnimalDomain> result = animalService.getAll();

        // Then
        assertThat(result).isEmpty();

        verify(animalRepository).findAll();
        verify(animalDomainMapper).toDomainList(any());
    }

    @Test
    @DisplayName("add() - Should add new animal successfully")
    void add_WithValidAnimal_ShouldAddAnimal() {
        // Given
        AnimalDomain newAnimalDomain = createTestAnimalDomain(null, "New Buddy", "Dog", "Labrador");
        Animal newAnimal = createTestAnimal(1L, "New Buddy", "Dog", "Labrador");
        AnimalDomain savedAnimalDomain = createTestAnimalDomain(1L, "New Buddy", "Dog", "Labrador");

        when(animalDomainMapper.toEntity(newAnimalDomain)).thenReturn(newAnimal);
        when(animalRepository.save(newAnimal)).thenReturn(newAnimal);
        when(animalDomainMapper.toDomain(newAnimal)).thenReturn(savedAnimalDomain);

        // When
        AnimalDomain result = animalService.add(newAnimalDomain);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("New Buddy");
        assertThat(result.getSpecies()).isEqualTo("Dog");
        assertThat(result.getBreed()).isEqualTo("Labrador");

        verify(animalDomainMapper).toEntity(newAnimalDomain);
        verify(animalRepository).save(newAnimal);
        verify(animalDomainMapper).toDomain(newAnimal);
    }

    @Test
    @DisplayName("add() - Should handle null animal domain")
    void add_WithNullAnimal_ShouldThrowException() {
        // Given
        when(animalDomainMapper.toEntity(null)).thenThrow(new IllegalArgumentException("Animal domain cannot be null"));

        // When & Then
        assertThatThrownBy(() -> animalService.add(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Animal domain cannot be null");

        verify(animalDomainMapper).toEntity(null);
    }

    @Test
    @DisplayName("update() - Should update existing animal successfully")
    void update_WithValidAnimal_ShouldUpdateAnimal() {
        // Given
        AnimalDomain updatedAnimalDomain = createTestAnimalDomain(1L, "Updated Buddy", "Dog", "Golden Retriever");
        Animal updatedAnimal = createTestAnimal(1L, "Updated Buddy", "Dog", "Golden Retriever");
        AnimalDomain savedAnimalDomain = createTestAnimalDomain(1L, "Updated Buddy", "Dog", "Golden Retriever");

        when(animalDomainMapper.toEntity(updatedAnimalDomain)).thenReturn(updatedAnimal);
        when(animalRepository.save(updatedAnimal)).thenReturn(updatedAnimal);
        when(animalDomainMapper.toDomain(updatedAnimal)).thenReturn(savedAnimalDomain);

        // When
        AnimalDomain result = animalService.update(updatedAnimalDomain);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Updated Buddy");
        assertThat(result.getSpecies()).isEqualTo("Dog");
        assertThat(result.getBreed()).isEqualTo("Golden Retriever");

        verify(animalDomainMapper).toEntity(updatedAnimalDomain);
        verify(animalRepository).save(updatedAnimal);
        verify(animalDomainMapper).toDomain(updatedAnimal);
    }

    @Test
    @DisplayName("update() - Should handle repository exception")
    void update_WhenRepositoryThrowsException_ShouldPropagateException() {
        // Given
        AnimalDomain updatedAnimalDomain = createTestAnimalDomain(1L, "Updated Buddy", "Dog", "Golden Retriever");
        Animal updatedAnimal = createTestAnimal(1L, "Updated Buddy", "Dog", "Golden Retriever");

        when(animalDomainMapper.toEntity(updatedAnimalDomain)).thenReturn(updatedAnimal);
        when(animalRepository.save(updatedAnimal)).thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        assertThatThrownBy(() -> animalService.update(updatedAnimalDomain))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database connection failed");

        verify(animalDomainMapper).toEntity(updatedAnimalDomain);
        verify(animalRepository).save(updatedAnimal);
        verify(animalDomainMapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("getAll() - Should handle environment property not set")
    void getAll_WhenEnvironmentPropertyNotSet_ShouldHandleGracefully() {
        // Given
        List<Animal> animals = Arrays.asList(testAnimal);
        List<AnimalDomain> animalDomains = Arrays.asList(testAnimalDomain);

        when(animalRepository.findAll()).thenReturn(animals);
        when(animalDomainMapper.toDomainList(animals)).thenReturn(animalDomains);
        when(environment.getProperty("local.server.port")).thenReturn(null);

        // When
        List<AnimalDomain> result = animalService.getAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEnvironment()).isNull();
        assertThat(result.get(0).getName()).isEqualTo("Buddy");

        verify(animalRepository).findAll();
        verify(animalDomainMapper).toDomainList(animals);
        verify(environment).getProperty("local.server.port");
    }

    // Helper methods
    private Animal createTestAnimal(Long id, String name, String species, String breed) {
        Animal animal = new Animal();
        animal.setId(id);
        animal.setName(name);
        animal.setSpecies(species);
        animal.setBreed(breed);
        return animal;
    }

    private AnimalDomain createTestAnimalDomain(Long id, String name, String species, String breed) {
        AnimalDomain animalDomain = new AnimalDomain();
        animalDomain.setId(id);
        animalDomain.setName(name);
        animalDomain.setSpecies(species);
        animalDomain.setBreed(breed);
        return animalDomain;
    }
}
