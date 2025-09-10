
# Animal Shelter Microservices Application

## Project Overview

This is a **Java 17 microservices application** for managing an animal shelter system. The project follows a **microservice architecture** with five distinct services that work together to provide a complete animal shelter management solution.

## Architecture & Technology Stack

### **Core Technologies:**
- **Java 17** with **Spring Boot 2.6.2/2.7.0**
- **Spring Cloud** ecosystem for microservices
- **MySQL** databases for data persistence
- **Maven** for dependency management

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

## Microservices Architecture

### 1. **Naming Server (Eureka Server)**
- **Port:** 8761
- **Purpose:** Service discovery and registration
- **Technology:** Netflix Eureka Server
- **URL:** `localhost:8761`

### 2. **API Gateway**
- **Port:** 8765
- **Purpose:** Single entry point for all microservices
- **Technology:** Spring Cloud Gateway
- **Features:** Service discovery integration, routing
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
- **Purpose:** Handles user registration, authentication, and management
- **Key Features:**
  - User CRUD operations
  - Role-based access control
  - Email functionality for password reset
  - User validation and exception handling
- **REST Endpoints:**
  - `GET /users` - Get all users
  - `GET /users/find/{username}` - Find user by email
  - `POST /users/add` - Add new user
  - `PUT /users/update` - Update user
  - `DELETE /users/delete/{id}` - Delete user
  - `GET /users/resetPassword/{email}` - Reset password

## Key Features & Patterns

### **SOLID Principles Implementation:**
- **Single Responsibility:** Each microservice has a specific domain
- **Open/Closed:** Extensible through interfaces and dependency injection
- **Liskov Substitution:** Proper inheritance and interface implementation
- **Interface Segregation:** Focused interfaces for specific operations
- **Dependency Inversion:** Dependency injection throughout the application

### **Design Patterns:**
- **Repository Pattern** for data access
- **DTO Pattern** for data transfer
- **Mapper Pattern** using MapStruct
- **Service Layer Pattern** for business logic
- **Circuit Breaker Pattern** with Resilience4j

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

### Through API Gateway:
```bash
# Get all animals
curl http://localhost:8765/animal-microservice/animals/getAll

# Get all shelters
curl http://localhost:8765/shelter-microservice/shelters/getAll

# Get all animals from shelter service
curl http://localhost:8765/shelter-microservice/shelters/getAllAnimals
```

### Direct Service Access:
```bash
# Animal Service
curl http://localhost:8093/animals/getAll

# Shelter Service
curl http://localhost:8092/shelters/getAll

# User Service
curl http://localhost:8091/users
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
- **DTO Layer:** Data transfer objects with validation
- **Domain Layer:** Business entities and models
- **Mapper Layer:** Object transformation using MapStruct

## Contributing

This project follows SOLID principles and modern microservices best practices. When contributing:

1. Ensure all services follow the established patterns
2. Maintain proper validation and error handling
3. Update documentation for any new endpoints
4. Follow the existing code structure and naming conventions

## License

This project is developed by Tnc for educational and demonstration purposes.

