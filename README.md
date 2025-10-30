
# Animal Shelter Microservices Application

## Project Overview

This is a **Java 17 microservices application** for managing an animal shelter system. The project follows a **clean microservices architecture** with standalone services that work together to provide a complete animal shelter management solution.

### **🏗️ Architecture Evolution:**
- **✅ Clean Microservices**: Each service is completely independent with its own dependencies
- **✅ Standalone Services**: No parent POM - each service manages its own versions
- **✅ Shared Security Library**: `security-common` module for unified JWT authentication
- **✅ Independent Deployment**: Services can be built, tested, and deployed separately

## Architecture & Technology Stack

### **Core Technologies:**
- **Java 17** with **Spring Boot 3.5.5**
- **Spring Cloud** ecosystem for microservices
- **MySQL** databases for data persistence
- **Maven** for dependency management
- **JWT Security** for authentication and authorization

### **Microservices Technologies:**
- **Netflix Eureka** for service discovery
- **Spring Cloud Gateway** for API Gateway
- **Spring Cloud Sleuth & Zipkin** for distributed tracing
- **RabbitMQ** for messaging and event-driven architecture
- **Resilience4j** for circuit breaker patterns and fault tolerance
- **Event-Driven Architecture** with RabbitMQ message publishing and consuming

### **API Gateway Architecture:**
- **Reactive Architecture:** Uses Spring WebFlux (reactive/non-blocking) instead of Spring MVC
- **Dependency Exclusion:** Must exclude `spring-boot-starter-web` to prevent conflicts
- **Netty Server:** Runs on Netty instead of Tomcat for high-concurrency gateway scenarios
- **WebFlux Integration:** Uses `spring-boot-starter-webflux` for reactive programming model
- **Why Exclusion is Required:** Spring Cloud Gateway and Spring MVC cannot coexist due to:
  - Incompatible web servers (Netty vs Tomcat)
  - Different HTTP handling (WebFlux filter chain vs Servlet filter chain)
  - Classpath conflicts in web infrastructure autoconfiguration
  - Programming model conflicts (reactive vs imperative)

### **Security Architecture by Service Type:**
- **API Gateway (WebFlux):** Custom JWT service and WebFlux security configuration
- **Business Microservices (Servlet):** Shared security library with servlet-based filters
- **Dependency Management:** Proper exclusions prevent servlet/WebFlux conflicts
- **Performance Benefits:** WebFlux gateway handles thousands of concurrent connections
- **Maintainability:** Architecture-specific security implementations for optimal performance

### **Development Tools:**
- **Lombok** for reducing boilerplate code
- **MapStruct** for object mapping
- **Spring Boot Validation** for data validation
- **Docker** for containerization
- **Records** for immutable data structures
- **Spring Security** for JWT authentication
- **Shared Libraries** for common functionality across microservices

## Microservices Architecture

### 1. **Naming Server (Eureka Server)**
- **Port:** 8761
- **Purpose:** Service discovery and registration
- **Technology:** Netflix Eureka Server
- **URL:** `localhost:8761`

### 2. **API Gateway**
- **Port:** 8765
- **Purpose:** Single entry point for all microservices with JWT security
- **Technology:** Spring Cloud Gateway with WebFlux Security
- **Architecture:** Reactive (WebFlux) - non-blocking, high-performance gateway
- **Features:** 
  - Service discovery integration and routing
  - JWT token validation and role-based access control
  - CORS configuration for frontend integration
  - Centralized security management
  - Reactive security filters for non-blocking authentication
- **Security Implementation:**
  - Custom `JwtService` for WebFlux compatibility
  - `WebFluxSecurityConfig` for reactive security configuration
  - `JwtAuthenticationFilter` for non-blocking token validation
- **Security Endpoints:**
  - `POST /user-management/auth/login` - User authentication
  - `POST /user-management/auth/register` - User registration
- **Example URLs:**
  - `http://localhost:8765/shelter-microservice/shelters/getAllAnimals`
  - `http://localhost:8765/animal-microservice/animals/getAll`

### 3. **Animal Microservice**
- **Ports:** 8093/8095
- **Database:** MySQL (`animal` database)
- **Purpose:** Manages animal records with event-driven architecture
- **Key Features:**
  - CRUD operations for animals with circuit breaker protection
  - Animal validation (name, breed, species)
  - Photo storage capability
  - Environment port tracking
  - **Event Publishing:** RabbitMQ event publishing for animal lifecycle events
  - **Circuit Breaker Protection:** Database and RabbitMQ operations with fallback mechanisms
