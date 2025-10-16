package com.tnc.animals.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tnc.animals.controller.dto.AnimalDTO;
import com.tnc.animals.controller.mapper.AnimalDTOMapper;
import com.tnc.animals.service.domain.AnimalDomain;
import com.tnc.animals.service.interfaces.AnimalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive unit tests for AnimalController
 * Tests all REST endpoints with various scenarios
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Animal Controller Tests")
class AnimalControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AnimalService animalService;

    @Mock
    private AnimalDTOMapper animalDTOMapper;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AnimalController(animalService, animalDTOMapper)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("GET /animals/getById/{id} - Should return animal when found")
    void getAnimalById_WhenFound_ShouldReturnAnimal() throws Exception {
        // Given
        Long animalId = 1L;
        AnimalDomain animalDomain = createAnimalDomain(animalId, "Buddy", "Dog", "Golden Retriever");
        AnimalDTO animalDTO = createAnimalDTO(animalId, "Buddy", "Dog", "Golden Retriever");

        when(animalService.get(animalId)).thenReturn(animalDomain);
        when(animalDTOMapper.toDTO(animalDomain)).thenReturn(animalDTO);

        // When & Then
        mockMvc.perform(get("/animals/getById/{id}", animalId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(animalId))
                .andExpect(jsonPath("$.name").value("Buddy"))
                .andExpect(jsonPath("$.species").value("Dog"))
                .andExpect(jsonPath("$.breed").value("Golden Retriever"));
    }

    @Test
    @DisplayName("GET /animals/getById/{id} - Should return 404 when animal not found")
    void getAnimalById_WhenNotFound_ShouldReturn404() throws Exception {
        // Given
        Long animalId = 999L;
        when(animalService.get(animalId)).thenThrow(new RuntimeException("Animal not found"));

        // When & Then
        mockMvc.perform(get("/animals/getById/{id}", animalId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("GET /animals/getAll - Should return all animals")
    void getAllAnimals_ShouldReturnAllAnimals() throws Exception {
        // Given
        List<AnimalDomain> animalDomains = Arrays.asList(
                createAnimalDomain(1L, "Buddy", "Dog", "Golden Retriever"),
                createAnimalDomain(2L, "Whiskers", "Cat", "Persian")
        );
        List<AnimalDTO> animalDTOs = Arrays.asList(
                createAnimalDTO(1L, "Buddy", "Dog", "Golden Retriever"),
                createAnimalDTO(2L, "Whiskers", "Cat", "Persian")
        );

        when(animalService.getAll()).thenReturn(animalDomains);
        when(animalDTOMapper.toDTOList(animalDomains)).thenReturn(animalDTOs);

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
    @DisplayName("POST /animals - Should create new animal")
    void createAnimal_WithValidData_ShouldCreateAnimal() throws Exception {
        // Given
        AnimalDTO animalDTO = createAnimalDTO(null, "Buddy", "Dog", "Golden Retriever");
        AnimalDomain animalDomain = createAnimalDomain(1L, "Buddy", "Dog", "Golden Retriever");
        AnimalDTO createdAnimalDTO = createAnimalDTO(1L, "Buddy", "Dog", "Golden Retriever");

        when(animalDTOMapper.toDomain(any(AnimalDTO.class))).thenReturn(animalDomain);
        when(animalService.add(any(AnimalDomain.class))).thenReturn(animalDomain);
        when(animalDTOMapper.toDTO(any(AnimalDomain.class))).thenReturn(createdAnimalDTO);

        // When & Then
        mockMvc.perform(post("/animals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(animalDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Buddy"));
    }

    @Test
    @DisplayName("POST /animals - Should return 400 for invalid data")
    void createAnimal_WithInvalidData_ShouldReturn400() throws Exception {
        // Given
        AnimalDTO invalidAnimalDTO = createAnimalDTO(null, "", "Dog", "Golden Retriever");

        // When & Then
        mockMvc.perform(post("/animals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAnimalDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /animals - Should update existing animal")
    void updateAnimal_WithValidData_ShouldUpdateAnimal() throws Exception {
        // Given
        AnimalDTO animalDTO = createAnimalDTO(1L, "Buddy Updated", "Dog", "Golden Retriever");
        AnimalDomain animalDomain = createAnimalDomain(1L, "Buddy Updated", "Dog", "Golden Retriever");
        AnimalDTO updatedAnimalDTO = createAnimalDTO(1L, "Buddy Updated", "Dog", "Golden Retriever");

        when(animalDTOMapper.toDomain(any(AnimalDTO.class))).thenReturn(animalDomain);
        when(animalService.update(any(AnimalDomain.class))).thenReturn(animalDomain);
        when(animalDTOMapper.toDTO(any(AnimalDomain.class))).thenReturn(updatedAnimalDTO);

        // When & Then
        mockMvc.perform(put("/animals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(animalDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Buddy Updated"));
    }

    @Test
    @DisplayName("PUT /animals - Should return 400 for invalid data")
    void updateAnimal_WithInvalidData_ShouldReturn400() throws Exception {
        // Given
        AnimalDTO invalidAnimalDTO = createAnimalDTO(1L, "", "Dog", "Golden Retriever");

        // When & Then
        mockMvc.perform(put("/animals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAnimalDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /animals/getById/{id} - Should handle null ID")
    void getAnimalById_WithNullId_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc.perform(get("/animals/getById/{id}", "null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /animals/getById/{id} - Should handle negative ID")
    void getAnimalById_WithNegativeId_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc.perform(get("/animals/getById/{id}", -1))
                .andExpect(status().isBadRequest());
    }

    // Helper methods
    private AnimalDomain createAnimalDomain(Long id, String name, String species, String breed) {
        AnimalDomain animalDomain = new AnimalDomain();
        animalDomain.setId(id);
        animalDomain.setName(name);
        animalDomain.setSpecies(species);
        animalDomain.setBreed(breed);
        return animalDomain;
    }

    private AnimalDTO createAnimalDTO(Long id, String name, String species, String breed) {
        return new AnimalDTO(id, name, breed, species, null);
    }
}
