# Pet Hotel Microservice Integration Plan

## Overview

Create a new **Pet Hotel** microservice that manages 21 pet rooms with booking functionality. Pet owners must create accounts in `micro_as_user` first, then can book rooms for specific date periods.

## Architecture Integration

### Service Details
- **Service Name:** `pet-hotel-microservice`
- **Port:** `8096` (next available port)
- **Database:** `pet_hotel` (new MySQL database)
- **Eureka Service ID:** `pet-hotel-microservice`
- **API Gateway Route:** `/pet-hotel-microservice/**`

### Technology Stack (matching existing services)
- Java 17
- Spring Boot 3.5.5
- Spring Cloud 2025.0.0
- MySQL 8.0
- Spring Data JPA
- Spring Security (via `tnc-security-lib`)
- Resilience4j (via `tnc-resilience-lib`)
- Swagger/OpenAPI (via `tnc-swagger-lib`)
- RabbitMQ (for future extensibility)
- Eureka Client (service discovery)

## Database Schema

### New Database: `pet_hotel`

#### `room` Table
- `id` (BIGINT, PK, AUTO_INCREMENT)
- `room_number` (INT, UNIQUE, NOT NULL) - Room numbers 1-21
- `room_type` (VARCHAR) - Optional: Standard, Deluxe, Suite
- `capacity` (INT) - Number of pets per room
- `is_available` (BOOLEAN, DEFAULT TRUE)
- `description` (TEXT) - Optional room description
- `created_at` (DATETIME)
- `updated_at` (DATETIME)
- Indexes: `idx_room_number`, `idx_room_available`

#### `booking` Table
- `id` (BIGINT, PK, AUTO_INCREMENT)
- `user_id` (VARCHAR) - References user from `micro_as_user`
- `room_id` (BIGINT, FK to `room.id`)
- `pet_name` (VARCHAR, NOT NULL)
- `pet_species` (VARCHAR) - Optional
- `pet_breed` (VARCHAR) - Optional
- `check_in_date` (DATE, NOT NULL)
- `check_out_date` (DATE, NOT NULL)
- `total_days` (INT) - Calculated field
- `status` (ENUM: PENDING, CONFIRMED, CANCELLED, COMPLETED)
- `special_instructions` (TEXT) - Optional
- `created_at` (DATETIME)
- `updated_at` (DATETIME)
- Indexes: `idx_booking_user_id`, `idx_booking_room_id`, `idx_booking_dates`, `idx_booking_status`
- Constraints: `check_out_date > check_in_date`

## Microservice Structure

### Package Structure
```
micro_as_pet_hotel/
├── src/main/java/com/tnc/pethotel/
│   ├── PetHotelApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   └── SwaggerConfig.java
│   ├── controller/
│   │   ├── RoomController.java
│   │   ├── BookingController.java
│   │   ├── dto/
│   │   │   ├── RoomDTO.java
│   │   │   ├── BookingDTO.java
│   │   │   └── BookingRequestDTO.java
│   │   └── mapper/
│   │       ├── RoomDTOMapper.java
│   │       └── BookingDTOMapper.java
│   ├── repository/
│   │   ├── entity/
│   │   │   ├── Room.java
│   │   │   └── Booking.java
│   │   ├── RoomRepository.java
│   │   └── BookingRepository.java
│   ├── service/
│   │   ├── interfaces/
│   │   │   ├── RoomService.java
│   │   │   └── BookingService.java
│   │   ├── impl/
│   │   │   ├── RoomServiceImpl.java
│   │   │   └── BookingServiceImpl.java
│   │   ├── CircuitBreakerService.java
│   │   ├── validation/
│   │   │   ├── OnCreate.java
│   │   │   └── OnUpdate.java
│   │   └── exception/
│   │       ├── RoomNotFoundException.java
│   │       ├── BookingNotFoundException.java
│   │       ├── RoomNotAvailableException.java
│   │       ├── InvalidBookingDatesException.java
│   │       └── ExceptionHandling.java
│   └── security/
│       └── (uses tnc-security-lib)
└── src/main/resources/
    └── application.yml
```

## Key Features

### 1. Room Management
- **GET** `/rooms` - List all rooms (with availability status)
- **GET** `/rooms/{id}` - Get room details
- **GET** `/rooms/available` - List available rooms
- **POST** `/rooms` - Create room (ADMIN only)
- **PUT** `/rooms` - Update room (ADMIN only)
- **DELETE** `/rooms/{id}` - Delete room (ADMIN only)

### 2. Booking Management
- **GET** `/bookings` - List all bookings (filtered by user role)
- **GET** `/bookings/{id}` - Get booking details
- **GET** `/bookings/user/{userId}` - Get user's bookings
- **GET** `/bookings/room/{roomId}` - Get bookings for a room
- **POST** `/bookings` - Create new booking (requires USER role)
- **PUT** `/bookings` - Update booking
- **DELETE** `/bookings/{id}` - Cancel booking
- **GET** `/bookings/availability` - Check room availability for date range

### 3. Business Logic
- **Date Validation:** Check-out date must be after check-in date
- **Room Availability:** Validate room is available for the requested dates
- **Overlap Prevention:** Prevent double-booking of the same room for overlapping dates
- **User Validation:** Verify user exists (via JWT token, no direct service call needed)
- **Total Days Calculation:** Automatically calculate booking duration

## Integration Points

### 1. User Management Service
- **Authentication:** Users must register/login via `micro_as_user`
- **JWT Validation:** API Gateway validates JWT tokens
- **User ID:** Extract `user_id` from JWT token claims for bookings
- **No Direct Service Call:** User validation handled via JWT token