- **REST Endpoints:**
  - `GET /animals/getAll` - Retrieve all animals
  - `GET /animals/getById/{id}` - Get animal by ID
  - `POST /animals` - Create new animal
  - `PUT /animals` - Update animal
- **Event Publishing:**
  - Animal Created, Updated, Adopted, Deleted events
  - Automatic event publishing after database operations
  - Circuit breaker protection for message publishing

### 4. **Shelter Microservice**
- **Ports:** 8092/8094
- **Database:** MySQL (`shelter` database)
- **Purpose:** Manages shelter information with event-driven integration
- **Key Features:**
  - Shelter CRUD operations with circuit breaker protection
  - Integration with Animal microservice via Feign
  - **Event Consumption:** RabbitMQ event processing for animal lifecycle updates
  - **Circuit Breaker Protection:** Database and RabbitMQ operations with fallback mechanisms
  - **Statistics Tracking:** Automatic shelter statistics updates based on animal events
  - **Capacity Management:** Shelter capacity monitoring and alerts
- **REST Endpoints:**
  - `GET /shelters/getAll` - Get all shelters
  - `GET /shelters/getAllAnimals` - Get all animals via Feign
  - `POST /shelters/add` - Add new shelter
  - `PUT /shelters/update` - Update shelter
- **Event Processing:**
  - Animal Created, Updated, Adopted, Deleted event consumption
  - Automatic shelter statistics updates
  - Circuit breaker protection for event processing

### 5. **User Management Microservice**
- **Port:** 8091
- **Database:** MySQL (`user_management` database)
- **Purpose:** Handles user registration, authentication, and notification management
- **Key Features:**
  - User CRUD operations with JWT security and circuit breaker protection
  - Role-based access control (USER, ADMIN, SHELTER_MANAGER, VET)
  - JWT token generation and validation
  - Password encryption with BCrypt
  - Email functionality for password reset
  - User validation and exception handling
  - Spring Security integration
  - **Notification System:** In-app notification management with circuit breaker protection
  - **Circuit Breaker Protection:** Database operations with fallback mechanisms
- **Authentication Endpoints:**
  - `POST /auth/login` - User login with JWT token generation
  - `POST /auth/register` - User registration with JWT token
- **User Management Endpoints:**
  - `GET /users` - Get all users (requires USER role)
  - `GET /users/find/{username}` - Find user by email
  - `POST /users/add` - Add new user
  - `PUT /users/update` - Update user
  - `DELETE /users/delete/{id}` - Delete user
  - `GET /users/resetPassword/{email}` - Reset password
- **Notification Endpoints:**
  - `GET /notifications` - Get user notifications
  - `POST /notifications` - Create notification
  - `PUT /notifications/{id}/read` - Mark notification as read
  - `GET /circuit-breaker/status` - Circuit breaker monitoring

## Security Implementation

### **🔐 Hybrid Security Architecture:**
- **API Gateway Security:** WebFlux-compatible JWT service for reactive gateway
- **Microservices Security:** Shared security library for servlet-based services
- **Architecture-Specific Implementation:** Different security approaches for different architectures
- **WebFlux Gateway:** Custom `JwtService` and `WebFluxSecurityConfig` for reactive applications
- **Servlet Microservices:** Shared `tnc-security-lib` for traditional Spring MVC services
- **Dependency Management:** Proper exclusions to prevent servlet/WebFlux conflicts

### **📚 Shared Libraries Architecture:**
- **tnc-security-lib:** JWT authentication and authorization for servlet-based services
- **tnc-resilience-lib:** Circuit breaker, retry, and fault tolerance patterns
- **tnc-swagger-lib:** OpenAPI/Swagger documentation configuration and utilities
- **tnc-docker-lib:** Docker templates, scripts, and containerization utilities
- **Modular Design:** Each library is independent and can be used separately
- **Version Management:** Each library maintains its own version and dependencies

### **Enhanced JWT Authentication & Authorization:**
- **Dual Token System:** Access tokens (15min) + Refresh tokens (7 days)
- **HttpOnly Cookie Security:** Refresh tokens stored in secure HttpOnly cookies
- **Token Validation:** API Gateway validates JWT tokens for all requests
- **Role-Based Access Control:** Different access levels for different microservices
- **Password Security:** BCrypt encryption for password storage
- **Stateless Authentication:** JWT-based stateless security
- **Token Management:** Automatic refresh and proper token revocation
- **Internal Token System:** Secure inter-service communication with internal tokens

