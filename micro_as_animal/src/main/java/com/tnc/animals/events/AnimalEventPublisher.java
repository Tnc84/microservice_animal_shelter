package com.tnc.animals.events;

import com.tnc.animals.config.RabbitMQConfig;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Service responsible for publishing animal events to RabbitMQ.
 * Publishes events after successful database operations to ensure consistency.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnimalEventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    
    /**
     * Publish animal created event.
     * 
     * @param eventDTO the animal event data
     */
    @CircuitBreaker(name = "rabbitmq", fallbackMethod = "publishAnimalCreatedFallback")
    @Retry(name = "rabbitmq")
    public void publishAnimalCreated(AnimalEventDTO eventDTO) {
        try {
            log.info("Publishing animal created event for animal ID: {}", eventDTO.getAnimalId());
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.ANIMAL_EVENTS_EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_ANIMAL_CREATED,
                eventDTO
            );
            log.info("Successfully published animal created event for animal ID: {}", eventDTO.getAnimalId());
        } catch (Exception e) {
            log.error("Failed to publish animal created event for animal ID: {}", eventDTO.getAnimalId(), e);
            // In production, you might want to store failed events for retry
        }
    }
    
    /**
     * Publish animal updated event.
     * 
     * @param eventDTO the animal event data
     */
    @CircuitBreaker(name = "rabbitmq", fallbackMethod = "publishAnimalUpdatedFallback")
    @Retry(name = "rabbitmq")
    public void publishAnimalUpdated(AnimalEventDTO eventDTO) {
        try {
            log.info("Publishing animal updated event for animal ID: {}", eventDTO.getAnimalId());
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.ANIMAL_EVENTS_EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_ANIMAL_UPDATED,
                eventDTO
            );
            log.info("Successfully published animal updated event for animal ID: {}", eventDTO.getAnimalId());
        } catch (Exception e) {
            log.error("Failed to publish animal updated event for animal ID: {}", eventDTO.getAnimalId(), e);
        }
    }
    
    /**
     * Publish animal adopted event.
     * 
     * @param eventDTO the animal event data
     */
    @CircuitBreaker(name = "rabbitmq", fallbackMethod = "publishAnimalAdoptedFallback")
    @Retry(name = "rabbitmq")
    public void publishAnimalAdopted(AnimalEventDTO eventDTO) {
        try {
            log.info("Publishing animal adopted event for animal ID: {}", eventDTO.getAnimalId());
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.ANIMAL_EVENTS_EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_ANIMAL_ADOPTED,
                eventDTO
            );
            log.info("Successfully published animal adopted event for animal ID: {}", eventDTO.getAnimalId());
        } catch (Exception e) {
            log.error("Failed to publish animal adopted event for animal ID: {}", eventDTO.getAnimalId(), e);
        }
    }
    
    /**
     * Publish animal deleted event.
     * 
     * @param eventDTO the animal event data
     */
    @CircuitBreaker(name = "rabbitmq", fallbackMethod = "publishAnimalDeletedFallback")
    @Retry(name = "rabbitmq")
    public void publishAnimalDeleted(AnimalEventDTO eventDTO) {
        try {
            log.info("Publishing animal deleted event for animal ID: {}", eventDTO.getAnimalId());
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.ANIMAL_EVENTS_EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_ANIMAL_DELETED,
                eventDTO
            );
            log.info("Successfully published animal deleted event for animal ID: {}", eventDTO.getAnimalId());
        } catch (Exception e) {
            log.error("Failed to publish animal deleted event for animal ID: {}", eventDTO.getAnimalId(), e);
        }
    }
    
    // Fallback methods for circuit breaker
    public void publishAnimalCreatedFallback(AnimalEventDTO eventDTO, Exception ex) {
        log.warn("RabbitMQ circuit breaker open, skipping animal created event for animal ID: {}. Error: {}", 
                eventDTO.getAnimalId(), ex.getMessage());
        // Store event for later processing when circuit breaker closes
    }
    
    public void publishAnimalUpdatedFallback(AnimalEventDTO eventDTO, Exception ex) {
        log.warn("RabbitMQ circuit breaker open, skipping animal updated event for animal ID: {}. Error: {}", 
                eventDTO.getAnimalId(), ex.getMessage());
        // Store event for later processing when circuit breaker closes
    }
    
    public void publishAnimalAdoptedFallback(AnimalEventDTO eventDTO, Exception ex) {
        log.warn("RabbitMQ circuit breaker open, skipping animal adopted event for animal ID: {}. Error: {}", 
                eventDTO.getAnimalId(), ex.getMessage());
        // Store event for later processing when circuit breaker closes
    }
    
    public void publishAnimalDeletedFallback(AnimalEventDTO eventDTO, Exception ex) {
        log.warn("RabbitMQ circuit breaker open, skipping animal deleted event for animal ID: {}. Error: {}", 
                eventDTO.getAnimalId(), ex.getMessage());
        // Store event for later processing when circuit breaker closes
    }
}
