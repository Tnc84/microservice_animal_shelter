# RabbitMQ Event Flow Architecture

## Complete Flow: Angular Frontend → RabbitMQ Event Processing

### Example Scenario: User Creates a New Animal

```
┌─────────────────────────────────────────────────────────────────┐
│                    COMPLETE FLOW DIAGRAM                        │
└─────────────────────────────────────────────────────────────────┘

1. ANGULAR FRONTEND (Port 4200)
   │
   │ User fills form: "Add New Animal - Buddy, Golden Retriever"
   │
   └─> POST http://localhost:8765/animal-microservice/animals
       Headers: Authorization: Bearer <JWT_TOKEN>
       Body: { name: "Buddy", species: "Dog", breed: "Golden Retriever", ... }

2. API GATEWAY (Port 8765 - Spring Cloud Gateway)
   │
   │ • Validates JWT token
   │ • Routes to: lb://animal-microservice (via Eureka)
   │ • Rewrites path: /animal-microservice/animals → /animals
   │
   └─> Forward to Animal Service (discovered via Eureka)

3. ANIMAL SERVICE (Port 8093)
   │
   │ AnimalController.add()
   │   ↓
   │ AnimalServiceImpl.add()
   │   ↓
   │ @Transactional
   │   ├─> Save to MySQL database (animal table)
   │   │   └─> Transaction COMMITS ✅
   │   │
   │   └─> publishAnimalCreatedEvent() [AFTER DB COMMIT]
   │       ↓
   │       AnimalEventPublisher.publishAnimalCreated()
   │       ↓
   │       @CircuitBreaker + @Retry
   │       ↓
   │       RabbitTemplate.convertAndSend(
   │         Exchange: "animal.events.exchange"
   │         Routing Key: "animal.created"
   │         Message: AnimalEventDTO { animalId: 123, name: "Buddy", ... }
   │       )
   │
   └─> Return response to Gateway → Angular ✅

4. RABBITMQ BROKER
   │
   │ Topic Exchange: "animal.events.exchange"
   │   │
   │   ├─> Routing Key: "animal.created"
   │   │   │
   │   │   ├─> Queue: "shelter.updates.queue" (bound with "animal.*")
   │   │   │   └─> Message delivered to Shelter Service
   │   │   │
   │   │   └─> Queue: "user.notifications.queue" (bound with "animal.*")
   │   │       └─> Message delivered to User Service
   │
   └─> [Both queues receive the SAME event message]

5. SHELTER SERVICE (Port 8092) - ASYNCHRONOUS PROCESSING
   │
   │ @RabbitListener(queues = "shelter.updates.queue")
   │ AnimalEventListener.handleAnimalEvent()
   │   ↓
   │ @CircuitBreaker + @Transactional
   │   ↓
   │ switch (eventType) {
   │   case "ANIMAL_CREATED":
   │     └─> Find shelter by ID
   │         └─> shelter.setAnimalCount(shelter.getAnimalCount() + 1)
   │             └─> Save to MySQL (shelter table)
   │                 └─> Log: "Updated shelter Bucium animal count to 15"
   │ }
   │
   └─> Shelter statistics updated ✅

6. USER SERVICE (Port 8091) - ASYNCHRONOUS PROCESSING
   │
   │ @RabbitListener(queues = "user.notifications.queue")
   │ AnimalEventConsumer.handleAnimalEvent()
   │   ↓
   │ @CircuitBreaker + @Transactional
   │   ↓
   │ switch (eventType) {
   │   case "ANIMAL_CREATED":
   │     └─> notificationService.createNotification()
   │         └─> Save to MySQL (notification table)
   │             └─> Title: "New Animal Added"
   │             └─> Message: "A new Dog named Buddy has been added..."
   │             └─> User ID: 1 (shelter staff)
   │ }
   │
   └─> Notification created ✅

7. RESULT
   │
   ├─> User sees success message in Angular UI
   ├─> Shelter statistics automatically updated (animal count +1)
   └─> Shelter staff receives notification about new animal
```

## Important Points

### 1. Synchronous vs Asynchronous
- **Synchronous**: Angular → Gateway → Animal Service → Database → Response
- **Asynchronous**: Animal Service → RabbitMQ → Shelter & User Services (parallel)

### 2. Transaction Safety
- Events are published **AFTER** the database transaction commits
- If DB fails, no event is sent
- If RabbitMQ fails, the DB operation still succeeds (circuit breaker protects)

### 3. Event Routing
- **Topic Exchange**: `animal.events.exchange`
- **Routing Pattern**: `animal.*` matches all animal events
- Both queues receive all events, but each service processes only what it needs

### 4. Resilience
- **Circuit Breaker**: If RabbitMQ is down, events are logged but don't block the main flow
- **Retry**: Automatic retries for failed message publishing
- **Dead Letter Queue**: Failed messages go to DLQ for manual review

### 5. Service Discovery
- **Eureka** (Port 8761): All services register and discover each other
- Gateway uses `lb://service-name` for load balancing

## Event Types and Their Effects

| Event Type | Animal Service | Shelter Service | User Service |
|------------|---------------|-----------------|--------------|
| **ANIMAL_CREATED** | Saves animal | `animalCount++` | Creates notification |
| **ANIMAL_UPDATED** | Updates animal | Logs update | Creates notification |
| **ANIMAL_ADOPTED** | Marks as adopted | `adoptionCount++`, `animalCount--` | Creates notification (user + staff) |
| **ANIMAL_DELETED** | Deletes animal | `animalCount--` | Creates notification |

## Example: User Adopts an Animal

```
1. Angular: POST /animal-microservice/animals/123/adopt { userId: 456 }
2. Animal Service: Updates animal status → Publishes "ANIMAL_ADOPTED" event
3. RabbitMQ: Routes to both queues
4. Shelter Service: Updates statistics (adoption count +1, animal count -1)
5. User Service: Creates 2 notifications:
   - For user 456: "Congratulations! You adopted Buddy"
   - For staff: "Buddy has been adopted by user@example.com"
```

## Architecture Benefits

This architecture ensures:
- ✅ **Loose Coupling**: Services don't directly call each other
- ✅ **Scalability**: Services can process events independently
- ✅ **Reliability**: Circuit breakers prevent cascading failures
- ✅ **Consistency**: Events are published only after successful DB commits

## RabbitMQ Configuration

### Exchange
- **Name**: `animal.events.exchange`
- **Type**: Topic Exchange
- **Durable**: Yes (survives broker restarts)

### Queues
- **shelter.updates.queue**: For shelter statistics updates
- **user.notifications.queue**: For user notifications
- Both queues are bound with routing pattern: `animal.*`

### Dead Letter Queues
- Failed messages are routed to DLQ for manual review
- Prevents message loss and allows retry mechanisms

## Service Ports

- **API Gateway**: 8765
- **Eureka Server**: 8761
- **Animal Service**: 8093
- **Shelter Service**: 8092
- **User Service**: 8091
- **RabbitMQ**: 5672
- **Angular Frontend**: 4200

## Shared Library

All services use `tnc-events-lib` for:
- `AnimalEventDTO`: Shared event data structure
- `RabbitMQConstants`: Shared queue/exchange names and routing keys

This ensures consistency across all microservices and prevents configuration drift.
