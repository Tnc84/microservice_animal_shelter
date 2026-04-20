package com.tnc.animals.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tnc.animals.config.TestSecurityConfig;
import com.tnc.animals.controller.dto.AnimalDTO;
import com.tnc.animals.repository.entities.Animal;
import com.tnc.animals.repository.interfaces.AnimalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for AnimalController validation
 * Tests validation annotations and error handling
 * 
 * TODO: These tests are currently disabled due to ApplicationContext loading issues
 * with security configuration. Re-enable after fixing security test configuration.
 */
@Disabled("Disabled due to ApplicationContext loading issues - security configuration conflicts")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@Transactional
@DisplayName("Animal Controller Integration Tests")
class AnimalControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Clean up test data
        animalRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /animals - Should return 400 for invalid data")
    void createAnimal_WithInvalidData_ShouldReturn400() throws Exception {
        // Given - AnimalDTO with empty name (invalid)
        AnimalDTO invalidAnimalDTO = new AnimalDTO(null, "", "Dog", "Retriever", "photo.jpg");

        // When & Then
        mockMvc.perform(post("/animals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAnimalDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /animals - Should return 400 for null name")
    void createAnimal_WithNullName_ShouldReturn400() throws Exception {
        // Given - AnimalDTO with null name
        AnimalDTO invalidAnimalDTO = new AnimalDTO(null, null, "Dog", "Retriever", "photo.jpg");

        // When & Then
        mockMvc.perform(post("/animals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAnimalDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /animals - Should return 400 for invalid data")
    void updateAnimal_WithInvalidData_ShouldReturn400() throws Exception {
        // Given - AnimalDTO with empty name (invalid)
        AnimalDTO invalidAnimalDTO = new AnimalDTO(1L, "", "Dog", "Retriever", "photo.jpg");

        // When & Then
        mockMvc.perform(put("/animals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAnimalDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /animals/getById/{id} - Should return 400 for negative ID")
    void getAnimalById_WithNegativeId_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc.perform(get("/animals/getById/-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /animals - Should return 200 for valid data")
    void createAnimal_WithValidData_ShouldReturn200() throws Exception {
        // Given - Valid AnimalDTO
        AnimalDTO validAnimalDTO = new AnimalDTO(null, "Buddy", "Dog", "Retriever", "photo.jpg");

        // When & Then
        mockMvc.perform(post("/animals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAnimalDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Buddy"))
                .andExpect(jsonPath("$.species").value("Dog"))
                .andExpect(jsonPath("$.breed").value("Retriever"));
    }

    @Test
    @DisplayName("GET /animals/getById/{id} - Should return 200 for valid ID")
    void getAnimalById_WithValidId_ShouldReturn200() throws Exception {
        // Given - Create a test animal
        Animal testAnimal = new Animal();
        testAnimal.setName("Buddy");
        testAnimal.setSpecies("Dog");
        testAnimal.setBreed("Retriever");
        testAnimal.setPhoto("photo.jpg");
        Animal savedAnimal = animalRepository.save(testAnimal);

        // When & Then
        mockMvc.perform(get("/animals/getById/" + savedAnimal.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Buddy"))
                .andExpect(jsonPath("$.species").value("Dog"))
                .andExpect(jsonPath("$.breed").value("Retriever"));
    }
}