### **Enhanced Security Flow:**
1. **User Authentication:** `POST /user-management/auth/login`
2. **Dual Token Generation:** Access token (15min) + Refresh token cookie (7 days)
3. **Token Validation:** API Gateway validates access tokens for all requests
4. **Automatic Refresh:** When access token expires, refresh token generates new access token
5. **Role-Based Authorization:** Access control based on user roles
6. **Secure Logout:** Both tokens invalidated and cookies cleared

### **New Security Endpoints:**
- `POST /auth/login` - Returns access token + HttpOnly refresh token cookie
- `POST /auth/register` - Returns access token + HttpOnly refresh token cookie
- `POST /auth/refresh` - Generates new access token from refresh token
- `POST /auth/logout` - Revokes tokens and clears cookies

### **Access Control Matrix:**
- **Public Endpoints:** `/auth/**`, `/swagger-ui/**`, `/actuator/health`
- **User Management:** Requires `USER` role
- **Shelter Management:** Requires `ADMIN` or `SHELTER_MANAGER` role
- **Animal Management:** Requires `ADMIN`, `SHELTER_MANAGER`, or `VET` role

### **Security Features:**
- **Token Blacklisting:** Proper token revocation on logout
- **Device Tracking:** IP address and device info logging
- **Automatic Cleanup:** Expired token removal
- **XSS Protection:** HttpOnly cookies prevent client-side access
- **CSRF Protection:** SameSite cookie configuration

## Circuit Breaker Architecture

### **Resilience4j Implementation:**
- **Database Circuit Breakers:** 50% failure threshold, 30s open state, automatic recovery
- **RabbitMQ Circuit Breakers:** 60% failure threshold, 60s open state, message processing protection
- **API Gateway Circuit Breakers:** Service-to-service communication protection
- **Retry Policies:** Exponential backoff with configurable attempts
- **Time Limiters:** 5s for database, 10s for RabbitMQ operations
- **Bulkhead Isolation:** Resource protection with concurrent call limits

### **Fault Tolerance Features:**
- **Automatic Fallback:** Graceful degradation when services fail
- **Health Monitoring:** Real-time circuit breaker status tracking
- **Event Processing:** RabbitMQ message processing with circuit breaker protection
- **Database Operations:** All CRUD operations protected with fallback mechanisms
- **Service Communication:** Inter-service calls with circuit breaker protection

### Clear Circuit Breaker Example (what happens step-by-step)

- Normal operation (CLOSED)
  - GET /animals reads from DB; success rate is healthy → all requests pass.
- Degradation begins
  - DB starts failing/timeouts. Within last 10 calls, 6 fail → failure-rate > 50%.
- Trip to OPEN
  - Circuit opens. New GET /animals requests fail fast (no DB call) with a quick fallback (e.g., cached/empty list or 503 with message).
- Half-open probing
  - After 30s, circuit goes HALF-OPEN and allows a few test calls (e.g., 3).
  - If they succeed → circuit closes; if they fail → circuit re-opens.
- Isolation and protection
  - Bulkhead caps concurrent DB calls (prevents thread starvation).
  - Time limiter aborts hanging calls (e.g., after 5s) to free resources.
- Not only databases
  - Use it for dependencies: databases, other microservices, RabbitMQ, caches, email/SMS providers. Not for pure in-process CPU work.

## Key Features & Patterns

### **SOLID Principles Implementation:**
- **Single Responsibility:** Each microservice has a specific domain
- **Open/Closed:** Extensible through interfaces and dependency injection
- **Liskov Substitution:** Proper inheritance and interface implementation
- **Interface Segregation:** Focused interfaces for specific operations
- **Dependency Inversion:** Dependency injection throughout the application

### **Design Patterns:**
- **Repository Pattern** for data access
- **DTO Pattern** for data transfer using Records
- **Mapper Pattern** using MapStruct
- **Service Layer Pattern** for business logic
- **Circuit Breaker Pattern** with Resilience4j for fault tolerance
- **Security Filter Pattern** for JWT authentication
- **Builder Pattern** for JWT token creation
- **Event-Driven Pattern** with RabbitMQ for asynchronous communication
- **Publisher-Subscriber Pattern** for animal lifecycle events
- **Fallback Pattern** for graceful degradation

