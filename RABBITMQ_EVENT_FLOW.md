# RabbitMQ Event Flow Architecture

This document describes the event-driven communication between microservices using RabbitMQ.

---

## Flow 1: Animal Events (Animal → Shelter & User Services)

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

## Animal Events - RabbitMQ Details

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

---

## Flow 2: Booking Events (Pet Hotel → User Service)

### Example Scenario: User Books a Room for Their Dog (3-Day Stay)

```
┌─────────────────────────────────────────────────────────────────┐
│          PET HOTEL BOOKING FLOW WITH RABBITMQ                   │
└─────────────────────────────────────────────────────────────────┘

1. ANGULAR FRONTEND (Port 4200)
   │
   │ User fills booking form:
   │   - Pet: "Max" (Dog, Golden Retriever)
   │   - Room Type: "Standard"
   │   - Check-in: 2026-01-25
   │   - Check-out: 2026-01-28 (3 days)
   │
   └─> POST http://localhost:8765/pet-hotel/bookings
       Headers: Authorization: Bearer <JWT_TOKEN>
       Body: {
         "userId": 456,
         "roomId": 10,
         "petName": "Max",
         "petSpecies": "Dog",
         "petBreed": "Golden Retriever",
         "checkInDate": "2026-01-25T14:00:00",
         "checkOutDate": "2026-01-28T11:00:00"
       }

2. API GATEWAY (Port 8765)
   │
   │ • Validates JWT token (user authenticated)
   │ • Routes to: lb://pet-hotel (via Eureka)
   │
   └─> Forward to Pet Hotel Service

3. PET HOTEL SERVICE (Port 8094)
   │
   │ BookingController.createBooking()
   │   ↓
   │ BookingServiceImpl.createBooking()
   │   ↓
   │ @Transactional
   │   ├─> Check room availability (no overlapping bookings)
   │   ├─> Calculate totalDays = 3
   │   ├─> Set status = PENDING
   │   ├─> Save to MySQL (booking table)
   │   │   └─> Transaction COMMITS ✅
   │   │
   │   └─> bookingEventPublisher.publishBookingCreated(booking)
   │       ↓
   │       @CircuitBreaker + @Retry
   │       ↓
   │       RabbitTemplate.convertAndSend(
   │         Exchange: "booking.events.exchange"
   │         Routing Key: "booking.created"
   │         Message: BookingEventDTO {
   │           bookingId: 789,
   │           eventType: "BOOKING_CREATED",
   │           userId: 456,
   │           roomId: 10,
   │           roomType: "Standard",
   │           petName: "Max",
   │           petSpecies: "Dog",
   │           checkInDate: "2026-01-25T14:00:00",
   │           checkOutDate: "2026-01-28T11:00:00",
   │           totalDays: 3,
   │           bookingStatus: "PENDING"
   │         }
   │       )
   │
   └─> Return 201 Created to Gateway → Angular ✅

4. RABBITMQ BROKER (Port 5672)
   │
   │ Topic Exchange: "booking.events.exchange"
   │   │
   │   └─> Routing Key: "booking.created"
   │       │
   │       └─> Queue: "booking.notifications.queue" (bound with "booking.*")
   │           └─> Message delivered to User Service
   │
   └─> Event ready for consumption

5. USER SERVICE (Port 8091) - ASYNCHRONOUS PROCESSING
   │
   │ @RabbitListener(queues = "booking.notifications.queue")
   │ BookingEventConsumer.handleBookingEvent()
   │   ↓
   │ @CircuitBreaker + @Transactional
   │   ↓
   │ switch (eventType) {
   │   case "BOOKING_CREATED":
   │     └─> notificationService.createBookingNotification()
   │         └─> Save to MySQL (notification table)
   │             └─> Title: "Booking Received"
   │             └─> Message: "Your booking for Max (Dog) has been received!
   │                          Room: Standard
   │                          Check-in: Jan 25, 2026 at 2:00 PM
   │                          Check-out: Jan 28, 2026 at 11:00 AM
   │                          Duration: 3 days"
   │             └─> User ID: 456
   │ }
   │
   └─> User notification created ✅

6. RESULT
   │
   ├─> User sees booking confirmation in Angular UI
   ├─> User receives notification in their notification center
   └─> Booking stored with PENDING status (awaiting confirmation)
```

### Booking Lifecycle Events

| User Action | Pet Hotel Publishes | User Service Creates |
|-------------|--------------------|--------------------|
| Creates booking | `BOOKING_CREATED` | "Booking Received" notification |
| Confirms/Pays | `BOOKING_CONFIRMED` | "Booking Confirmed" notification |
| Cancels booking | `BOOKING_CANCELLED` | "Booking Cancelled" notification |
| Checks out | `BOOKING_COMPLETED` | "Thank You" notification |
| Modifies booking | `BOOKING_UPDATED` | "Booking Updated" notification |

### Example: Complete Booking Lifecycle

```
1. User creates booking → BOOKING_CREATED → "Your booking has been received"
2. User pays online → BOOKING_CONFIRMED → "Payment received, booking confirmed!"
3. Pet stays 3 days at hotel
4. User picks up pet → BOOKING_COMPLETED → "Thank you! We hope Max enjoyed the stay"
```

### Example: Cancelled Booking

```
1. User creates booking → BOOKING_CREATED → "Your booking has been received"
2. User cancels → BOOKING_CANCELLED → "Your booking has been cancelled"
```

---

## RabbitMQ Configuration Summary

### Animal Events Exchange
- **Exchange**: `animal.events.exchange` (Topic)
- **Queues**:
  - `shelter.updates.queue` → Shelter Service
  - `user.notifications.queue` → User Service
- **Routing**: `animal.*` (matches all animal events)

### Booking Events Exchange
- **Exchange**: `booking.events.exchange` (Topic)
- **Queues**:
  - `booking.notifications.queue` → User Service
- **Routing**: `booking.*` (matches all booking events)

### Dead Letter Queues
| Main Queue | Dead Letter Queue |
|------------|-------------------|
| `shelter.updates.queue` | `shelter.updates.dlq` |
| `user.notifications.queue` | `user.notifications.dlq` |
| `booking.notifications.queue` | `booking.notifications.dlq` |

---

## Service Ports

| Service | Port |
|---------|------|
| API Gateway | 8765 |
| Eureka Server | 8761 |
| User Service | 8091 |
| Shelter Service | 8092 |
| Animal Service | 8093 |
| Pet Hotel Service | 8094 |
| RabbitMQ | 5672 |
| RabbitMQ Management | 15672 |
| Angular Frontend | 4200 |

---

## Shared Library

All services use `tnc-events-lib` for:
- `AnimalEventDTO`: Animal event data structure
- `BookingEventDTO`: Booking event data structure
- `RabbitMQConstants`: Shared queue/exchange names and routing keys

This ensures consistency across all microservices and prevents configuration drift.

---

## Architecture Benefits

| Benefit | Description |
|---------|-------------|
| **Loose Coupling** | Services communicate via events, not direct calls |
| **Scalability** | Each service processes events independently |
| **Reliability** | Circuit breakers prevent cascading failures |
| **Consistency** | Events published only after DB commits |
| **Fault Tolerance** | DLQ captures failed messages for retry |
| **Observability** | Events provide audit trail of actions |