### 2. API Gateway Configuration
Add new route in [api-gateway-as/src/main/resources/application.yml](api-gateway-as/src/main/resources/application.yml):
```yaml
- id: pet-hotel-service
  uri: lb://pet-hotel-microservice
  predicates:
    - Path=/pet-hotel-microservice/**
  filters:
    - RewritePath=/pet-hotel-microservice/(?<segment>.*), /$\{segment}
```

### 3. Eureka Service Discovery
- Register as `pet-hotel-microservice`
- Port: `8096`
- Health check endpoint: `/actuator/health`

### 4. Database Setup
Update [setup-databases.sql](setup-databases.sql):
- Add `CREATE DATABASE IF NOT EXISTS pet_hotel;`
- Grant privileges to `animalshelter` user
- Initialize 21 rooms (room numbers 1-21)

## Configuration Files

### 1. application.yml
- Port: `8096`
- Database: `pet_hotel`
- Eureka registration
- RabbitMQ configuration (for future use)
- Circuit breaker configuration
- Swagger configuration
- JWT internal token configuration

### 2. pom.xml
- Spring Boot 3.5.5 parent
- Dependencies matching existing services:
  - `tnc-security-lib` (1.0.0)
  - `tnc-resilience-lib` (1.0.0)
  - `tnc-swagger-lib` (1.0.0)
  - Spring Data JPA
  - Spring Web
  - Spring Security
  - MySQL Connector
  - MapStruct
  - Lombok
  - Validation API

### 3. Dockerfile
- Follow same pattern as existing services
- Multi-stage build
- Copy JAR and run

## Docker Compose Integration

Add to [docker-compose.yml](docker-compose.yml):
```yaml
pet-hotel-microservice:
  build: ./micro_as_pet_hotel
  container_name: pet-hotel-microservice
  ports:
    - "8096:8096"
  networks:
    - animal_shelter
  depends_on:
    - naming-server
    - mysql
    - rabbitmq
  environment:
    - SPRING_PROFILES_ACTIVE=docker
    - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/pet_hotel
    - SPRING_DATASOURCE_USERNAME=root
    - SPRING_DATASOURCE_PASSWORD=rootpassword
    - SPRING_RABBITMQ_HOST=rabbitmq
    - SPRING_RABBITMQ_PORT=5672
    - SPRING_RABBITMQ_USERNAME=admin
    - SPRING_RABBITMQ_PASSWORD=admin123
```

## Security & Authorization

### Role-Based Access Control
- **Public:** None (all endpoints require authentication)
- **USER Role:** Create/view own bookings
- **ADMIN Role:** Full CRUD on rooms and all bookings
- **INTERNAL_SERVICE:** Internal service-to-service communication

### JWT Integration
- Use `tnc-security-lib` for servlet-based security
- Extract `user_id` from JWT token claims
- Validate tokens via API Gateway

## Validation Rules

### Booking Validation
- `check_in_date` must be in the future (or today)
- `check_out_date` must be after `check_in_date`
- Room must exist and be available
- No overlapping bookings for the same room
- `pet_name` is required
- `user_id` extracted from JWT (not provided in request)

### Room Validation
- `room_number` must be unique (1-21)
- `room_number` must be positive integer
- `capacity` must be positive

## Error Handling

### Custom Exceptions
- `RoomNotFoundException` - Room doesn't exist
- `BookingNotFoundException` - Booking doesn't exist
- `RoomNotAvailableException` - Room unavailable for dates
- `InvalidBookingDatesException` - Invalid date range
- Global exception handler with `@ControllerAdvice`

## Testing Considerations

### Repository Layer
- Room CRUD operations
- Booking CRUD operations
- Date range queries for availability
- Overlap detection queries

### Service Layer
- Business logic validation
- Date calculations
- Availability checks
- Circuit breaker integration

## Implementation Steps

1. **Create microservice structure** - Directory, pom.xml, application.yml
2. **Database setup** - Update setup-databases.sql, create entities
3. **Repository layer** - RoomRepository, BookingRepository with custom queries
4. **Service layer** - Business logic, validation, circuit breakers
5. **Controller layer** - REST endpoints, DTOs, mappers
6. **Security configuration** - Integrate tnc-security-lib
7. **API Gateway integration** - Add routing configuration
8. **Docker integration** - Dockerfile and docker-compose.yml
9. **Documentation** - Swagger annotations, README updates

## Files to Create/Modify

### New Files
- `micro_as_pet_hotel/` (entire directory structure)
- All Java classes for the microservice
- `micro_as_pet_hotel/Dockerfile`
- `micro_as_pet_hotel/src/main/resources/application.yml`

### Files to Modify
- [setup-databases.sql](setup-databases.sql) - Add pet_hotel database
- [docker-compose.yml](docker-compose.yml) - Add pet-hotel service
- [api-gateway-as/src/main/resources/application.yml](api-gateway-as/src/main/resources/application.yml) - Add routing
- [README.md](README.md) - Document new service

## Architecture Diagram Integration

The Pet Hotel service will integrate into the existing architecture:

```
API Gateway (8765)
    ↓
Eureka Server (8761)
    ↓
Pet Hotel Microservice (8096)
    ↓
MySQL (pet_hotel database)
```

## Notes

- Follow SOLID principles (especially SRP for services)
- Use DTOs (Records) for API contracts
- MapStruct for entity-DTO mapping
- Circuit breakers for database operations
- Comprehensive validation using Bean Validation
- Swagger documentation for all endpoints
- Follow existing code patterns and naming conventions