### **Data Validation:**
- Comprehensive validation using Bean Validation
- Custom validation groups (`OnCreate`, `OnUpdate`)
- Field-level validation for names, emails, and other attributes

### **Monitoring & Observability:**
- **Distributed Tracing** with Spring Cloud Sleuth
- **Zipkin** integration for trace visualization
- **Actuator** endpoints for health checks and circuit breaker monitoring
- **Logging** with SLF4J
- **Circuit Breaker Monitoring** with dedicated endpoints
- **Event Tracking** for RabbitMQ message processing
- **Health Dashboards** for all microservices

### **Containerization:**
- **Docker** support with custom images
- **Docker Compose** for orchestration
- Pre-built images available (e.g., `tnc1984/shms-shelter:0.0.1-SNAPSHOT`)

## Database Schema

Each microservice maintains its own database:
- **Animal Database:** Stores animal information (id, name, breed, species, photo)
- **Shelter Database:** Stores shelter information with environment tracking
- **User Management Database:** Stores user data with role-based access

## Inter-Service Communication

- **Feign Client** for synchronous communication between Shelter and Animal services
- **Service Discovery** through Eureka for dynamic service location
- **Circuit Breaker** pattern for fault tolerance with Resilience4j
- **Fallback mechanisms** for service resilience
- **Event-Driven Architecture** with RabbitMQ for asynchronous communication
- **Message Publishing** from Animal service for lifecycle events
- **Message Consumption** in Shelter service for statistics updates
- **Circuit Breaker Protection** for all inter-service communications

## Getting Started

### Prerequisites
- Java 17
- Maven 3.6+
- MySQL 8.0+
- Docker (optional)

### Local Development Setup

**⚠️ Important Build Order for Standalone Microservices:**

1. **Build Security Common First (Required Dependency):**
   ```bash
   cd security-common
   mvn clean install -DskipTests
   ```

2. **Start Eureka Server:**
   ```bash
   cd naming-server-as
   mvn spring-boot:run
   ```
   Access at: `http://localhost:8761`

3. **Start Microservices (in any order after security-common is built):**
   ```bash
   # Animal Microservice
   cd micro_as_animal
   mvn spring-boot:run
   
   # Shelter Microservice
   cd micro_as_shelter
   mvn spring-boot:run
   
   # User Management Microservice
   cd micro_as_user
   mvn spring-boot:run
   
   # API Gateway
   cd api-gateway-as
   mvn spring-boot:run
   ```

**🔧 Build Commands for Standalone Services:**
```bash
# Build individual services (security-common must be built first)
cd security-common && mvn clean install -DskipTests
cd micro_as_user && mvn clean install -DskipTests  
cd micro_as_animal && mvn clean install -DskipTests
cd micro_as_shelter && mvn clean install -DskipTests
cd api-gateway-as && mvn clean install -DskipTests
cd naming-server-as && mvn clean install -DskipTests
```

3. **Access the Application:**
   - API Gateway: `http://localhost:8765`
   - Eureka Dashboard: `http://localhost:8761`
   - Zipkin (if running): `http://localhost:9411`

### Docker Deployment

```bash
# Build and run with Docker Compose
docker-compose up -d
```

## API Examples

### Enhanced Authentication Flow:
```bash
# User Registration (returns access token + HttpOnly refresh token cookie)
curl -X POST http://localhost:8765/user-management/auth/register \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "firstName": "John",
    "lastName": "Doe", 
    "email": "john.doe@example.com",
    "password": "password123"
  }'

# User Login (returns access token + HttpOnly refresh token cookie)
curl -X POST http://localhost:8765/user-management/auth/login \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "email": "john.doe@example.com",
    "password": "password123"
  }'

# Token Refresh (uses HttpOnly cookie to get new access token)
curl -X POST http://localhost:8765/user-management/auth/refresh \
  -b cookies.txt

# User Logout (revokes tokens and clears cookies)
curl -X POST http://localhost:8765/user-management/auth/logout \
  -b cookies.txt
```

### Through API Gateway (with JWT token):
```bash
# Get all animals (requires JWT token)
curl -H "Authorization: Bearer <JWT_TOKEN>" \
  http://localhost:8765/animal-microservice/animals/getAll

# Get all shelters (requires JWT token)
curl -H "Authorization: Bearer <JWT_TOKEN>" \
  http://localhost:8765/shelter-microservice/shelters/getAll

# Get all animals from shelter service (requires JWT token)
curl -H "Authorization: Bearer <JWT_TOKEN>" \
  http://localhost:8765/shelter-microservice/shelters/getAllAnimals
```

