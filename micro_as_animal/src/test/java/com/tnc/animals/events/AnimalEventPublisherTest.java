package com.tnc.animals.events;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AnimalEventPublisher.
 * Tests the event publishing functionality with mocked RabbitTemplate.
 */
@ExtendWith(MockitoExtension.class)
class AnimalEventPublisherTest {
    
    @Mock
    private RabbitTemplate rabbitTemplate;
    
    @InjectMocks
    private AnimalEventPublisher animalEventPublisher;
    
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
    void publishAnimalCreated_ShouldSendMessageToRabbitMQ() {
        // When
        animalEventPublisher.publishAnimalCreated(testEventDTO);
        
        // Then
        verify(rabbitTemplate).convertAndSend(
                eq("animal.events.exchange"),
                eq("animal.created"),
                eq(testEventDTO)
        );
    }
    
    @Test
    void publishAnimalUpdated_ShouldSendMessageToRabbitMQ() {
        // Given
        testEventDTO.setEventType(AnimalEventDTO.EVENT_ANIMAL_UPDATED);
        
        // When
        animalEventPublisher.publishAnimalUpdated(testEventDTO);
        
        // Then
        verify(rabbitTemplate).convertAndSend(
                eq("animal.events.exchange"),
                eq("animal.updated"),
                eq(testEventDTO)
        );
    }
    
    @Test
    void publishAnimalAdopted_ShouldSendMessageToRabbitMQ() {
        // Given
        testEventDTO.setEventType(AnimalEventDTO.EVENT_ANIMAL_ADOPTED);
        testEventDTO.setUserId(1L);
        testEventDTO.setUserEmail("user@example.com");
        
        // When
        animalEventPublisher.publishAnimalAdopted(testEventDTO);
        
        // Then
        verify(rabbitTemplate).convertAndSend(
                eq("animal.events.exchange"),
                eq("animal.adopted"),
                eq(testEventDTO)
        );
    }
    
    @Test
    void publishAnimalDeleted_ShouldSendMessageToRabbitMQ() {
        // Given
        testEventDTO.setEventType(AnimalEventDTO.EVENT_ANIMAL_DELETED);
        
        // When
        animalEventPublisher.publishAnimalDeleted(testEventDTO);
        
        // Then
        verify(rabbitTemplate).convertAndSend(
                eq("animal.events.exchange"),
                eq("animal.deleted"),
                eq(testEventDTO)
        );
    }
    
    @Test
    void publishAnimalCreated_WhenRabbitMQThrowsException_ShouldNotPropagateException() {
        // Given
        doThrow(new RuntimeException("RabbitMQ connection failed"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());
        
        // When & Then - Should not throw exception
        animalEventPublisher.publishAnimalCreated(testEventDTO);
        
        // Verify the method was called
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));
    }
}
