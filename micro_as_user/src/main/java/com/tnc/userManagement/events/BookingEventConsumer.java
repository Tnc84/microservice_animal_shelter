package com.tnc.userManagement.events;

import com.tnc.events.booking.BookingEventDTO;
import com.tnc.events.constants.RabbitMQConstants;
import com.tnc.userManagement.repository.entity.Notification;
import com.tnc.userManagement.service.NotificationService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

/**
 * Consumer for booking events from RabbitMQ (Pet Hotel).
 * Creates notifications for users when booking-related events occur.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookingEventConsumer {

    private final NotificationService notificationService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' h:mm a");

    /**
     * Listen to booking events from Pet Hotel service.
     * Creates notifications for users about their bookings.
     */
    @RabbitListener(queues = RabbitMQConstants.BOOKING_NOTIFICATIONS_QUEUE)
    @CircuitBreaker(name = "rabbitmq", fallbackMethod = "handleEventFallback")
    @Transactional
    public void handleBookingEvent(BookingEventDTO eventDTO) {
        log.info("Received booking event: {} for booking ID: {}", eventDTO.getEventType(), eventDTO.getBookingId());

        try {
            switch (eventDTO.getEventType()) {
                case BookingEventDTO.EVENT_BOOKING_CREATED:
                    handleBookingCreated(eventDTO);
                    break;
                case BookingEventDTO.EVENT_BOOKING_CONFIRMED:
                    handleBookingConfirmed(eventDTO);
                    break;
                case BookingEventDTO.EVENT_BOOKING_CANCELLED:
                    handleBookingCancelled(eventDTO);
                    break;
                case BookingEventDTO.EVENT_BOOKING_COMPLETED:
                    handleBookingCompleted(eventDTO);
                    break;
                case BookingEventDTO.EVENT_BOOKING_UPDATED:
                    handleBookingUpdated(eventDTO);
                    break;
                default:
                    log.warn("Unknown booking event type: {}", eventDTO.getEventType());
            }
        } catch (Exception e) {
            log.error("Error processing booking event for booking ID: {}", eventDTO.getBookingId(), e);
            throw e; // Re-throw to trigger DLQ
        }
    }

    /**
     * Handle booking created event.
     * Notifies user that their booking has been received.
     */
    private void handleBookingCreated(BookingEventDTO eventDTO) {
        log.info("Processing booking created event for pet: {}", eventDTO.getPetName());

        if (eventDTO.getUserId() == null) {
            log.warn("No user ID in booking event, skipping notification");
            return;
        }

        String title = "Booking Received";
        String message = buildBookingMessage(eventDTO, 
                "Your booking request for %s (%s) has been received!");

        createBookingNotification(eventDTO, title, message, Notification.TYPE_BOOKING_CREATED);
        log.info("Created notification for booking created: {}", eventDTO.getBookingId());
    }

    /**
     * Handle booking confirmed event.
     * Notifies user that their booking has been confirmed.
     */
    private void handleBookingConfirmed(BookingEventDTO eventDTO) {
        log.info("Processing booking confirmed event for pet: {}", eventDTO.getPetName());

        if (eventDTO.getUserId() == null) {
            log.warn("No user ID in booking event, skipping notification");
            return;
        }

        String title = "Booking Confirmed";
        String message = buildBookingMessage(eventDTO, 
                "Great news! Your booking for %s (%s) is confirmed!");

        createBookingNotification(eventDTO, title, message, Notification.TYPE_BOOKING_CONFIRMED);
        log.info("Created notification for booking confirmed: {}", eventDTO.getBookingId());
    }

    /**
     * Handle booking cancelled event.
     * Notifies user that their booking has been cancelled.
     */
    private void handleBookingCancelled(BookingEventDTO eventDTO) {
        log.info("Processing booking cancelled event for pet: {}", eventDTO.getPetName());

        if (eventDTO.getUserId() == null) {
            log.warn("No user ID in booking event, skipping notification");
            return;
        }

        String title = "Booking Cancelled";
        String message = String.format("Your booking for %s (%s) has been cancelled. " +
                "If you didn't request this cancellation, please contact us.",
                eventDTO.getPetName(), eventDTO.getPetSpecies());

        createBookingNotification(eventDTO, title, message, Notification.TYPE_BOOKING_CANCELLED);
        log.info("Created notification for booking cancelled: {}", eventDTO.getBookingId());
    }

    /**
     * Handle booking completed event.
     * Notifies user that their stay has been completed.
     */
    private void handleBookingCompleted(BookingEventDTO eventDTO) {
        log.info("Processing booking completed event for pet: {}", eventDTO.getPetName());

        if (eventDTO.getUserId() == null) {
            log.warn("No user ID in booking event, skipping notification");
            return;
        }

        String title = "Stay Completed - Thank You!";
        String message = String.format("Thank you for choosing Pet Hotel! " +
                "We hope %s (%s) enjoyed their %d-day stay. We'd love to see you again!",
                eventDTO.getPetName(), 
                eventDTO.getPetSpecies(),
                eventDTO.getTotalDays() != null ? eventDTO.getTotalDays() : 0);

        createBookingNotification(eventDTO, title, message, Notification.TYPE_BOOKING_COMPLETED);
        log.info("Created notification for booking completed: {}", eventDTO.getBookingId());
    }

    /**
     * Handle booking updated event.
     * Notifies user that their booking has been modified.
     */
    private void handleBookingUpdated(BookingEventDTO eventDTO) {
        log.info("Processing booking updated event for pet: {}", eventDTO.getPetName());

        if (eventDTO.getUserId() == null) {
            log.warn("No user ID in booking event, skipping notification");
            return;
        }

        String title = "Booking Updated";
        String message = buildBookingMessage(eventDTO, 
                "Your booking for %s (%s) has been updated.");

        createBookingNotification(eventDTO, title, message, Notification.TYPE_BOOKING_UPDATED);
        log.info("Created notification for booking updated: {}", eventDTO.getBookingId());
    }

    /**
     * Builds a detailed booking message with dates and room info.
     */
    private String buildBookingMessage(BookingEventDTO eventDTO, String template) {
        StringBuilder message = new StringBuilder();
        message.append(String.format(template, eventDTO.getPetName(), eventDTO.getPetSpecies()));
        
        // Add booking details
        message.append("\n\nBooking Details:");
        
        if (eventDTO.getRoomType() != null) {
            message.append("\n- Room: ").append(eventDTO.getRoomType());
        }
        
        if (eventDTO.getCheckInDate() != null) {
            message.append("\n- Check-in: ").append(eventDTO.getCheckInDate().format(DATE_FORMATTER));
        }
        
        if (eventDTO.getCheckOutDate() != null) {
            message.append("\n- Check-out: ").append(eventDTO.getCheckOutDate().format(DATE_FORMATTER));
        }
        
        if (eventDTO.getTotalDays() != null) {
            message.append("\n- Duration: ").append(eventDTO.getTotalDays()).append(" day(s)");
        }
        
        return message.toString();
    }

    /**
     * Creates a booking notification using the notification service.
     */
    private void createBookingNotification(BookingEventDTO eventDTO, String title, String message, String type) {
        notificationService.createBookingNotification(
                eventDTO.getUserId(),
                title,
                message,
                type,
                eventDTO.getBookingId()
        );
    }

    /**
     * Fallback method when circuit breaker is open.
     */
    public void handleEventFallback(BookingEventDTO event, Throwable t) {
        log.error("Circuit breaker open - failed to process booking event: bookingId={}, error={}",
                event.getBookingId(), t.getMessage());
        // Could implement retry logic or store for later processing
    }
}
