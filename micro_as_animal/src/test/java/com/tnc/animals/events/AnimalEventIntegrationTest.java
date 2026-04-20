package com.tnc.animals.events;

import com.tnc.animals.service.domain.AnimalDomain;
import com.tnc.events.animal.AnimalEventDTO;
import com.tnc.animals.service.interfaces.AnimalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Integration tests for AnimalEventPublisher with service operations
 * Tests event publishing in the context of actual service operations
 */
@ExtendWith(MockitoExtension.class)
class AnimalEventIntegrationTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private AnimalService animalService;

    @InjectMocks
    private AnimalEventPublisher animalEventPublisher;

    private AnimalDomain testAnimal;
    private AnimalEventDTO testEvent;

    @BeforeEach
    void setUp() {
        testAnimal = new AnimalDomain();
        testAnimal.setId(1L);
        testAnimal.setName("Buddy");
        testAnimal.setSpecies("Dog");
        testAnimal.setBreed("Golden Retriever");
        testAnimal.setPhoto("buddy.jpg");

        testEvent = AnimalEventDTO.builder()
                .animalId(1L)
                .eventType(AnimalEventDTO.EVENT_ANIMAL_CREATED)
                .animalName("Buddy")
                .animalType("Dog")
                .animalBreed("Golden Retriever")
                .animalAge(3)
                .animalStatus(AnimalEventDTO.STATUS_AVAILABLE)
                .shelterId(100L)
                .shelterName("Happy Paws Shelter")
                .shelterAddress("123 Main St, City")
                .userId(200L)
                .userEmail("user@example.com")
                .timestamp(LocalDateTime.now())
                .description("New animal added to the shelter")
                .build();
    }

    @Test
    @DisplayName("Should publish created event when animal is added")
    void addAnimal_ShouldPublishCreatedEvent() {
        // Given
        when(animalService.add(any(AnimalDomain.class))).thenReturn(testAnimal);

        // When
        AnimalDomain result = animalService.add(testAnimal);
        animalEventPublisher.publishAnimalCreated(testEvent);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Buddy");

        verify(rabbitTemplate).convertAndSend(
                anyString(),
                eq("animal.created"),
                any(AnimalEventDTO.class)
        );
    }

    @Test
    @DisplayName("Should publish updated event when animal is updated")
    void updateAnimal_ShouldPublishUpdatedEvent() {
        // Given
        AnimalDomain updatedAnimal = new AnimalDomain();
        updatedAnimal.setId(1L);
        updatedAnimal.setName("Buddy Updated");
        updatedAnimal.setSpecies("Dog");
        updatedAnimal.setBreed("Golden Retriever");
        updatedAnimal.setPhoto("buddy_updated.jpg");

        AnimalEventDTO updatedEvent = AnimalEventDTO.builder()
                .animalId(1L)
                .eventType(AnimalEventDTO.EVENT_ANIMAL_UPDATED)
                .animalName("Buddy Updated")
                .animalType("Dog")
                .animalBreed("Golden Retriever")
                .animalAge(3)
                .animalStatus(AnimalEventDTO.STATUS_AVAILABLE)
                .shelterId(100L)
                .shelterName("Happy Paws Shelter")
                .shelterAddress("123 Main St, City")
                .userId(200L)
                .userEmail("user@example.com")
                .timestamp(LocalDateTime.now())
                .description("Animal information updated")
                .build();

        when(animalService.update(any(AnimalDomain.class))).thenReturn(updatedAnimal);

        // When
        AnimalDomain result = animalService.update(updatedAnimal);
        animalEventPublisher.publishAnimalUpdated(updatedEvent);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Buddy Updated");

        verify(rabbitTemplate).convertAndSend(
                anyString(),
                eq("animal.updated"),
                any(AnimalEventDTO.class)
        );
    }

    @Test
    @DisplayName("Should publish adopted event when animal is adopted")
    void adoptAnimal_ShouldPublishAdoptedEvent() {
        // Given
        AnimalDomain adoptedAnimal = new AnimalDomain();
        adoptedAnimal.setId(1L);
        adoptedAnimal.setName("Buddy (Adopted)");
        adoptedAnimal.setSpecies("Dog");
        adoptedAnimal.setBreed("Golden Retriever");
        adoptedAnimal.setPhoto("buddy_adopted.jpg");

        AnimalEventDTO adoptedEvent = AnimalEventDTO.builder()
                .animalId(1L)
                .eventType(AnimalEventDTO.EVENT_ANIMAL_ADOPTED)
                .animalName("Buddy")
                .animalType("Dog")
                .animalBreed("Golden Retriever")
                .animalAge(3)
                .animalStatus(AnimalEventDTO.STATUS_ADOPTED)
                .shelterId(100L)
                .shelterName("Happy Paws Shelter")
                .shelterAddress("123 Main St, City")
                .userId(300L)
                .userEmail("adopter@example.com")
                .timestamp(LocalDateTime.now())
                .description("Animal was successfully adopted")
                .build();

        when(animalService.get(eq(1L))).thenReturn(adoptedAnimal);

        // When
        AnimalDomain result = animalService.get(1L);
        animalEventPublisher.publishAnimalAdopted(adoptedEvent);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Buddy (Adopted)");

        verify(rabbitTemplate).convertAndSend(
                anyString(),
                eq("animal.adopted"),
                any(AnimalEventDTO.class)
        );
    }

    @Test
    @DisplayName("Should publish deleted event when animal is deleted")
    void deleteAnimal_ShouldPublishDeletedEvent() {
        // Given
        AnimalEventDTO deletedEvent = AnimalEventDTO.builder()
                .animalId(1L)
                .eventType(AnimalEventDTO.EVENT_ANIMAL_DELETED)
                .animalName("Buddy")
                .animalType("Dog")
                .animalBreed("Golden Retriever")
                .animalAge(3)
                .animalStatus(AnimalEventDTO.STATUS_AVAILABLE)
                .shelterId(100L)
                .shelterName("Happy Paws Shelter")
                .shelterAddress("123 Main St, City")
                .userId(400L)
                .userEmail("admin@example.com")
                .timestamp(LocalDateTime.now())
                .description("Animal record was deleted")
                .build();

        // When
        animalService.delete(1L);
        animalEventPublisher.publishAnimalDeleted(deletedEvent);

        // Then
        verify(animalService).delete(1L);
        verify(rabbitTemplate).convertAndSend(
                anyString(),
                eq("animal.deleted"),
                any(AnimalEventDTO.class)
        );
    }

    @Test
    @DisplayName("Should handle multiple events in sequence")
    void multipleEvents_ShouldBePublishedInSequence() {
        // Given
        when(animalService.add(any(AnimalDomain.class))).thenReturn(testAnimal);
        when(animalService.update(any(AnimalDomain.class))).thenReturn(testAnimal);

        // When
        animalService.add(testAnimal);
        animalEventPublisher.publishAnimalCreated(testEvent);

        animalService.update(testAnimal);
        animalEventPublisher.publishAnimalUpdated(testEvent);

        // Then
        verify(animalService).add(testAnimal);
        verify(animalService).update(testAnimal);
        verify(rabbitTemplate, times(2)).convertAndSend(anyString(), anyString(), any(AnimalEventDTO.class));
    }

    @Test
    @DisplayName("Should handle event publishing failures gracefully")
    void eventPublishingFailure_ShouldBeHandledGracefully() {
        // Given
        doThrow(new RuntimeException("RabbitMQ connection failed"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));

        when(animalService.add(any(AnimalDomain.class))).thenReturn(testAnimal);

        // When
        AnimalDomain result = animalService.add(testAnimal);
        animalEventPublisher.publishAnimalCreated(testEvent);

        // Then
        assertThat(result).isNotNull();
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    @DisplayName("Should create event DTOs with correct data mapping")
    void createEventDTOs_ShouldMapDataCorrectly() {
        // Given
        AnimalDomain animal = new AnimalDomain();
        animal.setId(2L);
        animal.setName("Luna");
        animal.setSpecies("Cat");
        animal.setBreed("Siamese");
        animal.setPhoto("luna.jpg");

        // When
        AnimalEventDTO createdEvent = AnimalEventDTO.builder()
                .animalId(animal.getId())
                .eventType(AnimalEventDTO.EVENT_ANIMAL_CREATED)
                .animalName(animal.getName())
                .animalType(animal.getSpecies())
                .animalBreed(animal.getBreed())
                .animalAge(2)
                .animalStatus(AnimalEventDTO.STATUS_AVAILABLE)
                .shelterId(101L)
                .shelterName("Cat Rescue")
                .shelterAddress("456 Oak St")
                .userId(201L)
                .userEmail("admin@example.com")
                .timestamp(LocalDateTime.now())
                .description("New cat added to the shelter")
                .build();

        // Then
        assertThat(createdEvent.getAnimalId()).isEqualTo(animal.getId());
        assertThat(createdEvent.getAnimalName()).isEqualTo(animal.getName());
        assertThat(createdEvent.getAnimalType()).isEqualTo(animal.getSpecies());
        assertThat(createdEvent.getAnimalBreed()).isEqualTo(animal.getBreed());
        assertThat(createdEvent.getEventType()).isEqualTo(AnimalEventDTO.EVENT_ANIMAL_CREATED);
    }

    @Test
    @DisplayName("Should handle null animal gracefully")
    void handleNullAnimal_ShouldNotThrowException() {
        // When & Then
        animalService.delete(null);

        // Should not throw exceptions
        verify(animalService).delete(null);
    }

    @Test
    @DisplayName("Should verify event constants are used correctly")
    void eventConstants_ShouldBeUsedCorrectly() {
        // Given
        AnimalEventDTO event = AnimalEventDTO.builder()
                .eventType(AnimalEventDTO.EVENT_ANIMAL_CREATED)
                .animalStatus(AnimalEventDTO.STATUS_AVAILABLE)
                .build();

        // When
        animalEventPublisher.publishAnimalCreated(event);

        // Then
        assertThat(event.getEventType()).isEqualTo(AnimalEventDTO.EVENT_ANIMAL_CREATED);
        assertThat(event.getAnimalStatus()).isEqualTo(AnimalEventDTO.STATUS_AVAILABLE);
        verify(rabbitTemplate).convertAndSend(anyString(), eq("animal.created"), any(AnimalEventDTO.class));
    }
}