### Circuit Breaker Monitoring:
```bash
# Check circuit breaker status for all services
curl http://localhost:8091/circuit-breaker/status
curl http://localhost:8092/circuit-breaker/status
curl http://localhost:8093/circuit-breaker/status

# Check circuit breaker health
curl http://localhost:8091/circuit-breaker/health
curl http://localhost:8092/circuit-breaker/health
curl http://localhost:8093/circuit-breaker/health

# Check retry metrics
curl http://localhost:8091/circuit-breaker/retry/status
curl http://localhost:8092/circuit-breaker/retry/status
curl http://localhost:8093/circuit-breaker/retry/status

# Check time limiter status
curl http://localhost:8091/circuit-breaker/time-limiter/status
curl http://localhost:8092/circuit-breaker/time-limiter/status
curl http://localhost:8093/circuit-breaker/time-limiter/status
```

### Direct Service Access (for development):
```bash
# Animal Service
curl http://localhost:8093/animals/getAll

# Shelter Service  
curl http://localhost:8092/shelters/getAll

# User Service (requires authentication)
curl -H "Authorization: Bearer <JWT_TOKEN>" http://localhost:8091/users
```

### User Notifications (User Management Microservice)

The user service provides in-app notifications for user events (e.g., animal created/updated/adopted). Notifications are created internally by services; the public API allows listing, filtering, counting, marking as read, and deleting.

Endpoints (served by `micro_as_user`):

- GET `/notifications/user/{userId}`: Paginated notifications; supports `page`, `size`, `sortBy` (default `createdAt`), `sortDir` (`asc|desc`, default `desc`).
- GET `/notifications/user/{userId}/unread`: All unread notifications.
- GET `/notifications/user/{userId}/count`: Count of unread notifications.
- GET `/notifications/user/{userId}/type/{type}`: Paginated notifications by type.
- PUT `/notifications/{notificationId}/read?userId={userId}`: Mark a single notification as read.
- PUT `/notifications/user/{userId}/mark-all-read`: Mark all notifications as read; returns updated count.
- DELETE `/notifications/{notificationId}?userId={userId}`: Delete a notification (must belong to user).

Common notification types: `ANIMAL_CREATED`, `ANIMAL_UPDATED`, `ANIMAL_ADOPTED`, `ANIMAL_DELETED`, `SHELTER_UPDATE`.

Example requests:

```bash
# List latest notifications for a user (through API Gateway)
curl -H "Authorization: Bearer <JWT_TOKEN>" \
  "http://localhost:8765/user-management/notifications/user/1?page=0&size=10&sortBy=createdAt&sortDir=desc"

# Get unread notifications
curl -H "Authorization: Bearer <JWT_TOKEN>" \
  "http://localhost:8765/user-management/notifications/user/1/unread"

# Get unread count
curl -H "Authorization: Bearer <JWT_TOKEN>" \
  "http://localhost:8765/user-management/notifications/user/1/count"

# Mark one as read
curl -X PUT -H "Authorization: Bearer <JWT_TOKEN>" \
  "http://localhost:8765/user-management/notifications/42/read?userId=1"

# Mark all as read
curl -X PUT -H "Authorization: Bearer <JWT_TOKEN>" \
  "http://localhost:8765/user-management/notifications/user/1/mark-all-read"

# Delete a notification
curl -X DELETE -H "Authorization: Bearer <JWT_TOKEN>" \
  "http://localhost:8765/user-management/notifications/42?userId=1"
```

## Project Structure

