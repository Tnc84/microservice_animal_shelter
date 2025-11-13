package com.tnc.animals.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for RabbitMQConfig
 * Tests RabbitMQ configuration beans and their properties
 */
@ExtendWith(MockitoExtension.class)
class RabbitMQConfigTest {

    private RabbitMQConfig rabbitMQConfig;

    @Test
    @DisplayName("Should have correct exchange name constant")
    void exchangeName_ShouldBeCorrect() {
        assertThat(RabbitMQConfig.ANIMAL_EVENTS_EXCHANGE).isEqualTo("animal.events.exchange");
    }

    @Test
    @DisplayName("Should have correct queue name constants")
    void queueNames_ShouldBeCorrect() {
        assertThat(RabbitMQConfig.USER_NOTIFICATIONS_QUEUE).isEqualTo("user.notifications.queue");
        assertThat(RabbitMQConfig.SHELTER_UPDATES_QUEUE).isEqualTo("shelter.updates.queue");
    }

    @Test
    @DisplayName("Should have correct routing key constants")
    void routingKeys_ShouldBeCorrect() {
        assertThat(RabbitMQConfig.ROUTING_KEY_ANIMAL_CREATED).isEqualTo("animal.created");
        assertThat(RabbitMQConfig.ROUTING_KEY_ANIMAL_UPDATED).isEqualTo("animal.updated");
        assertThat(RabbitMQConfig.ROUTING_KEY_ANIMAL_ADOPTED).isEqualTo("animal.adopted");
        assertThat(RabbitMQConfig.ROUTING_KEY_ANIMAL_DELETED).isEqualTo("animal.deleted");
    }

    @Test
    @DisplayName("Should create JSON message converter")
    void jsonMessageConverter_ShouldBeCreated() {
        // Given
        rabbitMQConfig = new RabbitMQConfig();

        // When
        MessageConverter converter = rabbitMQConfig.jsonMessageConverter();

        // Then
        assertThat(converter).isNotNull();
        assertThat(converter).isInstanceOf(Jackson2JsonMessageConverter.class);
    }

    @Test
    @DisplayName("Should create RabbitTemplate with correct configuration")
    void rabbitTemplate_ShouldBeConfiguredCorrectly() {
        // Given
        rabbitMQConfig = new RabbitMQConfig();
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);

        // When
        RabbitTemplate template = rabbitMQConfig.rabbitTemplate(connectionFactory);

