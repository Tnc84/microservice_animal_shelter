package com.tnc.userManagement.events;

import com.tnc.userManagement.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AnimalEventConsumer.
 * Tests the event consumption and notification creation functionality.
 */
@ExtendWith(MockitoExtension.class)
class AnimalEventConsumerTest {
    
    @Mock
    private NotificationService notificationService;
    
    @InjectMocks
    private AnimalEventConsumer animalEventConsumer;
    
    private AnimalEventDTO testEventDTO;
    
    @BeforeEach
    void setUp() {
        testEventDTO = AnimalEventDTO.builder()
                .animalId(1L)
                .eventType(AnimalEventDTO.EVENT_ANIMAL_CREATED)
                .animalName("Buddy")
                .animalType("Dog")
                .animalBreed("Golden Retriever")
                .animalStatus(AnimalEventDTO.STATUS_AVAILABLE)
                .shelterId(1L)
                .shelterName("Happy Paws Shelter")
                .timestamp(LocalDateTime.now())
                .description("New animal added to shelter")
                .build();
    }
    
    @Test
    void handleAnimalEvent_WhenAnimalCreated_ShouldCreateNotification() {
        // When
        animalEventConsumer.handleAnimalEvent(testEventDTO);
        
        // Then
        verify(notificationService).createNotification(
                eq(1L), // shelterStaffUserId
                eq("New Animal Added"),
                contains("Buddy"),
                eq("ANIMAL_CREATED"),
                eq(1L), // animalId
                eq(1L)  // shelterId
        );
    }
    
    @Test
    void handleAnimalEvent_WhenAnimalUpdated_ShouldCreateNotification() {
        // Given
        testEventDTO.setEventType(AnimalEventDTO.EVENT_ANIMAL_UPDATED);
        
        // When
        animalEventConsumer.handleAnimalEvent(testEventDTO);
        
        // Then
        verify(notificationService).createNotification(
                eq(1L), // shelterStaffUserId
                eq("Animal Information Updated"),
                contains("Buddy"),
                eq("ANIMAL_UPDATED"),
                eq(1L), // animalId
                eq(1L)  // shelterId
        );
    }
    
    @Test
    void handleAnimalEvent_WhenAnimalAdopted_ShouldCreateNotificationsForUserAndStaff() {
        // Given
        testEventDTO.setEventType(AnimalEventDTO.EVENT_ANIMAL_ADOPTED);
        testEventDTO.setUserId(2L);
        testEventDTO.setUserEmail("adopter@example.com");
        
        // When
        animalEventConsumer.handleAnimalEvent(testEventDTO);
        
        // Then - Should create notification for the user who adopted
        verify(notificationService).createNotification(
                eq(2L), // userId
                eq("Animal Adoption Confirmed"),
                contains("Buddy"),
                eq("ANIMAL_ADOPTED"),
                eq(1L), // animalId
                eq(1L)  // shelterId
        );
        
        // And notification for shelter staff
        verify(notificationService).createNotification(
                eq(1L), // shelterStaffUserId
                eq("Animal Adopted"),
                contains("Buddy"),
                eq("ANIMAL_ADOPTED"),
                eq(1L), // animalId
                eq(1L)  // shelterId
        );
    }
    
    @Test
    void handleAnimalEvent_WhenAnimalDeleted_ShouldCreateNotification() {
        // Given
        testEventDTO.setEventType(AnimalEventDTO.EVENT_ANIMAL_DELETED);
        
        // When
        animalEventConsumer.handleAnimalEvent(testEventDTO);
        
        // Then
        verify(notificationService).createNotification(
                eq(1L), // shelterStaffUserId
                eq("Animal Removed"),
                contains("Buddy"),
                eq("ANIMAL_DELETED"),
                eq(1L), // animalId
                eq(1L)  // shelterId
        );
    }
    
    @Test
    void handleAnimalEvent_WhenUnknownEventType_ShouldNotCreateNotification() {
        // Given
        testEventDTO.setEventType("UNKNOWN_EVENT");
        
        // When
        animalEventConsumer.handleAnimalEvent(testEventDTO);
        
        // Then
        verify(notificationService, never()).createNotification(any(), any(), any(), any(), any(), any());
    }
    
    @Test
    void handleAnimalEvent_WhenExceptionThrown_ShouldNotPropagateException() {
        // Given
        doThrow(new RuntimeException("Database error"))
                .when(notificationService).createNotification(any(), any(), any(), any(), any(), any());
        
        // When & Then - Should not throw exception
        animalEventConsumer.handleAnimalEvent(testEventDTO);
        
        // Verify the method was called
        verify(notificationService).createNotification(any(), any(), any(), any(), any(), any());
    }
}
