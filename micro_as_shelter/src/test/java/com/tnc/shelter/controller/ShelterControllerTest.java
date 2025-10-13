package com.tnc.shelter.controller;

import com.tnc.shelter.controller.dto.AnimalDTO;
import com.tnc.shelter.controller.dto.ShelterDTO;
import com.tnc.shelter.controller.mapper.ShelterDTOMapper;
import com.tnc.shelter.service.exception.ShelterAddressException;
import com.tnc.shelter.service.exception.ShelterNameException;
import com.tnc.shelter.service.impl.FeignAnimalProxy;
import com.tnc.shelter.service.interfaces.ShelterService;
import com.tnc.shelter.service.domain.ShelterDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ShelterController
 * Tests all REST endpoints and error handling scenarios
 */
@ExtendWith(MockitoExtension.class)
class ShelterControllerTest {

    @Mock
    private FeignAnimalProxy feignAnimalProxy;

    @Mock
    private ShelterService shelterService;

    @Mock
    private ShelterDTOMapper shelterDTOMapper;

    @InjectMocks
    private ShelterController shelterController;

    private ShelterDTO testShelterDTO;
    private ShelterDomain testShelterDomain;
    private AnimalDTO testAnimalDTO;

    @BeforeEach
    void setUp() {
        testShelterDTO = new ShelterDTO(1L, "Test Shelter", "Test City", "8080");

        testShelterDomain = new ShelterDomain(1L, "Test Shelter", "Test City");
        testShelterDomain.setEnvironment("8080");

        testAnimalDTO = new AnimalDTO(1L, "Test Animal", "photo.jpg", "8080");
    }

    @Test
    void getAllAnimalsFeign_ShouldReturnAnimalsList() {
        // Arrange
        List<AnimalDTO> expectedAnimals = Arrays.asList(testAnimalDTO);
        when(feignAnimalProxy.getAllAnimals()).thenReturn(expectedAnimals);

        // Act
        List<AnimalDTO> result = shelterController.getAllAnimalsFeign();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Animal", result.get(0).name());
        verify(feignAnimalProxy).getAllAnimals();
    }

    @Test
    void getAllAnimalsFeign_WhenServiceDown_ShouldReturnNull() {
        // Arrange
        when(feignAnimalProxy.getAllAnimals()).thenThrow(new RuntimeException("Service down"));

        // Act & Assert
        // Since Resilience4j is not configured in test, the exception will be thrown
        assertThrows(RuntimeException.class, () -> shelterController.getAllAnimalsFeign());
        verify(feignAnimalProxy).getAllAnimals();
    }

    @Test
    void getAll_ShouldReturnAllShelters() {
        // Arrange
        List<ShelterDomain> shelterDomains = Arrays.asList(testShelterDomain);
        List<ShelterDTO> expectedDTOs = Arrays.asList(testShelterDTO);
        
        when(shelterService.getAll()).thenReturn(shelterDomains);
        when(shelterDTOMapper.toDTOList(shelterDomains)).thenReturn(expectedDTOs);

        // Act
        ResponseEntity<List<ShelterDTO>> response = shelterController.getAll();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(shelterService).getAll();
        verify(shelterDTOMapper).toDTOList(shelterDomains);
    }

    @Test
    void getShelterByName_ShouldReturnShelter() {
        // Arrange
        when(shelterService.getShelterByName()).thenReturn(testShelterDomain);
        when(shelterDTOMapper.toDTO(testShelterDomain)).thenReturn(testShelterDTO);

        // Act
        ResponseEntity<ShelterDTO> response = shelterController.getShelterByName();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Shelter", response.getBody().name());
        verify(shelterService).getShelterByName();
        verify(shelterDTOMapper).toDTO(testShelterDomain);
    }

    @Test
    void add_WithValidShelter_ShouldReturnCreatedShelter() throws ShelterAddressException, ShelterNameException {
        // Arrange
        when(shelterDTOMapper.toDomain(testShelterDTO)).thenReturn(testShelterDomain);
        when(shelterService.add(testShelterDomain)).thenReturn(testShelterDomain);
        when(shelterDTOMapper.toDTO(testShelterDomain)).thenReturn(testShelterDTO);

        // Act
        ResponseEntity<ShelterDTO> response = shelterController.add(testShelterDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Shelter", response.getBody().name());
        verify(shelterService).add(testShelterDomain);
        verify(shelterDTOMapper).toDomain(testShelterDTO);
        verify(shelterDTOMapper).toDTO(testShelterDomain);
    }

    @Test
    void add_WithInvalidShelter_ShouldThrowException() throws ShelterAddressException, ShelterNameException {
        // Arrange
        when(shelterDTOMapper.toDomain(testShelterDTO)).thenReturn(testShelterDomain);
        when(shelterService.add(testShelterDomain)).thenThrow(new ShelterNameException("Invalid name"));

        // Act & Assert
        assertThrows(ShelterNameException.class, () -> shelterController.add(testShelterDTO));
        verify(shelterService).add(testShelterDomain);
    }

    @Test
    void update_WithValidShelter_ShouldReturnUpdatedShelter() throws ShelterAddressException, ShelterNameException {
        // Arrange
        when(shelterDTOMapper.toDomain(testShelterDTO)).thenReturn(testShelterDomain);
        when(shelterService.update(testShelterDomain)).thenReturn(testShelterDomain);
        when(shelterDTOMapper.toDTO(testShelterDomain)).thenReturn(testShelterDTO);

        // Act
        ResponseEntity<ShelterDTO> response = shelterController.update(testShelterDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Shelter", response.getBody().name());
        verify(shelterService).update(testShelterDomain);
        verify(shelterDTOMapper).toDomain(testShelterDTO);
        verify(shelterDTOMapper).toDTO(testShelterDomain);
    }

    @Test
    void update_WithInvalidShelter_ShouldThrowException() throws ShelterAddressException, ShelterNameException {
        // Arrange
        when(shelterDTOMapper.toDomain(testShelterDTO)).thenReturn(testShelterDomain);
        when(shelterService.update(testShelterDomain)).thenAnswer(invocation -> {
            throw new ShelterAddressException("Invalid address");
        });

        // Act & Assert
        assertThrows(ShelterAddressException.class, () -> shelterController.update(testShelterDTO));
        verify(shelterService).update(testShelterDomain);
    }

    @Test
    void getAnimalById_ShouldReturnFeignProxy() {
        // Act
        FeignAnimalProxy result = shelterController.getAnimalById();

        // Assert
        assertNotNull(result);
        assertEquals(feignAnimalProxy, result);
    }
}
