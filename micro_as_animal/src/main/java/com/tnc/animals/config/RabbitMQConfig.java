package com.tnc.animals.config;

import com.tnc.events.constants.RabbitMQConstants;
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
        return new TopicExchange(RabbitMQConstants.ANIMAL_EVENTS_EXCHANGE, true, false);
    }
    
    /**
     * Queue for user notifications.
     * Durable queue that survives broker restarts.
     */
    @Bean
    public Queue userNotificationsQueue() {
        return QueueBuilder.durable(RabbitMQConstants.USER_NOTIFICATIONS_QUEUE)
                .withArgument("x-dead-letter-exchange", RabbitMQConstants.DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", RabbitMQConstants.USER_NOTIFICATIONS_DLQ)
                .build();
    }
    
    /**
     * Queue for shelter updates.
     * Durable queue that survives broker restarts.
     */
    @Bean
    public Queue shelterUpdatesQueue() {
        return QueueBuilder.durable(RabbitMQConstants.SHELTER_UPDATES_QUEUE)
                .withArgument("x-dead-letter-exchange", RabbitMQConstants.DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", RabbitMQConstants.SHELTER_UPDATES_DLQ)
                .build();
    }
    
    /**
     * Dead letter exchange for failed messages.
     */
    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(RabbitMQConstants.DEAD_LETTER_EXCHANGE, true, false);
    }
    
    /**
     * Dead letter queue for user notifications.
     */
    @Bean
    public Queue userNotificationsDlq() {
        return QueueBuilder.durable(RabbitMQConstants.USER_NOTIFICATIONS_DLQ).build();
    }
    
    /**
     * Dead letter queue for shelter updates.
     */
    @Bean
    public Queue shelterUpdatesDlq() {
        return QueueBuilder.durable(RabbitMQConstants.SHELTER_UPDATES_DLQ).build();
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
                .with(RabbitMQConstants.ROUTING_KEY_ANIMAL_ALL);
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
                .with(RabbitMQConstants.ROUTING_KEY_ANIMAL_ALL);
    }
    
    /**
     * Bind dead letter queues to dead letter exchange.
     */
    @Bean
    public Binding userNotificationsDlqBinding() {
        return BindingBuilder
                .bind(userNotificationsDlq())
                .to(deadLetterExchange())
                .with(RabbitMQConstants.USER_NOTIFICATIONS_DLQ);
    }
    
    @Bean
    public Binding shelterUpdatesDlqBinding() {
        return BindingBuilder
                .bind(shelterUpdatesDlq())
                .to(deadLetterExchange())
                .with(RabbitMQConstants.SHELTER_UPDATES_DLQ);
    }
}
