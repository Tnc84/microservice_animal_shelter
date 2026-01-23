package com.tnc.events.constants;

/**
 * Shared RabbitMQ constants for all microservices.
 * Ensures consistency in exchange names, queue names, and routing keys.
 */
public final class RabbitMQConstants {

    private RabbitMQConstants() {
        // Prevent instantiation
    }

    // ============ Exchanges ============
    public static final String ANIMAL_EVENTS_EXCHANGE = "animal.events.exchange";
    public static final String DEAD_LETTER_EXCHANGE = "animal.events.dlx";

    // ============ Queues ============
    public static final String USER_NOTIFICATIONS_QUEUE = "user.notifications.queue";
    public static final String SHELTER_UPDATES_QUEUE = "shelter.updates.queue";

    // Dead Letter Queues
    public static final String USER_NOTIFICATIONS_DLQ = "user.notifications.dlq";
    public static final String SHELTER_UPDATES_DLQ = "shelter.updates.dlq";

    // ============ Routing Keys ============
    public static final String ROUTING_KEY_ANIMAL_CREATED = "animal.created";
    public static final String ROUTING_KEY_ANIMAL_UPDATED = "animal.updated";
    public static final String ROUTING_KEY_ANIMAL_ADOPTED = "animal.adopted";
    public static final String ROUTING_KEY_ANIMAL_DELETED = "animal.deleted";

    // Wildcard patterns
    public static final String ROUTING_KEY_ANIMAL_ALL = "animal.*";
}
