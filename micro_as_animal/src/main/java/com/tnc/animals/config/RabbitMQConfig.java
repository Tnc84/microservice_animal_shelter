package com.tnc.animals.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for Animal microservice.
 * Configures exchanges, queues, and message converters for event-driven communication.
 */
@Configuration
public class RabbitMQConfig {

    // Exchange names
    public static final String ANIMAL_EVENTS_EXCHANGE = "animal.events.exchange";
    
    // Queue names
    public static final String USER_NOTIFICATIONS_QUEUE = "user.notifications.queue";
    public static final String SHELTER_UPDATES_QUEUE = "shelter.updates.queue";
    
    // Routing keys
    public static final String ROUTING_KEY_ANIMAL_CREATED = "animal.created";
    public static final String ROUTING_KEY_ANIMAL_UPDATED = "animal.updated";
    public static final String ROUTING_KEY_ANIMAL_ADOPTED = "animal.adopted";
    public static final String ROUTING_KEY_ANIMAL_DELETED = "animal.deleted";
    
    /**
     * Configure JSON message converter for serializing/deserializing messages.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    /**
     * Configure RabbitTemplate with JSON converter and publisher confirmations.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        template.setMandatory(true);
        return template;
    }
    
    /**
     * Configure listener container factory for consumers.
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }
    
    /**
     * Topic exchange for animal events.
     * Allows routing based on routing key patterns.
     */
    @Bean
    public TopicExchange animalEventsExchange() {
        return new TopicExchange(ANIMAL_EVENTS_EXCHANGE, true, false);
    }
    
    /**
     * Queue for user notifications.
     * Durable queue that survives broker restarts.
     */
    @Bean
    public Queue userNotificationsQueue() {
        return QueueBuilder.durable(USER_NOTIFICATIONS_QUEUE)
                .withArgument("x-dead-letter-exchange", "animal.events.dlx")
                .withArgument("x-dead-letter-routing-key", "user.notifications.dlq")
                .build();
    }
    
    /**
     * Queue for shelter updates.
     * Durable queue that survives broker restarts.
     */
    @Bean
    public Queue shelterUpdatesQueue() {
        return QueueBuilder.durable(SHELTER_UPDATES_QUEUE)
                .withArgument("x-dead-letter-exchange", "animal.events.dlx")
                .withArgument("x-dead-letter-routing-key", "shelter.updates.dlq")
                .build();
    }
    
    /**
     * Dead letter exchange for failed messages.
     */
    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange("animal.events.dlx", true, false);
    }
    
    /**
     * Dead letter queue for user notifications.
     */
    @Bean
    public Queue userNotificationsDlq() {
        return QueueBuilder.durable("user.notifications.dlq").build();
    }
    
    /**
     * Dead letter queue for shelter updates.
     */
    @Bean
    public Queue shelterUpdatesDlq() {
        return QueueBuilder.durable("shelter.updates.dlq").build();
    }
    
    /**
     * Bind user notifications queue to exchange.
     * Listens to all animal events for user notifications.
     */
    @Bean
    public Binding userNotificationsBinding() {
        return BindingBuilder
                .bind(userNotificationsQueue())
                .to(animalEventsExchange())
                .with("animal.*");
    }
    
    /**
     * Bind shelter updates queue to exchange.
     * Listens to all animal events for shelter statistics updates.
     */
    @Bean
    public Binding shelterUpdatesBinding() {
        return BindingBuilder
                .bind(shelterUpdatesQueue())
                .to(animalEventsExchange())
                .with("animal.*");
    }
    
    /**
     * Bind dead letter queues to dead letter exchange.
     */
    @Bean
    public Binding userNotificationsDlqBinding() {
        return BindingBuilder
                .bind(userNotificationsDlq())
                .to(deadLetterExchange())
                .with("user.notifications.dlq");
    }
    
    @Bean
    public Binding shelterUpdatesDlqBinding() {
        return BindingBuilder
                .bind(shelterUpdatesDlq())
                .to(deadLetterExchange())
                .with("shelter.updates.dlq");
    }
}