```
microservice_animal_shelter/
├── tnc-shared-libraries/     # 🔐 Shared Libraries (Standalone)
│   ├── tnc-security-lib/     # Security library for servlet-based services
│   │   ├── JwtService.java                    # JWT service for servlet apps
│   │   ├── InternalTokenService.java          # Internal token management
│   │   ├── SecurityAutoConfiguration.java     # Auto-configuration
│   │   └── InternalTokenWebFluxFilter.java    # WebFlux-compatible filter
│   ├── tnc-resilience-lib/   # Circuit breaker library
│   ├── tnc-swagger-lib/      # Swagger/OpenAPI documentation library
│   └── tnc-docker-lib/       # Docker utilities and templates
├── api-gateway-as/           # 🌐 API Gateway (WebFlux - Reactive)
│   ├── security/
│   │   ├── JwtService.java                    # WebFlux JWT service
│   │   ├── WebFluxSecurityConfig.java         # Reactive security config
│   │   └── JwtAuthenticationFilter.java       # Reactive auth filter
│   └── config/
├── micro_as_animal/          # 🐕 Animal service (Servlet - Spring MVC)
├── micro_as_shelter/         # 🏠 Shelter service (Servlet - Spring MVC)
├── micro_as_user/            # 👤 User service (Servlet - Spring MVC)
├── naming-server-as/         # 🔍 Eureka service discovery
├── docker-compose.yml        # Docker orchestration
└── README.md                 # This file
```

Each microservice follows a clean architecture with:
- **Controller Layer:** REST endpoints and request handling
- **Service Layer:** Business logic and validation
- **Repository Layer:** Data access and persistence
- **DTO Layer:** Data transfer objects using Records with validation
- **Domain Layer:** Business entities and models
- **Mapper Layer:** Object transformation using MapStruct
- **Security Layer:** Architecture-specific security implementation
  - **API Gateway:** WebFlux-compatible JWT service and reactive filters
  - **Business Services:** Shared security library with servlet-based filters
- **Dependency Management:** Proper exclusions to prevent servlet/WebFlux conflicts
- **Internal Security:** Inter-service communication with internal tokens

## Frontend Integration

### **📱 Frontend Security Guide:**
A comprehensive guide for frontend teams is available in `FRONTEND_SECURITY_GUIDE.md` which includes:

- **Angular Service Implementation:** Complete authentication service with dual token handling
- **HTTP Interceptor Setup:** Automatic token management and refresh
- **Route Guards:** Role-based access control for Angular routes
- **API Integration Examples:** Ready-to-use code for microservices integration
- **Security Best Practices:** XSS protection, CSRF prevention, and secure token storage

### **🔧 Frontend Features:**
- **Automatic Token Refresh:** Seamless user experience without re-login
- **HttpOnly Cookie Support:** Secure refresh token handling
- **Role-Based Routing:** Different access levels for different user types
- **Error Handling:** Comprehensive error management for authentication
- **Security Headers:** Proper CORS and security configuration

## Current Status

### **✅ Implemented Features:**
- **Enhanced JWT Security:** Dual token system with access (15min) and refresh (7 days) tokens
- **HttpOnly Cookie Security:** Refresh tokens stored in secure HttpOnly cookies
- **Role-Based Access Control:** USER, ADMIN, SHELTER_MANAGER, VET roles
- **API Gateway Security:** Centralized JWT token validation with automatic refresh
- **User Management:** Registration, login, token refresh, and secure logout
- **Token Management:** Database-backed refresh tokens with device tracking
- **Modern Java Features:** Records, Lombok, and Spring Boot 3.5.5
- **Microservices Architecture:** All services with enhanced security integration
- **Frontend Integration Guide:** Complete Angular implementation guide
- **Circuit Breaker Implementation:** Resilience4j circuit breakers for all services
- **Event-Driven Architecture:** RabbitMQ messaging for animal lifecycle events
- **Fault Tolerance:** Fallback mechanisms and graceful degradation
- **Notification System:** In-app notification management with circuit breaker protection
- **Monitoring & Observability:** Circuit breaker status and health monitoring endpoints

### **🔧 Technical Improvements:**
- **Spring Boot 3.5.5:** Updated from 2.6.2/2.7.0
- **Clean Microservices Architecture:** Each service is completely independent
- **Standalone Services:** No parent POM - each service manages its own dependencies
- **Hybrid Security Architecture:** WebFlux for API Gateway, servlet for business services
- **WebFlux API Gateway:** Reactive, non-blocking gateway for high performance
- **Dependency Management:** Proper exclusions prevent servlet/WebFlux conflicts
- **Shared Libraries:** Modular libraries for security, resilience, documentation, and Docker
- **Enhanced JWT Security:** Dual token system with automatic refresh
- **Internal Token System:** Secure inter-service communication
- **Password Encryption:** BCrypt for secure password storage
- **CORS Configuration:** Frontend integration support
- **Clean Architecture:** SOLID principles with architecture-specific security layers
- **Database Security:** Refresh token storage with metadata tracking
- **Device Tracking:** IP address and device info logging for security
- **Circuit Breaker Integration:** Resilience4j for fault tolerance across all services
- **Event-Driven Architecture:** RabbitMQ for asynchronous communication
- **Fault Tolerance:** Comprehensive fallback mechanisms and graceful degradation
- **Monitoring Enhancement:** Circuit breaker status and health monitoring
- **Production Readiness:** Enterprise-grade resilience patterns
- **Independent Deployment:** Services can be built, tested, and deployed separately
- **Version Management:** Each service can evolve its dependencies independently

