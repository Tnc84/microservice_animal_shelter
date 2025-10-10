
# Animal Shelter Microservices Application

## Project Overview

This is a **Java 17 microservices application** for managing an animal shelter system. The project follows a **microservice architecture** with five distinct services that work together to provide a complete animal shelter management solution.

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
- **OpenFeign** for inter-service communication
- **Spring Cloud Sleuth & Zipkin** for distributed tracing
- **RabbitMQ** for messaging
- **Resilience4j** for circuit breaker patterns

### **Development Tools:**
- **Lombok** for reducing boilerplate code
- **MapStruct** for object mapping
- **Spring Boot Validation** for data validation
- **Docker** for containerization
- **Records** for immutable data structures
- **Spring Security** for JWT authentication

## Microservices Architecture

### 1. **Naming Server (Eureka Server)**
- **Port:** 8761
- **Purpose:** Service discovery and registration
- **Technology:** Netflix Eureka Server
- **URL:** `localhost:8761`

### 2. **API Gateway**
- **Port:** 8765
- **Purpose:** Single entry point for all microservices with JWT security
- **Technology:** Spring Cloud Gateway with Spring Security
- **Features:** 
  - Service discovery integration and routing
  - JWT token validation and role-based access control
  - CORS configuration for frontend integration
  - Centralized security management
- **Security Endpoints:**
  - `POST /user-management/auth/login` - User authentication
  - `POST /user-management/auth/register` - User registration
- **Example URLs:**
  - `http://localhost:8765/shelter-microservice/shelters/getAllAnimals`
  - `http://localhost:8765/animal-microservice/animals/getAll`

### 3. **Animal Microservice**
- **Ports:** 8093/8095
- **Database:** MySQL (`animal` database)
- **Purpose:** Manages animal records
- **Key Features:**
  - CRUD operations for animals
  - Animal validation (name, breed, species)
  - Photo storage capability
  - Environment port tracking
- **REST Endpoints:**
  - `GET /animals/getAll` - Retrieve all animals
  - `GET /animals/getById/{id}` - Get animal by ID
  - `POST /animals` - Create new animal
  - `PUT /animals` - Update animal

### 4. **Shelter Microservice**
- **Ports:** 8092/8094
- **Database:** MySQL (`shelter` database)
- **Purpose:** Manages shelter information and integrates with animal service
- **Key Features:**
  - Shelter CRUD operations
  - Integration with Animal microservice via Feign
  - Resilience4j circuit breaker implementation
  - Fallback mechanisms for service failures
- **REST Endpoints:**
  - `GET /shelters/getAll` - Get all shelters
  - `GET /shelters/getAllAnimals` - Get all animals via Feign
  - `POST /shelters/add` - Add new shelter
  - `PUT /shelters/update` - Update shelter

### 5. **User Management Microservice**
- **Port:** 8091
- **Database:** MySQL (`user_management` database)
- **Purpose:** Handles user registration, authentication, and JWT token generation
- **Key Features:**
  - User CRUD operations with JWT security
  - Role-based access control (USER, ADMIN, SHELTER_MANAGER, VET)
  - JWT token generation and validation
  - Password encryption with BCrypt
  - Email functionality for password reset
  - User validation and exception handling
  - Spring Security integration
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

## Security Implementation

### **Enhanced JWT Authentication & Authorization:**
- **Dual Token System:** Access tokens (15min) + Refresh tokens (7 days)
- **HttpOnly Cookie Security:** Refresh tokens stored in secure HttpOnly cookies
- **Token Validation:** API Gateway validates JWT tokens for all requests
- **Role-Based Access Control:** Different access levels for different microservices
- **Password Security:** BCrypt encryption for password storage
- **Stateless Authentication:** JWT-based stateless security
- **Token Management:** Automatic refresh and proper token revocation

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
- **Circuit Breaker Pattern** with Resilience4j
- **Security Filter Pattern** for JWT authentication
- **Builder Pattern** for JWT token creation

### **Data Validation:**
- Comprehensive validation using Bean Validation
- Custom validation groups (`OnCreate`, `OnUpdate`)
- Field-level validation for names, emails, and other attributes

### **Monitoring & Observability:**
- **Distributed Tracing** with Spring Cloud Sleuth
- **Zipkin** integration for trace visualization
- **Actuator** endpoints for health checks
- **Logging** with SLF4J

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
- **Circuit Breaker** pattern for fault tolerance
- **Fallback mechanisms** for service resilience

## Getting Started

### Prerequisites
- Java 17
- Maven 3.6+
- MySQL 8.0+
- Docker (optional)

### Local Development Setup

1. **Start Eureka Server:**
   ```bash
   cd naming-server-as
   mvn spring-boot:run
   ```
   Access at: `http://localhost:8761`

2. **Start Microservices:**
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

### Direct Service Access (for development):
```bash
# Animal Service
curl http://localhost:8093/animals/getAll

# Shelter Service  
curl http://localhost:8092/shelters/getAll

# User Service (requires authentication)
curl -H "Authorization: Bearer <JWT_TOKEN>" http://localhost:8091/users
```

## Project Structure

```
microservice_animal_shelter/
├── api-gateway-as/           # API Gateway service
├── micro_as_animal/          # Animal management service
├── micro_as_shelter/         # Shelter management service
├── micro_as_user/            # User management service
├── naming-server-as/         # Eureka service discovery
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
- **Security Layer:** JWT authentication and authorization (User Management)
- **Filter Layer:** JWT token validation (API Gateway)

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

### **🔧 Technical Improvements:**
- **Spring Boot 3.5.5:** Updated from 2.6.2/2.7.0
- **Enhanced JWT Security:** Dual token system with automatic refresh
- **Password Encryption:** BCrypt for secure password storage
- **CORS Configuration:** Frontend integration support
- **Clean Architecture:** SOLID principles with security layers
- **Database Security:** Refresh token storage with metadata tracking
- **Device Tracking:** IP address and device info logging for security

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

