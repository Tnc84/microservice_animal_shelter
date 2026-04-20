package org.tnc.pethotelmicroservice.events;

import com.tnc.events.booking.BookingEventDTO;
import com.tnc.events.constants.RabbitMQConstants;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.tnc.pethotelmicroservice.repository.entities.Booking;

import java.time.LocalDateTime;

/**
 * Service responsible for publishing booking events to RabbitMQ.
 * Uses circuit breaker and retry patterns for resilience.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Publishes a booking created event.
     */
    @CircuitBreaker(name = "rabbitmq", fallbackMethod = "publishEventFallback")
    @Retry(name = "rabbitmq")
    public void publishBookingCreated(Booking booking) {
        BookingEventDTO event = buildEvent(booking, BookingEventDTO.EVENT_BOOKING_CREATED);
        event.setDescription("New booking created for pet: " + booking.getPetName());
        publishEvent(event, RabbitMQConstants.ROUTING_KEY_BOOKING_CREATED);
    }

    /**
     * Publishes a booking confirmed event.
     */
    @CircuitBreaker(name = "rabbitmq", fallbackMethod = "publishEventFallback")
    @Retry(name = "rabbitmq")
    public void publishBookingConfirmed(Booking booking) {
        BookingEventDTO event = buildEvent(booking, BookingEventDTO.EVENT_BOOKING_CONFIRMED);
        event.setDescription("Booking confirmed for pet: " + booking.getPetName());
        publishEvent(event, RabbitMQConstants.ROUTING_KEY_BOOKING_CONFIRMED);
    }

    /**
     * Publishes a booking cancelled event.
     */
    @CircuitBreaker(name = "rabbitmq", fallbackMethod = "publishEventFallback")
    @Retry(name = "rabbitmq")
    public void publishBookingCancelled(Booking booking) {
        BookingEventDTO event = buildEvent(booking, BookingEventDTO.EVENT_BOOKING_CANCELLED);
        event.setDescription("Booking cancelled for pet: " + booking.getPetName());
        publishEvent(event, RabbitMQConstants.ROUTING_KEY_BOOKING_CANCELLED);
    }

    /**
     * Publishes a booking completed event.
     */
    @CircuitBreaker(name = "rabbitmq", fallbackMethod = "publishEventFallback")
    @Retry(name = "rabbitmq")
    public void publishBookingCompleted(Booking booking) {
        BookingEventDTO event = buildEvent(booking, BookingEventDTO.EVENT_BOOKING_COMPLETED);
        event.setDescription("Booking completed for pet: " + booking.getPetName());
        publishEvent(event, RabbitMQConstants.ROUTING_KEY_BOOKING_COMPLETED);
    }

    /**
     * Publishes a booking updated event.
     */
    @CircuitBreaker(name = "rabbitmq", fallbackMethod = "publishEventFallback")
    @Retry(name = "rabbitmq")
    public void publishBookingUpdated(Booking booking) {
        BookingEventDTO event = buildEvent(booking, BookingEventDTO.EVENT_BOOKING_UPDATED);
        event.setDescription("Booking updated for pet: " + booking.getPetName());
        publishEvent(event, RabbitMQConstants.ROUTING_KEY_BOOKING_UPDATED);
    }

    /**
     * Builds a BookingEventDTO from a Booking entity.
     */
    private BookingEventDTO buildEvent(Booking booking, String eventType) {
        return BookingEventDTO.builder()
                .bookingId(booking.getId())
                .eventType(eventType)
                .userId(booking.getUserId())
                .roomId(booking.getRoomId() != null ? booking.getRoomId().getId() : null)
                .roomType(booking.getRoomId() != null ? booking.getRoomId().getRoomType() : null)
                .petName(booking.getPetName())
                .petSpecies(booking.getPetSpecies())
                .petBreed(booking.getPetBreed())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .totalDays(booking.getTotalDays())
                .bookingStatus(booking.getStatus() != null ? booking.getStatus().name() : null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Sends the event to RabbitMQ.
     */
    private void publishEvent(BookingEventDTO event, String routingKey) {
        log.info("Publishing booking event: type={}, bookingId={}, routingKey={}",
                event.getEventType(), event.getBookingId(), routingKey);
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.BOOKING_EVENTS_EXCHANGE,
                routingKey,
                event
        );
        log.debug("Booking event published successfully: {}", event);
    }

    /**
     * Fallback method when circuit breaker is open or retries exhausted.
     */
    public void publishEventFallback(Booking booking, Throwable t) {
        log.error("Failed to publish booking event for booking ID: {}. Error: {}",
                booking.getId(), t.getMessage());
        // Could store failed events in a database for later retry
    }
}
