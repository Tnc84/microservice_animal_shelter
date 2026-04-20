package com.tnc.shelter.config;

import com.tnc.events.constants.RabbitMQConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for Shelter microservice.
 * Configures message converters and listener factories for consuming animal events.
 */
@Configuration
public class RabbitMQConfig {

    /**
     * Configure JSON message converter for deserializing messages.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Configure listener container factory for consumers with JSON converter.
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setDefaultRequeueRejected(false); // Don't requeue failed messages (use DLQ)
        return factory;
    }

    /**
     * Declare the exchange (must match Animal Service).
     */
    @Bean
    public TopicExchange animalEventsExchange() {
        return new TopicExchange(RabbitMQConstants.ANIMAL_EVENTS_EXCHANGE, true, false);
    }

    /**
     * Declare the queue for shelter updates.
     */
    @Bean
    public Queue shelterUpdatesQueue() {
        return QueueBuilder.durable(RabbitMQConstants.SHELTER_UPDATES_QUEUE)
                .withArgument("x-dead-letter-exchange", RabbitMQConstants.DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", RabbitMQConstants.SHELTER_UPDATES_DLQ)
                .build();
    }

    /**
     * Bind shelter updates queue to exchange with wildcard routing.
     */
    @Bean
    public Binding shelterUpdatesBinding() {
        return BindingBuilder
                .bind(shelterUpdatesQueue())
                .to(animalEventsExchange())
                .with(RabbitMQConstants.ROUTING_KEY_ANIMAL_ALL);
    }
}
