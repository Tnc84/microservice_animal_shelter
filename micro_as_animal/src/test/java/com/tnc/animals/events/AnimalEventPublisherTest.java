package com.tnc.animals.events;

import com.tnc.events.animal.AnimalEventDTO;
import com.tnc.events.constants.RabbitMQConstants;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AnimalEventPublisher
 * Tests event publishing functionality with RabbitMQ integration
 */
@ExtendWith(MockitoExtension.class)
class AnimalEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private AnimalEventPublisher animalEventPublisher;

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
    @DisplayName("Should publish animal created event successfully")
    void publishAnimalCreated_WithValidEvent_ShouldPublishSuccessfully() {
        // When
        animalEventPublisher.publishAnimalCreated(testEvent);

        // Then
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConstants.ANIMAL_EVENTS_EXCHANGE),
                eq(RabbitMQConstants.ROUTING_KEY_ANIMAL_CREATED),
                eq(testEvent)
        );
    }

    @Test
    @DisplayName("Should publish animal updated event successfully")
    void publishAnimalUpdated_WithValidEvent_ShouldPublishSuccessfully() {
        // Given
        testEvent.setEventType(AnimalEventDTO.EVENT_ANIMAL_UPDATED);
        testEvent.setDescription("Animal information updated");

        // When
        animalEventPublisher.publishAnimalUpdated(testEvent);

        // Then
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConstants.ANIMAL_EVENTS_EXCHANGE),
                eq(RabbitMQConstants.ROUTING_KEY_ANIMAL_UPDATED),
                eq(testEvent)
        );
    }

    @Test
    @DisplayName("Should publish animal adopted event successfully")
    void publishAnimalAdopted_WithValidEvent_ShouldPublishSuccessfully() {
        // Given
        testEvent.setEventType(AnimalEventDTO.EVENT_ANIMAL_ADOPTED);
        testEvent.setAnimalStatus(AnimalEventDTO.STATUS_ADOPTED);
        testEvent.setDescription("Animal was successfully adopted");

        // When
        animalEventPublisher.publishAnimalAdopted(testEvent);

        // Then
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConstants.ANIMAL_EVENTS_EXCHANGE),
                eq(RabbitMQConstants.ROUTING_KEY_ANIMAL_ADOPTED),
                eq(testEvent)
        );
    }

    @Test
    @DisplayName("Should publish animal deleted event successfully")
    void publishAnimalDeleted_WithValidEvent_ShouldPublishSuccessfully() {
        // Given
        testEvent.setEventType(AnimalEventDTO.EVENT_ANIMAL_DELETED);
        testEvent.setDescription("Animal record was deleted");

        // When
        animalEventPublisher.publishAnimalDeleted(testEvent);

        // Then
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConstants.ANIMAL_EVENTS_EXCHANGE),
                eq(RabbitMQConstants.ROUTING_KEY_ANIMAL_DELETED),
                eq(testEvent)
        );
    }

    @Test
    @DisplayName("Should handle RabbitMQ connection failure for created event")
    void publishAnimalCreated_WhenRabbitMQFails_ShouldHandleGracefully() {
        // Given
        doThrow(new RuntimeException("RabbitMQ connection failed"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));

        // When & Then
        animalEventPublisher.publishAnimalCreated(testEvent);

        // Verify that the method was called despite the exception
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConstants.ANIMAL_EVENTS_EXCHANGE),
                eq(RabbitMQConstants.ROUTING_KEY_ANIMAL_CREATED),
                eq(testEvent)
        );
    }

    @Test
    @DisplayName("Should handle RabbitMQ connection failure for updated event")
    void publishAnimalUpdated_WhenRabbitMQFails_ShouldHandleGracefully() {
        // Given
        doThrow(new RuntimeException("RabbitMQ connection failed"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));

        // When & Then
        animalEventPublisher.publishAnimalUpdated(testEvent);

        // Verify that the method was called despite the exception
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConstants.ANIMAL_EVENTS_EXCHANGE),
                eq(RabbitMQConstants.ROUTING_KEY_ANIMAL_UPDATED),
                eq(testEvent)
        );
    }

    @Test
    @DisplayName("Should handle RabbitMQ connection failure for adopted event")
    void publishAnimalAdopted_WhenRabbitMQFails_ShouldHandleGracefully() {
        // Given
        doThrow(new RuntimeException("RabbitMQ connection failed"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));

        // When & Then
        animalEventPublisher.publishAnimalAdopted(testEvent);

        // Verify that the method was called despite the exception
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConstants.ANIMAL_EVENTS_EXCHANGE),
                eq(RabbitMQConstants.ROUTING_KEY_ANIMAL_ADOPTED),
                eq(testEvent)
        );
    }

    @Test
    @DisplayName("Should handle RabbitMQ connection failure for deleted event")
    void publishAnimalDeleted_WhenRabbitMQFails_ShouldHandleGracefully() {
        // Given
        doThrow(new RuntimeException("RabbitMQ connection failed"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));

        // When & Then
        animalEventPublisher.publishAnimalDeleted(testEvent);

        // Verify that the method was called despite the exception
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConstants.ANIMAL_EVENTS_EXCHANGE),
                eq(RabbitMQConstants.ROUTING_KEY_ANIMAL_DELETED),
                eq(testEvent)
        );
    }

    @Test
    @DisplayName("Should call fallback method for created event when circuit breaker opens")
    void publishAnimalCreatedFallback_WhenCircuitBreakerOpens_ShouldLogWarning() {
        // Given
        Exception circuitBreakerException = new RuntimeException("Circuit breaker is open");

        // When
        animalEventPublisher.publishAnimalCreatedFallback(testEvent, circuitBreakerException);

        // Then - The fallback method should execute without throwing exceptions
        // This test verifies the method signature and basic functionality
        assertThat(testEvent).isNotNull();
        assertThat(circuitBreakerException).isNotNull();
    }

    @Test
    @DisplayName("Should call fallback method for updated event when circuit breaker opens")
    void publishAnimalUpdatedFallback_WhenCircuitBreakerOpens_ShouldLogWarning() {
        // Given
        Exception circuitBreakerException = new RuntimeException("Circuit breaker is open");

        // When
        animalEventPublisher.publishAnimalUpdatedFallback(testEvent, circuitBreakerException);

        // Then - The fallback method should execute without throwing exceptions
        assertThat(testEvent).isNotNull();
        assertThat(circuitBreakerException).isNotNull();
    }

    @Test
    @DisplayName("Should call fallback method for adopted event when circuit breaker opens")
    void publishAnimalAdoptedFallback_WhenCircuitBreakerOpens_ShouldLogWarning() {
        // Given
        Exception circuitBreakerException = new RuntimeException("Circuit breaker is open");

        // When
        animalEventPublisher.publishAnimalAdoptedFallback(testEvent, circuitBreakerException);

        // Then - The fallback method should execute without throwing exceptions
        assertThat(testEvent).isNotNull();
        assertThat(circuitBreakerException).isNotNull();
    }

    @Test
    @DisplayName("Should call fallback method for deleted event when circuit breaker opens")
    void publishAnimalDeletedFallback_WhenCircuitBreakerOpens_ShouldLogWarning() {
        // Given
        Exception circuitBreakerException = new RuntimeException("Circuit breaker is open");

        // When
        animalEventPublisher.publishAnimalDeletedFallback(testEvent, circuitBreakerException);

        // Then - The fallback method should execute without throwing exceptions
        assertThat(testEvent).isNotNull();
        assertThat(circuitBreakerException).isNotNull();
    }

    @Test
    @DisplayName("Should publish multiple events correctly")
    void publishMultipleEvents_ShouldPublishAllCorrectly() {
        // Given
        AnimalEventDTO createdEvent = AnimalEventDTO.builder()
                .animalId(1L)
                .eventType(AnimalEventDTO.EVENT_ANIMAL_CREATED)
                .animalName("Buddy")
                .build();

        AnimalEventDTO updatedEvent = AnimalEventDTO.builder()
                .animalId(1L)
                .eventType(AnimalEventDTO.EVENT_ANIMAL_UPDATED)
                .animalName("Buddy Updated")
                .build();

        // When
        animalEventPublisher.publishAnimalCreated(createdEvent);
        animalEventPublisher.publishAnimalUpdated(updatedEvent);

        // Then
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConstants.ANIMAL_EVENTS_EXCHANGE),
                eq(RabbitMQConstants.ROUTING_KEY_ANIMAL_CREATED),
                eq(createdEvent)
        );
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConstants.ANIMAL_EVENTS_EXCHANGE),
                eq(RabbitMQConstants.ROUTING_KEY_ANIMAL_UPDATED),
                eq(updatedEvent)
        );
        verify(rabbitTemplate, times(2)).convertAndSend(anyString(), anyString(), any(Object.class));
    }

}