## Architecture Decision: WebFlux vs Servlet

### **Why WebFlux for API Gateway?**
- **Performance:** Handles 10,000+ concurrent connections vs 200-500 for servlet
- **Memory Efficiency:** Lower memory footprint with event-driven architecture
- **Non-blocking I/O:** Perfect for high-throughput gateway scenarios
- **Spring Cloud Gateway:** Built specifically for WebFlux architecture
- **Industry Standard:** Production microservices use reactive gateways

### **Why Servlet for Business Services?**
- **Familiarity:** Most developers are comfortable with Spring MVC
- **Rich Ecosystem:** Extensive library support for servlet-based applications
- **Complex Business Logic:** Easier to implement with blocking operations
- **Database Integration:** Better support for JPA/Hibernate with servlet
- **Testing:** More mature testing frameworks for servlet applications

### **Best Practices Applied:**
- **Dependency Exclusions:** Prevent servlet/WebFlux conflicts
- **Architecture-Specific Security:** Optimized for each service type
- **Clean Separation:** Clear boundaries between reactive and imperative code
- **Performance Optimization:** Right tool for the right job

## Documentation

### **📚 Available Documentation:**
- **README.md:** Main project documentation with architecture overview
- **FRONTEND_SECURITY_GUIDE.md:** Complete frontend integration guide for Angular teams
- **API Documentation:** Swagger UI available at `http://localhost:8765/swagger-ui.html`

### **🔗 Quick Links:**
- **API Gateway:** `http://localhost:8765`
- **Eureka Dashboard:** `http://localhost:8761`
- **Swagger UI:** `http://localhost:8765/swagger-ui.html`
- **Health Check:** `http://localhost:8765/actuator/health`

## 🏗️ System Architecture

                    ┌─────────────▼─────────────┐
                    │    API Gateway (8765)     │
                    │   JWT Security + Routing  |
                    └─────────────┬─────────────┘
                                  │
                    ┌─────────────▼─────────────┐
                    │   Eureka Server (8761)    │
                    │   Service Discovery       │
                    └─────────────┬─────────────┘
                                 │
        ┌────────────────────────┼────────────────────────┐
        │                        │                        │
┌───────▼───────┐    ┌───────────▼──────────┐    ┌───────▼───────┐
│ User Service  │    │   Animal Service     │    │Shelter Service│
│    (8091)     │    │      (8093)          │    │     (8092)    │
│ Auth + Notif  │    │   Animal CRUD +      │    │ Shelter Mgmt +│
│               │    │      Events          │    │    Stats      │
└───────┬───────┘    └───────────┬──────────┘    └───────┬───────┘
        │                        │                        │
        │                        │                        │
┌───────▼───────┐    ┌───────────▼──────────┐    ┌───────▼───────┐
│   MySQL DB    │    │     MySQL DB         │    │   MySQL DB    │
│user_management│    │      animal          │    │    shelter    │
└───────────────┘    └──────────────────────┘    └───────────────┘
                                 │
                    ┌─────────────▼─────────────┐
                    │      RabbitMQ             │
                    │  Event-Driven Comm.       │
                    └───────────────────────────┘


## API Testing with Swagger UI

### **📋 Complete Swagger Testing Guide:**

All microservices have comprehensive Swagger/OpenAPI documentation with interactive testing capabilities.

### **🚀 Quick Start Testing:**

1. **Start All Services:**
   ```bash
   # Use the provided batch script
   start-all.bat
   ```

2. **Access Swagger UI for Each Service:**
   - **API Gateway**: `http://localhost:8765/swagger-ui.html`
   - **Animal Service**: `http://localhost:8081/swagger-ui.html`
   - **Shelter Service**: `http://localhost:8082/swagger-ui.html`
   - **User Service**: `http://localhost:8083/swagger-ui.html`
   - **Naming Server**: `http://localhost:8761/swagger-ui.html`

