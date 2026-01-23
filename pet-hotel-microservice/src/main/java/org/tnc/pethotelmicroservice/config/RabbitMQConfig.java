package org.tnc.pethotelmicroservice.config;

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
 * RabbitMQ Configuration for Pet Hotel Microservice.
 * Configures exchanges, queues, and bindings for booking events.
 */
@Configuration
public class RabbitMQConfig {

    // ============ Message Converter ============
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    // ============ Exchanges ============
    @Bean
    public TopicExchange bookingEventsExchange() {
        return new TopicExchange(RabbitMQConstants.BOOKING_EVENTS_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange bookingDeadLetterExchange() {
        return new DirectExchange(RabbitMQConstants.BOOKING_DEAD_LETTER_EXCHANGE, true, false);
    }

    // ============ Queues ============
    @Bean
    public Queue bookingNotificationsQueue() {
        return QueueBuilder.durable(RabbitMQConstants.BOOKING_NOTIFICATIONS_QUEUE)
                .withArgument("x-dead-letter-exchange", RabbitMQConstants.BOOKING_DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", RabbitMQConstants.BOOKING_NOTIFICATIONS_DLQ)
                .build();
    }

    @Bean
    public Queue bookingNotificationsDLQ() {
        return QueueBuilder.durable(RabbitMQConstants.BOOKING_NOTIFICATIONS_DLQ).build();
    }

    // ============ Bindings ============
    @Bean
    public Binding bookingNotificationsBinding() {
        return BindingBuilder
                .bind(bookingNotificationsQueue())
                .to(bookingEventsExchange())
                .with(RabbitMQConstants.ROUTING_KEY_BOOKING_ALL);
    }

    @Bean
    public Binding bookingNotificationsDLQBinding() {
        return BindingBuilder
                .bind(bookingNotificationsDLQ())
                .to(bookingDeadLetterExchange())
                .with(RabbitMQConstants.BOOKING_NOTIFICATIONS_DLQ);
    }
}