        // Then
        assertThat(template).isNotNull();
        assertThat(template.getMessageConverter()).isInstanceOf(Jackson2JsonMessageConverter.class);
    }

    @Test
    @DisplayName("Should create listener container factory")
    void rabbitListenerContainerFactory_ShouldBeConfiguredCorrectly() {
        // Given
        rabbitMQConfig = new RabbitMQConfig();
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);

        // When
        SimpleRabbitListenerContainerFactory factory = rabbitMQConfig.rabbitListenerContainerFactory(connectionFactory);

        // Then
        assertThat(factory).isNotNull();
    }

    @Test
    @DisplayName("Should create topic exchange with correct properties")
    void animalEventsExchange_ShouldBeConfiguredCorrectly() {
        // Given
        rabbitMQConfig = new RabbitMQConfig();

        // When
        TopicExchange exchange = rabbitMQConfig.animalEventsExchange();

        // Then
        assertThat(exchange).isNotNull();
        assertThat(exchange.getName()).isEqualTo(RabbitMQConfig.ANIMAL_EVENTS_EXCHANGE);
        assertThat(exchange.isDurable()).isTrue();
        assertThat(exchange.isAutoDelete()).isFalse();
    }

    @Test
    @DisplayName("Should create user notifications queue with correct properties")
    void userNotificationsQueue_ShouldBeConfiguredCorrectly() {
        // Given
        rabbitMQConfig = new RabbitMQConfig();

        // When
        Queue queue = rabbitMQConfig.userNotificationsQueue();

        // Then
        assertThat(queue).isNotNull();
        assertThat(queue.getName()).isEqualTo(RabbitMQConfig.USER_NOTIFICATIONS_QUEUE);
        assertThat(queue.isDurable()).isTrue();
        assertThat(queue.getArguments()).containsEntry("x-dead-letter-exchange", "animal.events.dlx");
        assertThat(queue.getArguments()).containsEntry("x-dead-letter-routing-key", "user.notifications.dlq");
    }

    @Test
    @DisplayName("Should create shelter updates queue with correct properties")
    void shelterUpdatesQueue_ShouldBeConfiguredCorrectly() {
        // Given
        rabbitMQConfig = new RabbitMQConfig();

        // When
        Queue queue = rabbitMQConfig.shelterUpdatesQueue();

        // Then
        assertThat(queue).isNotNull();
        assertThat(queue.getName()).isEqualTo(RabbitMQConfig.SHELTER_UPDATES_QUEUE);
        assertThat(queue.isDurable()).isTrue();
        assertThat(queue.getArguments()).containsEntry("x-dead-letter-exchange", "animal.events.dlx");
        assertThat(queue.getArguments()).containsEntry("x-dead-letter-routing-key", "shelter.updates.dlq");
    }

    @Test
    @DisplayName("Should create dead letter exchange")
    void deadLetterExchange_ShouldBeConfiguredCorrectly() {
        // Given
        rabbitMQConfig = new RabbitMQConfig();

        // When
        TopicExchange exchange = rabbitMQConfig.deadLetterExchange();

        // Then
        assertThat(exchange).isNotNull();
        assertThat(exchange.getName()).isEqualTo("animal.events.dlx");
        assertThat(exchange.isDurable()).isTrue();
        assertThat(exchange.isAutoDelete()).isFalse();
    }

    @Test
    @DisplayName("Should create dead letter queues")
    void deadLetterQueues_ShouldBeConfiguredCorrectly() {
        // Given
        rabbitMQConfig = new RabbitMQConfig();

        // When
        Queue userDlq = rabbitMQConfig.userNotificationsDlq();
        Queue shelterDlq = rabbitMQConfig.shelterUpdatesDlq();

        // Then
        assertThat(userDlq).isNotNull();
        assertThat(userDlq.getName()).isEqualTo("user.notifications.dlq");
        assertThat(userDlq.isDurable()).isTrue();

        assertThat(shelterDlq).isNotNull();
        assertThat(shelterDlq.getName()).isEqualTo("shelter.updates.dlq");
        assertThat(shelterDlq.isDurable()).isTrue();
    }

    @Test
    @DisplayName("Should create bindings with correct routing patterns")
    void bindings_ShouldBeConfiguredCorrectly() {
        // Given
        rabbitMQConfig = new RabbitMQConfig();

        // When
        Binding userBinding = rabbitMQConfig.userNotificationsBinding();
        Binding shelterBinding = rabbitMQConfig.shelterUpdatesBinding();

        // Then
        assertThat(userBinding).isNotNull();
        assertThat(userBinding.getRoutingKey()).isEqualTo("animal.*");

        assertThat(shelterBinding).isNotNull();
        assertThat(shelterBinding.getRoutingKey()).isEqualTo("animal.*");
    }

    @Test
    @DisplayName("Should create dead letter queue bindings")
    void deadLetterBindings_ShouldBeConfiguredCorrectly() {
        // Given
        rabbitMQConfig = new RabbitMQConfig();

        // When
        Binding userDlqBinding = rabbitMQConfig.userNotificationsDlqBinding();
        Binding shelterDlqBinding = rabbitMQConfig.shelterUpdatesDlqBinding();

        // Then
        assertThat(userDlqBinding).isNotNull();
        assertThat(userDlqBinding.getRoutingKey()).isEqualTo("user.notifications.dlq");

        assertThat(shelterDlqBinding).isNotNull();
        assertThat(shelterDlqBinding.getRoutingKey()).isEqualTo("shelter.updates.dlq");
    }

    @Test
    @DisplayName("Should have all required constants defined")
    void constants_ShouldBeDefined() {
        // Exchange
        assertThat(RabbitMQConfig.ANIMAL_EVENTS_EXCHANGE).isNotNull();
        
        // Queues
        assertThat(RabbitMQConfig.USER_NOTIFICATIONS_QUEUE).isNotNull();
        assertThat(RabbitMQConfig.SHELTER_UPDATES_QUEUE).isNotNull();
        
        // Routing Keys
        assertThat(RabbitMQConfig.ROUTING_KEY_ANIMAL_CREATED).isNotNull();
        assertThat(RabbitMQConfig.ROUTING_KEY_ANIMAL_UPDATED).isNotNull();
        assertThat(RabbitMQConfig.ROUTING_KEY_ANIMAL_ADOPTED).isNotNull();
        assertThat(RabbitMQConfig.ROUTING_KEY_ANIMAL_DELETED).isNotNull();
    }

    @Test
    @DisplayName("Should create all beans without errors")
    void allBeans_ShouldBeCreatedSuccessfully() {
        // Given
        rabbitMQConfig = new RabbitMQConfig();
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);

        // When & Then - All beans should be created without exceptions
        assertThat(rabbitMQConfig.jsonMessageConverter()).isNotNull();
        assertThat(rabbitMQConfig.rabbitTemplate(connectionFactory)).isNotNull();
        assertThat(rabbitMQConfig.rabbitListenerContainerFactory(connectionFactory)).isNotNull();
        assertThat(rabbitMQConfig.animalEventsExchange()).isNotNull();
        assertThat(rabbitMQConfig.userNotificationsQueue()).isNotNull();
        assertThat(rabbitMQConfig.shelterUpdatesQueue()).isNotNull();
        assertThat(rabbitMQConfig.deadLetterExchange()).isNotNull();
        assertThat(rabbitMQConfig.userNotificationsDlq()).isNotNull();
        assertThat(rabbitMQConfig.shelterUpdatesDlq()).isNotNull();
        assertThat(rabbitMQConfig.userNotificationsBinding()).isNotNull();
        assertThat(rabbitMQConfig.shelterUpdatesBinding()).isNotNull();
        assertThat(rabbitMQConfig.userNotificationsDlqBinding()).isNotNull();
        assertThat(rabbitMQConfig.shelterUpdatesDlqBinding()).isNotNull();
    }

    private ConnectionFactory mock(Class<ConnectionFactory> connectionFactoryClass) {
        return org.mockito.Mockito.mock(ConnectionFactory.class);
    }
}
