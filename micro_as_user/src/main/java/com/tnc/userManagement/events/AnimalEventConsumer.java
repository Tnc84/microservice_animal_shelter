package com.tnc.userManagement.events;

import com.tnc.events.animal.AnimalEventDTO;
import com.tnc.events.constants.RabbitMQConstants;
import com.tnc.userManagement.repository.entity.Notification;
import com.tnc.userManagement.service.NotificationService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consumer for animal events from RabbitMQ.
 * Creates notifications for users when animal-related events occur.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AnimalEventConsumer {
    
    private final NotificationService notificationService;
    
    /**
     * Listen to animal created events.
     * Creates notifications for relevant users when a new animal is added.
     * 
     * @param eventDTO the animal event data
     */
    @RabbitListener(queues = RabbitMQConstants.USER_NOTIFICATIONS_QUEUE)
    @CircuitBreaker(name = "rabbitmq", fallbackMethod = "handleEventFallback")
    @Transactional
    public void handleAnimalEvent(AnimalEventDTO eventDTO) {
        log.info("Received animal event: {} for animal ID: {}", eventDTO.getEventType(), eventDTO.getAnimalId());
        
        try {
            switch (eventDTO.getEventType()) {
                case AnimalEventDTO.EVENT_ANIMAL_CREATED:
                    handleAnimalCreated(eventDTO);
                    break;
                case AnimalEventDTO.EVENT_ANIMAL_UPDATED:
                    handleAnimalUpdated(eventDTO);
                    break;
                case AnimalEventDTO.EVENT_ANIMAL_ADOPTED:
                    handleAnimalAdopted(eventDTO);
                    break;
                case AnimalEventDTO.EVENT_ANIMAL_DELETED:
                    handleAnimalDeleted(eventDTO);
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
     * Creates notification for shelter staff about new animal.
     */
    private void handleAnimalCreated(AnimalEventDTO eventDTO) {
        log.info("Processing animal created event for animal: {}", eventDTO.getAnimalName());
        
        // Create notification for shelter staff (you might want to get actual user IDs from shelter)
        // For now, we'll create a general notification
        String title = "New Animal Added";
        String message = String.format("A new %s named %s has been added to the shelter", 
                eventDTO.getAnimalType(), eventDTO.getAnimalName());
        
        // Note: In a real implementation, you'd get the actual user IDs from the shelter
        // For now, we'll use a placeholder user ID or create notifications for all shelter staff
        Long shelterStaffUserId = 1L; // This should be retrieved from shelter service
        
        notificationService.createNotification(
                shelterStaffUserId,
                title,
                message,
                Notification.TYPE_ANIMAL_CREATED,
                eventDTO.getAnimalId(),
                eventDTO.getShelterId()
        );
        
        log.info("Created notification for animal created event: {}", eventDTO.getAnimalName());
    }
    
    /**
     * Handle animal updated event.
     * Creates notification for relevant users about animal updates.
     */
    private void handleAnimalUpdated(AnimalEventDTO eventDTO) {
        log.info("Processing animal updated event for animal: {}", eventDTO.getAnimalName());
        
        String title = "Animal Information Updated";
        String message = String.format("Information for %s (%s) has been updated", 
                eventDTO.getAnimalName(), eventDTO.getAnimalType());
        
        Long shelterStaffUserId = 1L; // This should be retrieved from shelter service
        
        notificationService.createNotification(
                shelterStaffUserId,
                title,
                message,
                Notification.TYPE_ANIMAL_UPDATED,
                eventDTO.getAnimalId(),
                eventDTO.getShelterId()
        );
        
        log.info("Created notification for animal updated event: {}", eventDTO.getAnimalName());
    }
    
    /**
     * Handle animal adopted event.
     * Creates notification for the user who adopted the animal and shelter staff.
     */
    private void handleAnimalAdopted(AnimalEventDTO eventDTO) {
        log.info("Processing animal adopted event for animal: {} by user: {}", 
                eventDTO.getAnimalName(), eventDTO.getUserId());
        
        // Create notification for the user who adopted the animal
        if (eventDTO.getUserId() != null) {
            String title = "Animal Adoption Confirmed";
            String message = String.format("Congratulations! You have successfully adopted %s (%s)", 
                    eventDTO.getAnimalName(), eventDTO.getAnimalType());
            
            notificationService.createNotification(
                    eventDTO.getUserId(),
                    title,
                    message,
                    Notification.TYPE_ANIMAL_ADOPTED,
                    eventDTO.getAnimalId(),
                    eventDTO.getShelterId()
            );
        }
        
        // Create notification for shelter staff
        String staffTitle = "Animal Adopted";
        String staffMessage = String.format("%s (%s) has been adopted by user %s", 
                eventDTO.getAnimalName(), eventDTO.getAnimalType(), eventDTO.getUserEmail());
        
        Long shelterStaffUserId = 1L; // This should be retrieved from shelter service
        
        notificationService.createNotification(
                shelterStaffUserId,
                staffTitle,
                staffMessage,
                Notification.TYPE_ANIMAL_ADOPTED,
                eventDTO.getAnimalId(),
                eventDTO.getShelterId()
        );
        
        log.info("Created notifications for animal adopted event: {}", eventDTO.getAnimalName());
    }
    
    /**
     * Handle animal deleted event.
     * Creates notification for shelter staff about animal removal.
     */
    private void handleAnimalDeleted(AnimalEventDTO eventDTO) {
        log.info("Processing animal deleted event for animal: {}", eventDTO.getAnimalName());
        
        String title = "Animal Removed";
        String message = String.format("%s (%s) has been removed from the shelter", 
                eventDTO.getAnimalName(), eventDTO.getAnimalType());
        
        Long shelterStaffUserId = 1L; // This should be retrieved from shelter service
        
        notificationService.createNotification(
                shelterStaffUserId,
                title,
                message,
                Notification.TYPE_ANIMAL_DELETED,
                eventDTO.getAnimalId(),
                eventDTO.getShelterId()
        );
        
        log.info("Created notification for animal deleted event: {}", eventDTO.getAnimalName());
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
