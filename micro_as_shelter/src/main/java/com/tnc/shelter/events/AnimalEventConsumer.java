package com.tnc.shelter.events;

import com.tnc.shelter.repository.interfaces.ShelterRepository;
import com.tnc.shelter.repository.entities.Shelter;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consumer for animal events from RabbitMQ.
 * Updates shelter statistics when animal-related events occur.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AnimalEventConsumer {
    
    private final ShelterRepository shelterRepository;
    
    /**
     * Listen to animal events and update shelter statistics.
     * 
     * @param eventDTO the animal event data
     */
    @RabbitListener(queues = "shelter.updates.queue")
    @Transactional
    @CircuitBreaker(name = "rabbitmq", fallbackMethod = "handleAnimalEventFallback")
    @Retry(name = "rabbitmq")
    @TimeLimiter(name = "rabbitmq")
    public void handleAnimalEvent(AnimalEventDTO eventDTO) {
        log.info("Received animal event: {} for animal ID: {}", eventDTO.getEventType(), eventDTO.getAnimalId());
        
        try {
            // Find the shelter associated with this animal event
            Shelter shelter = findShelterForEvent(eventDTO);
            if (shelter == null) {
                log.warn("No shelter found for animal event: {}", eventDTO.getAnimalId());
                return;
            }
            
            switch (eventDTO.getEventType()) {
                case AnimalEventDTO.EVENT_ANIMAL_CREATED:
                    handleAnimalCreated(eventDTO, shelter);
                    break;
                case AnimalEventDTO.EVENT_ANIMAL_UPDATED:
                    handleAnimalUpdated(eventDTO, shelter);
                    break;
                case AnimalEventDTO.EVENT_ANIMAL_ADOPTED:
                    handleAnimalAdopted(eventDTO, shelter);
                    break;
                case AnimalEventDTO.EVENT_ANIMAL_DELETED:
                    handleAnimalDeleted(eventDTO, shelter);
                    break;
                default:
                    log.warn("Unknown animal event type: {}", eventDTO.getEventType());
            }
        } catch (Exception e) {
            log.error("Error processing animal event for animal ID: {}", eventDTO.getAnimalId(), e);
            // In production, you might want to send to dead letter queue or retry
        }
    }
    
    /**
     * Handle animal created event.
     * Updates shelter animal count and capacity.
     */
    private void handleAnimalCreated(AnimalEventDTO eventDTO, Shelter shelter) {
        log.info("Processing animal created event for shelter ID: {}, animal: {}", 
                shelter.getId(), eventDTO.getAnimalName());
        
        // Update shelter statistics
        updateShelterAnimalCount(shelter, 1);
        updateShelterCapacity(shelter);
        
        log.info("Updated shelter statistics for animal created event: {}", eventDTO.getAnimalName());
    }
    
    /**
     * Handle animal updated event.
     * Updates shelter information if needed.
     */
    private void handleAnimalUpdated(AnimalEventDTO eventDTO, Shelter shelter) {
        log.info("Processing animal updated event for shelter ID: {}, animal: {}", 
                shelter.getId(), eventDTO.getAnimalName());
        
        // Update shelter last modified timestamp
        shelter.setLastModified(java.time.LocalDateTime.now());
        shelterRepository.save(shelter);
        
        log.info("Updated shelter last modified for animal updated event: {}", eventDTO.getAnimalName());
    }
    
    /**
     * Handle animal adopted event.
     * Updates shelter animal count and capacity.
     */
    private void handleAnimalAdopted(AnimalEventDTO eventDTO, Shelter shelter) {
        log.info("Processing animal adopted event for shelter ID: {}, animal: {}", 
                shelter.getId(), eventDTO.getAnimalName());
        
        // Update shelter statistics
        updateShelterAnimalCount(shelter, -1);
        updateShelterCapacity(shelter);
        
        // Update adoption statistics
        updateAdoptionStatistics(shelter);
        
        log.info("Updated shelter statistics for animal adopted event: {}", eventDTO.getAnimalName());
    }
    
    /**
     * Handle animal deleted event.
     * Updates shelter animal count and capacity.
     */
    private void handleAnimalDeleted(AnimalEventDTO eventDTO, Shelter shelter) {
        log.info("Processing animal deleted event for shelter ID: {}, animal: {}", 
                shelter.getId(), eventDTO.getAnimalName());
        
        // Update shelter statistics
        updateShelterAnimalCount(shelter, -1);
        updateShelterCapacity(shelter);
        
        log.info("Updated shelter statistics for animal deleted event: {}", eventDTO.getAnimalName());
    }
    
    /**
     * Find shelter associated with the animal event.
     * 
     * @param eventDTO the animal event
     * @return the shelter or null if not found
     */
    private Shelter findShelterForEvent(AnimalEventDTO eventDTO) {
        if (eventDTO.getShelterId() != null) {
            return shelterRepository.findById(eventDTO.getShelterId()).orElse(null);
        }
        
        // If no shelter ID in event, try to find by shelter name or address
        if (eventDTO.getShelterName() != null) {
            return shelterRepository.findByName(eventDTO.getShelterName());
        }
        
        return null;
    }
    
    /**
     * Update shelter animal count.
     * 
     * @param shelter the shelter
     * @param delta the change in animal count (positive for added, negative for removed)
     */
    private void updateShelterAnimalCount(Shelter shelter, int delta) {
        int currentCount = shelter.getAnimalCount() != null ? shelter.getAnimalCount() : 0;
        int newCount = Math.max(0, currentCount + delta); // Ensure count doesn't go below 0
        shelter.setAnimalCount(newCount);
        shelterRepository.save(shelter);
        
        log.debug("Updated animal count for shelter ID: {} from {} to {} (delta: {})", 
                shelter.getId(), currentCount, newCount, delta);
    }
    
    /**
     * Update shelter capacity information.
     * 
     * @param shelter the shelter
     */
    private void updateShelterCapacity(Shelter shelter) {
        // Calculate capacity percentage
        int currentCount = shelter.getAnimalCount() != null ? shelter.getAnimalCount() : 0;
        int maxCapacity = shelter.getMaxCapacity() != null ? shelter.getMaxCapacity() : 100;
        double capacityPercentage = maxCapacity > 0 ? (double) currentCount / maxCapacity * 100 : 0;
        
        log.debug("Shelter ID: {} capacity: {}/{} ({:.1f}%)", 
                shelter.getId(), currentCount, maxCapacity, capacityPercentage);
        
        // You could add capacity alerts here if needed
        if (capacityPercentage > 90) {
            log.warn("Shelter ID: {} is at {}% capacity!", shelter.getId(), capacityPercentage);
        }
    }
    
    /**
     * Update adoption statistics.
     * 
     * @param shelter the shelter
     */
    private void updateAdoptionStatistics(Shelter shelter) {
        int currentAdoptions = shelter.getAdoptionCount() != null ? shelter.getAdoptionCount() : 0;
        shelter.setAdoptionCount(currentAdoptions + 1);
        shelterRepository.save(shelter);
        
        log.debug("Updated adoption count for shelter ID: {} to {}", 
                shelter.getId(), shelter.getAdoptionCount());
    }
    
    /**
     * Fallback method for when RabbitMQ circuit breaker is open.
     * 
     * @param eventDTO the animal event data
     * @param ex the exception that triggered the fallback
     */
    public void handleAnimalEventFallback(AnimalEventDTO eventDTO, Exception ex) {
        log.warn("RabbitMQ circuit breaker open, skipping animal event processing for animal ID: {}. Error: {}", 
                eventDTO.getAnimalId(), ex.getMessage());
        
        // In a production environment, you might want to:
        // 1. Store the event in a dead letter queue
        // 2. Send to a backup processing system
        // 3. Store in a database for later processing
        // 4. Send alert to monitoring system
        
        log.info("Event {} for animal ID {} will be processed when circuit breaker closes", 
                eventDTO.getEventType(), eventDTO.getAnimalId());
    }
}
