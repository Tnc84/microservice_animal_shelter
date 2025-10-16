<!-- 85780461-1294-4edf-897e-c534c7e44b7d 9d406dda-be49-439c-9a6e-13f540a0f398 -->
# RabbitMQ Integration for Animal Shelter Microservices

## Implementation Strategy

**Start simple with RabbitMQ only** - Add Kafka later when you need event streaming/analytics for the pet hotel expansion.

## What We'll Build

### 1. RabbitMQ Infrastructure

- Add RabbitMQ container to `docker-compose.yml`
- Configure exchanges, queues, and bindings using Spring AMQP

### 2. Event-Driven Communication Pattern

**Animal Service (Publisher):**

- Publishes events: `AnimalCreated`, `AnimalUpdated`, `AnimalAdopted`, `AnimalDeleted`
- Events published AFTER successful DB commit

**User Service (Consumer):**

- Consumes animal events
- Creates in-app notifications stored in DB
- New `Notification` entity with fields: userId, message, type, timestamp, read status

**Shelter Service (Consumer):**

- Consumes animal events
- Updates shelter statistics (animal count, available capacity)

### 3. Core Components (Each Microservice)

**Shared Event DTOs:**

- `AnimalEventDTO` - contains animalId, eventType, timestamp, shelterInfo, etc.

**Animal Service:**

- `RabbitMQConfig` - exchange/queue configuration
- `AnimalEventPublisher` - publishes events to RabbitMQ
- Modify `AnimalServiceImpl` to publish events after DB operations

**User Service:**

- Add `spring-boot-starter-amqp` dependency
- `Notification` entity and `NotificationRepository`
- `NotificationService` - business logic for notifications
- `AnimalEventConsumer` - listens to animal events, creates notifications
- `NotificationController` - REST endpoints to fetch user notifications

**Shelter Service:**

- `AnimalEventConsumer` - listens to animal events
- Update shelter logic to track animal counts

### 4. Configuration

- RabbitMQ connection settings in `application.yml` for each service
- Exchange: `animal.events.exchange` (topic exchange)
- Queues: `user.notifications.queue`, `shelter.updates.queue`
- Routing keys: `animal.created`, `animal.adopted`, etc.

## Why This Approach

**SOLID Principles:**

- **SRP**: Separate publisher/consumer classes, single responsibility
- **OCP**: Event-driven design allows adding new consumers without modifying publishers
- **DIP**: Services depend on message contracts (DTOs), not concrete implementations

**Best Practices:**

- Events published AFTER DB commit (consistency)
- Dead letter queues for failed messages
- Idempotent consumers (handle duplicate messages)
- Async processing doesn't block main operations

**Future-Proof:**

- Easy to add Kafka later for event streaming
- Easy to add new consumers (email service, analytics service)
- Scalable pattern for pet hotel service

## Database Considerations

**Synchronous DB operations remain unchanged** - RabbitMQ is ONLY for:

1. Service-to-service notifications
2. Side effects after successful DB commits
3. Decoupling services

**NOT for:**

- Direct database operations
- Transaction management
- Critical business logic requiring immediate consistency

### To-dos

- [ ] Add RabbitMQ container to docker-compose.yml with management UI
- [ ] Create shared event DTOs for animal events (AnimalEventDTO)
- [ ] Implement RabbitMQ publisher in Animal service (config, publisher, integrate with service layer)
- [ ] Add notification system to User service (entity, repository, service, consumer, controller)
- [ ] Implement event consumer in Shelter service to update statistics
- [ ] Add unit and integration tests for publishers and consumers