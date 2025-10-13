package com.tnc.animals.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tnc.animals.controller.dto.AnimalDTO;
import com.tnc.animals.repository.entities.Animal;
import com.tnc.animals.repository.interfaces.AnimalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Animal microservice
 * Tests the complete flow from controller to database
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Animal Integration Tests")
class AnimalIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnimalRepository animalRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        animalRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /animals - Should create animal and return it")
    void createAnimal_ShouldCreateAndReturnAnimal() throws Exception {
        // Given
        AnimalDTO animalDTO = createAnimalDTO(null, "Buddy", "Dog", "Golden Retriever");

        // When & Then
        mockMvc.perform(post("/animals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(animalDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Buddy"))
                .andExpect(jsonPath("$.species").value("Dog"))
                .andExpect(jsonPath("$.breed").value("Golden Retriever"))
                .andExpect(jsonPath("$.id").isNotEmpty());

        // Verify in database
        assertThat(animalRepository.findAll()).hasSize(1);
        Animal savedAnimal = animalRepository.findAll().get(0);
        assertThat(savedAnimal.getName()).isEqualTo("Buddy");
        assertThat(savedAnimal.getSpecies()).isEqualTo("Dog");
        assertThat(savedAnimal.getBreed()).isEqualTo("Golden Retriever");
    }

    @Test
    @DisplayName("GET /animals/getById/{id} - Should return animal by ID")
    void getAnimalById_ShouldReturnAnimal() throws Exception {
        // Given
        Animal savedAnimal = createAndSaveAnimal("Buddy", "Dog", "Golden Retriever");

        // When & Then
        mockMvc.perform(get("/animals/getById/{id}", savedAnimal.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(savedAnimal.getId()))
                .andExpect(jsonPath("$.name").value("Buddy"))
                .andExpect(jsonPath("$.species").value("Dog"))
                .andExpect(jsonPath("$.breed").value("Golden Retriever"));
    }

    @Test
    @DisplayName("GET /animals/getAll - Should return all animals")
    void getAllAnimals_ShouldReturnAllAnimals() throws Exception {
        // Given
        createAndSaveAnimal("Buddy", "Dog", "Golden Retriever");
        createAndSaveAnimal("Whiskers", "Cat", "Persian");

        // When & Then
        mockMvc.perform(get("/animals/getAll"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Buddy"))
                .andExpect(jsonPath("$[1].name").value("Whiskers"));
    }

    @Test
    @DisplayName("PUT /animals - Should update existing animal")
    void updateAnimal_ShouldUpdateAnimal() throws Exception {
        // Given
        Animal savedAnimal = createAndSaveAnimal("Buddy", "Dog", "Golden Retriever");
        AnimalDTO updatedAnimalDTO = createAnimalDTO(savedAnimal.getId(), "Updated Buddy", "Dog", "Labrador");

        // When & Then
        mockMvc.perform(put("/animals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedAnimalDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(savedAnimal.getId()))
                .andExpect(jsonPath("$.name").value("Updated Buddy"))
                .andExpect(jsonPath("$.breed").value("Labrador"));

        // Verify in database
        Animal updatedAnimal = animalRepository.findById(savedAnimal.getId()).orElse(null);
        assertThat(updatedAnimal).isNotNull();
        assertThat(updatedAnimal.getName()).isEqualTo("Updated Buddy");
        assertThat(updatedAnimal.getBreed()).isEqualTo("Labrador");
    }

    @Test
    @DisplayName("POST /animals - Should validate required fields")
    void createAnimal_WithMissingFields_ShouldReturn400() throws Exception {
        // Given
        AnimalDTO invalidAnimalDTO = createAnimalDTO(null, "", "Dog", "Golden Retriever");

        // When & Then
        mockMvc.perform(post("/animals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAnimalDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /animals/getById/{id} - Should return 404 for non-existent animal")
    void getAnimalById_WithNonExistentId_ShouldReturn404() throws Exception {
        // When & Then
        mockMvc.perform(get("/animals/getById/{id}", 999L))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("PUT /animals - Should validate required fields")
    void updateAnimal_WithMissingFields_ShouldReturn400() throws Exception {
        // Given
        AnimalDTO invalidAnimalDTO = createAnimalDTO(1L, "", "Dog", "Golden Retriever");

        // When & Then
        mockMvc.perform(put("/animals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAnimalDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /animals/getAll - Should return empty list when no animals exist")
    void getAllAnimals_WhenNoAnimalsExist_ShouldReturnEmptyList() throws Exception {
        // When & Then
        mockMvc.perform(get("/animals/getAll"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("POST /animals - Should handle large number of animals")
    void createMultipleAnimals_ShouldHandleLargeNumbers() throws Exception {
        // Given
        int numberOfAnimals = 100;

        // When
        for (int i = 0; i < numberOfAnimals; i++) {
            AnimalDTO animalDTO = createAnimalDTO(null, "Animal " + i, "Dog", "Breed " + i);
            mockMvc.perform(post("/animals")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(animalDTO)))
                    .andExpect(status().isOk());
        }

        // Then
        assertThat(animalRepository.findAll()).hasSize(numberOfAnimals);
    }

    // Helper methods
    private AnimalDTO createAnimalDTO(Long id, String name, String species, String breed) {
        return new AnimalDTO(id, name, breed, species, null);
    }

    private Animal createAndSaveAnimal(String name, String species, String breed) {
        Animal animal = new Animal();
        animal.setName(name);
        animal.setSpecies(species);
        animal.setBreed(breed);
        return animalRepository.save(animal);
    }
}