### **🔐 Authentication Testing Flow:**

1. **Register/Login via User Service Swagger:**
   - Navigate to `http://localhost:8083/swagger-ui.html`
   - Find `/auth/login` or `/auth/register` endpoints
   - Click "Try it out" and enter credentials:
     ```json
     {
       "email": "test@example.com",
       "password": "password123"
     }
     ```
   - **Copy the JWT token** from the response

2. **Authorize in Other Services:**
   - Open any other service's Swagger UI
   - Click the **"Authorize"** button (🔒 icon)
   - Enter: `Bearer <your-jwt-token>`
   - Click "Authorize"

3. **Test Protected Endpoints:**
   - All endpoints marked with 🔒 require authentication
   - Use "Try it out" to test CRUD operations
   - Verify responses match the documented schemas

### **📊 Individual Service Testing:**

#### **Animal Service Testing:**
- **URL**: `http://localhost:8081/swagger-ui.html`
- **Key Endpoints**:
  - `GET /animals/getAll` - List all animals
  - `POST /animals` - Create new animal
  - `PUT /animals` - Update animal
  - `DELETE /animals/{id}` - Delete animal
- **Authentication**: Required for all operations

#### **Shelter Service Testing:**
- **URL**: `http://localhost:8082/swagger-ui.html`
- **Key Endpoints**:
  - `GET /shelters/getAll` - List all shelters
  - `POST /shelters/add` - Create new shelter
  - `PUT /shelters/update` - Update shelter
  - `GET /shelters/getAllAnimals` - Get animals via Feign
- **Authentication**: Required for all operations

#### **User Service Testing:**
- **URL**: `http://localhost:8083/swagger-ui.html`
- **Key Endpoints**:
  - `POST /auth/login` - User authentication
  - `POST /auth/register` - User registration
  - `GET /users` - List users (requires USER role)
  - `GET /notifications` - User notifications
- **Authentication**: Login endpoints are public, others require authentication

#### **API Gateway Testing:**
- **URL**: `http://localhost:8765/swagger-ui.html`
- **Features**:
  - Aggregated view of all microservice endpoints
  - Centralized authentication
  - Service routing examples

### **🧪 Testing Scenarios:**

#### **Complete CRUD Flow:**
1. **Login** via User Service Swagger
2. **Create Animal** via Animal Service Swagger
3. **Create Shelter** via Shelter Service Swagger
4. **View Notifications** via User Service Swagger
5. **Update/Delete** entities as needed

#### **Authentication Flow:**
1. **Register** new user via User Service
2. **Login** and copy JWT token
3. **Authorize** in other services
4. **Test protected endpoints**
5. **Verify role-based access** (try different user roles)

#### **Error Testing:**
1. **Test without authentication** (should get 401)
2. **Test with invalid token** (should get 403)
3. **Test with expired token** (should get 401)
4. **Test invalid data** (should get 400)

### **📋 Swagger Features Available:**

- **Interactive API Testing**: Click "Try it out" on any endpoint
- **Request/Response Examples**: Pre-filled examples for all endpoints
- **Schema Validation**: Automatic validation of request/response formats
- **Authentication Testing**: Built-in JWT token testing
- **Error Response Documentation**: Complete error code documentation
- **Model Definitions**: Detailed request/response schemas
- **Endpoint Grouping**: Organized by service functionality

### **🔧 Advanced Testing:**

#### **Circuit Breaker Testing:**
- Test endpoints under load to trigger circuit breakers
- Monitor fallback responses
- Check circuit breaker status endpoints

#### **Event-Driven Testing:**
- Create animals and verify event publishing
- Check shelter statistics updates
- Monitor notification creation

#### **Security Testing:**
- Test different user roles and permissions
- Verify JWT token expiration handling
- Test refresh token functionality

## Contributing

This project follows SOLID principles and modern microservices best practices. When contributing:

1. Ensure all services follow the established patterns
2. Maintain proper validation and error handling
3. Update documentation for any new endpoints
4. Follow the existing code structure and naming conventions
5. **Security Guidelines:** Always use JWT tokens for authentication
6. **Testing:** Use `-DskipTests` flag during development to avoid test failures
7. **Frontend Integration:** Refer to `FRONTEND_SECURITY_GUIDE.md` for Angular implementation

## License

This project is developed by Tnc for educational and demonstration purposes.

