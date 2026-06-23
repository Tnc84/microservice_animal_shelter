package com.tnc.events.constants;

/**
 * Shared RabbitMQ constants for all microservices.
 * Ensures consistency in exchange names, queue names, and routing keys.
 */
public final class RabbitMQConstants {

    private RabbitMQConstants() {
        // Prevent instantiation
    }

    // ============ Animal Exchanges ============
    public static final String ANIMAL_EVENTS_EXCHANGE = "animal.events.exchange";
    public static final String DEAD_LETTER_EXCHANGE = "animal.events.dlx";

    // ============ Booking Exchanges ============
    public static final String BOOKING_EVENTS_EXCHANGE = "booking.events.exchange";
    public static final String BOOKING_DEAD_LETTER_EXCHANGE = "booking.events.dlx";

    // ============ Animal Queues ============
    public static final String USER_NOTIFICATIONS_QUEUE = "user.notifications.queue";
    public static final String SHELTER_UPDATES_QUEUE = "shelter.updates.queue";

    // Animal Dead Letter Queues
    public static final String USER_NOTIFICATIONS_DLQ = "user.notifications.dlq";
    public static final String SHELTER_UPDATES_DLQ = "shelter.updates.dlq";

    // ============ Booking Queues ============
    public static final String BOOKING_NOTIFICATIONS_QUEUE = "booking.notifications.queue";
    public static final String BOOKING_UPDATES_QUEUE = "booking.updates.queue";

    // Booking Dead Letter Queues
    public static final String BOOKING_NOTIFICATIONS_DLQ = "booking.notifications.dlq";
    public static final String BOOKING_UPDATES_DLQ = "booking.updates.dlq";

    // ============ Animal Routing Keys ============
    public static final String ROUTING_KEY_ANIMAL_CREATED = "animal.created";
    public static final String ROUTING_KEY_ANIMAL_UPDATED = "animal.updated";
    public static final String ROUTING_KEY_ANIMAL_ADOPTED = "animal.adopted";
    public static final String ROUTING_KEY_ANIMAL_DELETED = "animal.deleted";

    // Animal Wildcard patterns
    public static final String ROUTING_KEY_ANIMAL_ALL = "animal.*";

    // ============ Booking Routing Keys ============
    public static final String ROUTING_KEY_BOOKING_CREATED = "booking.created";
    public static final String ROUTING_KEY_BOOKING_CONFIRMED = "booking.confirmed";
    public static final String ROUTING_KEY_BOOKING_CANCELLED = "booking.cancelled";
    public static final String ROUTING_KEY_BOOKING_COMPLETED = "booking.completed";
    public static final String ROUTING_KEY_BOOKING_UPDATED = "booking.updated";

    // Booking Wildcard patterns
    public static final String ROUTING_KEY_BOOKING_ALL = "booking.*";
}
