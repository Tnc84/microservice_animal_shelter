package com.tnc.shelter.events;

import com.tnc.events.animal.AnimalEventDTO;
import com.tnc.events.constants.RabbitMQConstants;
import com.tnc.shelter.repository.interfaces.ShelterRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Listens to animal events from RabbitMQ and updates shelter statistics.
 * Processes animal lifecycle events to maintain accurate shelter data.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnimalEventListener {

    private final ShelterRepository shelterRepository;

    /**
     * Consume animal events from the shelter updates queue.
     * Updates shelter statistics based on event type.
     */
    @RabbitListener(queues = RabbitMQConstants.SHELTER_UPDATES_QUEUE)
    @CircuitBreaker(name = "rabbitmq", fallbackMethod = "handleEventFallback")
    @Transactional
    public void handleAnimalEvent(AnimalEventDTO event) {
        log.info("Received animal event: type={}, animalId={}, shelterId={}",
                event.getEventType(), event.getAnimalId(), event.getShelterId());

        try {
            switch (event.getEventType()) {
                case AnimalEventDTO.EVENT_ANIMAL_CREATED:
                    handleAnimalCreated(event);
                    break;
                case AnimalEventDTO.EVENT_ANIMAL_ADOPTED:
                    handleAnimalAdopted(event);
                    break;
                case AnimalEventDTO.EVENT_ANIMAL_DELETED:
                    handleAnimalDeleted(event);
                    break;
                case AnimalEventDTO.EVENT_ANIMAL_UPDATED:
                    handleAnimalUpdated(event);
                    break;
                default:
                    log.warn("Unknown event type: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Error processing animal event: {}", event, e);
            throw e; // Re-throw to trigger DLQ if configured
        }
    }

    /**
     * Handle animal created event - increment shelter animal count.
     */
    private void handleAnimalCreated(AnimalEventDTO event) {
        log.info("Processing animal created event for animal: {}", event.getAnimalName());

        if (event.getShelterId() != null) {
            shelterRepository.findById(event.getShelterId())
                    .ifPresentOrElse(
                            shelter -> {
                                shelter.setAnimalCount(shelter.getAnimalCount() + 1);
                                shelterRepository.save(shelter);
                                log.info("Updated shelter {} animal count to {}",
                                        shelter.getName(), shelter.getAnimalCount());
                            },
                            () -> log.warn("Shelter not found with ID: {}", event.getShelterId())
                    );
        } else {
            // If no specific shelter, update all shelters or use default logic
            log.info("No shelter ID in event, animal created without shelter assignment");
        }
    }

    /**
     * Handle animal adopted event - increment adoption count and decrement animal count.
     */
    private void handleAnimalAdopted(AnimalEventDTO event) {
        log.info("Processing animal adopted event for animal: {} by user: {}",
                event.getAnimalName(), event.getUserEmail());

        if (event.getShelterId() != null) {
            shelterRepository.findById(event.getShelterId())
                    .ifPresentOrElse(
                            shelter -> {
                                shelter.setAdoptionCount(shelter.getAdoptionCount() + 1);
                                if (shelter.getAnimalCount() > 0) {
                                    shelter.setAnimalCount(shelter.getAnimalCount() - 1);
                                }
                                shelterRepository.save(shelter);
                                log.info("Updated shelter {} - adoptions: {}, animals: {}",
                                        shelter.getName(), shelter.getAdoptionCount(), shelter.getAnimalCount());
                            },
                            () -> log.warn("Shelter not found with ID: {}", event.getShelterId())
                    );
        }
    }

    /**
     * Handle animal deleted event - decrement shelter animal count.
     */
    private void handleAnimalDeleted(AnimalEventDTO event) {
        log.info("Processing animal deleted event for animal ID: {}", event.getAnimalId());

        if (event.getShelterId() != null) {
            shelterRepository.findById(event.getShelterId())
                    .ifPresentOrElse(
                            shelter -> {
                                if (shelter.getAnimalCount() > 0) {
                                    shelter.setAnimalCount(shelter.getAnimalCount() - 1);
                                    shelterRepository.save(shelter);
                                    log.info("Updated shelter {} animal count to {}",
                                            shelter.getName(), shelter.getAnimalCount());
                                }
                            },
                            () -> log.warn("Shelter not found with ID: {}", event.getShelterId())
                    );
        }
    }

    /**
     * Handle animal updated event - log update for potential future processing.
     */
    private void handleAnimalUpdated(AnimalEventDTO event) {
        log.info("Processing animal updated event for animal: {}", event.getAnimalName());
        // Currently just logging, could be extended to track specific updates
    }

    /**
     * Fallback method when circuit breaker is open.
     */
    public void handleEventFallback(AnimalEventDTO event, Throwable t) {
        log.error("Circuit breaker open - failed to process event: {}, error: {}",
                event, t.getMessage());
        // Could implement retry logic or store for later processing
    }
}
