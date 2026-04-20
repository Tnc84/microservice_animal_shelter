package com.tnc.animals.events;

import com.tnc.events.animal.AnimalEventDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for AnimalEventDTO
 * Tests the data structure and helper methods for animal events
 */
@ExtendWith(MockitoExtension.class)
class AnimalEventDTOTest {

    private AnimalEventDTO testEvent;

    @BeforeEach
    void setUp() {
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
    @DisplayName("Should create AnimalEventDTO with builder pattern")
    void createEvent_WithBuilder_ShouldCreateCorrectly() {
        assertThat(testEvent.getAnimalId()).isEqualTo(1L);
        assertThat(testEvent.getEventType()).isEqualTo(AnimalEventDTO.EVENT_ANIMAL_CREATED);
        assertThat(testEvent.getAnimalName()).isEqualTo("Buddy");
        assertThat(testEvent.getAnimalType()).isEqualTo("Dog");
        assertThat(testEvent.getAnimalBreed()).isEqualTo("Golden Retriever");
        assertThat(testEvent.getAnimalAge()).isEqualTo(3);
        assertThat(testEvent.getAnimalStatus()).isEqualTo(AnimalEventDTO.STATUS_AVAILABLE);
        assertThat(testEvent.getShelterId()).isEqualTo(100L);
        assertThat(testEvent.getShelterName()).isEqualTo("Happy Paws Shelter");
        assertThat(testEvent.getShelterAddress()).isEqualTo("123 Main St, City");
        assertThat(testEvent.getUserId()).isEqualTo(200L);
        assertThat(testEvent.getUserEmail()).isEqualTo("user@example.com");
        assertThat(testEvent.getDescription()).isEqualTo("New animal added to the shelter");
        assertThat(testEvent.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should create AnimalEventDTO with no-args constructor")
    void createEvent_WithNoArgsConstructor_ShouldCreateEmpty() {
        AnimalEventDTO event = new AnimalEventDTO();
        
        assertThat(event.getAnimalId()).isNull();
        assertThat(event.getEventType()).isNull();
        assertThat(event.getAnimalName()).isNull();
        assertThat(event.getAnimalType()).isNull();
        assertThat(event.getAnimalBreed()).isNull();
        assertThat(event.getAnimalAge()).isNull();
        assertThat(event.getAnimalStatus()).isNull();
        assertThat(event.getShelterId()).isNull();
        assertThat(event.getShelterName()).isNull();
        assertThat(event.getShelterAddress()).isNull();
        assertThat(event.getUserId()).isNull();
        assertThat(event.getUserEmail()).isNull();
        assertThat(event.getTimestamp()).isNull();
        assertThat(event.getDescription()).isNull();
    }

    @Test
    @DisplayName("Should create AnimalEventDTO with all-args constructor")
    void createEvent_WithAllArgsConstructor_ShouldCreateCorrectly() {
        LocalDateTime timestamp = LocalDateTime.now();
        AnimalEventDTO event = new AnimalEventDTO(
                2L, AnimalEventDTO.EVENT_ANIMAL_UPDATED, "Max", "Cat", "Persian", 2,
                AnimalEventDTO.STATUS_PENDING_ADOPTION, 101L, "Cat Shelter", "456 Oak St",
                201L, "admin@example.com", timestamp, "Animal information updated"
        );

        assertThat(event.getAnimalId()).isEqualTo(2L);
        assertThat(event.getEventType()).isEqualTo(AnimalEventDTO.EVENT_ANIMAL_UPDATED);
        assertThat(event.getAnimalName()).isEqualTo("Max");
        assertThat(event.getAnimalType()).isEqualTo("Cat");
        assertThat(event.getAnimalBreed()).isEqualTo("Persian");
        assertThat(event.getAnimalAge()).isEqualTo(2);
        assertThat(event.getAnimalStatus()).isEqualTo(AnimalEventDTO.STATUS_PENDING_ADOPTION);
        assertThat(event.getShelterId()).isEqualTo(101L);
        assertThat(event.getShelterName()).isEqualTo("Cat Shelter");
        assertThat(event.getShelterAddress()).isEqualTo("456 Oak St");
        assertThat(event.getUserId()).isEqualTo(201L);
        assertThat(event.getUserEmail()).isEqualTo("admin@example.com");
        assertThat(event.getTimestamp()).isEqualTo(timestamp);
        assertThat(event.getDescription()).isEqualTo("Animal information updated");
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void createEvent_WithNullValues_ShouldHandleGracefully() {
        AnimalEventDTO event = AnimalEventDTO.builder()
                .animalId(null)
                .eventType(null)
                .animalName(null)
                .animalType(null)
                .animalBreed(null)
                .animalAge(null)
                .animalStatus(null)
                .shelterId(null)
                .shelterName(null)
                .shelterAddress(null)
                .userId(null)
                .userEmail(null)
                .timestamp(null)
                .description(null)
                .build();

        assertThat(event.getAnimalId()).isNull();
        assertThat(event.getEventType()).isNull();
        assertThat(event.getAnimalName()).isNull();
        assertThat(event.getAnimalType()).isNull();
        assertThat(event.getAnimalBreed()).isNull();
        assertThat(event.getAnimalAge()).isNull();
        assertThat(event.getAnimalStatus()).isNull();
        assertThat(event.getShelterId()).isNull();
        assertThat(event.getShelterName()).isNull();
        assertThat(event.getShelterAddress()).isNull();
        assertThat(event.getUserId()).isNull();
        assertThat(event.getUserEmail()).isNull();
        assertThat(event.getTimestamp()).isNull();
        assertThat(event.getDescription()).isNull();
    }

    @Test
    @DisplayName("Should update fields correctly")
    void updateEvent_ShouldUpdateFields() {
        testEvent.setAnimalName("Max");
        testEvent.setAnimalAge(5);
        testEvent.setAnimalStatus(AnimalEventDTO.STATUS_ADOPTED);
        testEvent.setDescription("Animal was adopted");

        assertThat(testEvent.getAnimalName()).isEqualTo("Max");
        assertThat(testEvent.getAnimalAge()).isEqualTo(5);
        assertThat(testEvent.getAnimalStatus()).isEqualTo(AnimalEventDTO.STATUS_ADOPTED);
        assertThat(testEvent.getDescription()).isEqualTo("Animal was adopted");
    }

    @Test
    @DisplayName("Should have correct event type constants")
    void eventTypeConstants_ShouldBeCorrect() {
        assertThat(AnimalEventDTO.EVENT_ANIMAL_CREATED).isEqualTo("ANIMAL_CREATED");
        assertThat(AnimalEventDTO.EVENT_ANIMAL_UPDATED).isEqualTo("ANIMAL_UPDATED");
        assertThat(AnimalEventDTO.EVENT_ANIMAL_ADOPTED).isEqualTo("ANIMAL_ADOPTED");
        assertThat(AnimalEventDTO.EVENT_ANIMAL_DELETED).isEqualTo("ANIMAL_DELETED");
    }

    @Test
    @DisplayName("Should have correct status constants")
    void statusConstants_ShouldBeCorrect() {
        assertThat(AnimalEventDTO.STATUS_AVAILABLE).isEqualTo("AVAILABLE");
        assertThat(AnimalEventDTO.STATUS_ADOPTED).isEqualTo("ADOPTED");
        assertThat(AnimalEventDTO.STATUS_MEDICAL_CARE).isEqualTo("MEDICAL_CARE");
        assertThat(AnimalEventDTO.STATUS_PENDING_ADOPTION).isEqualTo("PENDING_ADOPTION");
    }

    @Test
    @DisplayName("Should create adoption event correctly")
    void createAdoptionEvent_ShouldHaveCorrectFields() {
        AnimalEventDTO adoptionEvent = AnimalEventDTO.builder()
                .animalId(3L)
                .eventType(AnimalEventDTO.EVENT_ANIMAL_ADOPTED)
                .animalName("Luna")
                .animalType("Cat")
                .animalBreed("Siamese")
                .animalAge(1)
                .animalStatus(AnimalEventDTO.STATUS_ADOPTED)
                .shelterId(102L)
                .shelterName("Cat Rescue")
                .shelterAddress("789 Pine St")
                .userId(300L)
                .userEmail("adopter@example.com")
                .timestamp(LocalDateTime.now())
                .description("Animal was successfully adopted")
                .build();

        assertThat(adoptionEvent.getEventType()).isEqualTo(AnimalEventDTO.EVENT_ANIMAL_ADOPTED);
        assertThat(adoptionEvent.getAnimalStatus()).isEqualTo(AnimalEventDTO.STATUS_ADOPTED);
        assertThat(adoptionEvent.getUserId()).isEqualTo(300L);
        assertThat(adoptionEvent.getUserEmail()).isEqualTo("adopter@example.com");
    }

    @Test
    @DisplayName("Should create deletion event correctly")
    void createDeletionEvent_ShouldHaveCorrectFields() {
        AnimalEventDTO deletionEvent = AnimalEventDTO.builder()
                .animalId(4L)
                .eventType(AnimalEventDTO.EVENT_ANIMAL_DELETED)
                .animalName("Rex")
                .animalType("Dog")
                .animalBreed("German Shepherd")
                .animalAge(7)
                .animalStatus(AnimalEventDTO.STATUS_AVAILABLE)
                .shelterId(103L)
                .shelterName("Dog Shelter")
                .shelterAddress("321 Elm St")
                .userId(400L)
                .userEmail("admin@example.com")
                .timestamp(LocalDateTime.now())
                .description("Animal record was deleted")
                .build();

        assertThat(deletionEvent.getEventType()).isEqualTo(AnimalEventDTO.EVENT_ANIMAL_DELETED);
        assertThat(deletionEvent.getUserId()).isEqualTo(400L);
        assertThat(deletionEvent.getUserEmail()).isEqualTo("admin@example.com");
    }

    @Test
    @DisplayName("Should handle timestamp formatting")
    void timestamp_ShouldBeHandledCorrectly() {
        LocalDateTime specificTime = LocalDateTime.of(2024, 1, 15, 14, 30, 45);
        testEvent.setTimestamp(specificTime);

        assertThat(testEvent.getTimestamp()).isEqualTo(specificTime);
        assertThat(testEvent.getTimestamp().getYear()).isEqualTo(2024);
        assertThat(testEvent.getTimestamp().getMonthValue()).isEqualTo(1);
        assertThat(testEvent.getTimestamp().getDayOfMonth()).isEqualTo(15);
        assertThat(testEvent.getTimestamp().getHour()).isEqualTo(14);
        assertThat(testEvent.getTimestamp().getMinute()).isEqualTo(30);
        assertThat(testEvent.getTimestamp().getSecond()).isEqualTo(45);
    }
}
