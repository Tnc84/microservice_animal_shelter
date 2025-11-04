# Micrometer and Zipkin Setup Guide

## 📊 What are Micrometer and Zipkin?

### **Micrometer** = Metrics Collection Framework
- Collects application metrics (CPU, memory, request counts, response times)
- Standardizes metrics across different monitoring systems
- Provides tracing capabilities for distributed systems
- Acts as a bridge between your app and monitoring tools

### **Zipkin** = Distributed Tracing System
- Visualizes request flows across microservices
- Shows timing for each service call
- Tracks request journey from start to finish
- Identifies bottlenecks and slow services

## 🚀 Quick Start

### Step 1: Add Zipkin to Docker Compose

Add Zipkin server to your `docker-compose.yml`:

```yaml
  # Zipkin Distributed Tracing
  zipkin:
    image: openzipkin/zipkin:latest
    container_name: zipkin
    ports:
      - "9411:9411"
    networks:
      - animal_shelter
    healthcheck:
      test: ["CMD", "wget", "--spider", "-q", "http://localhost:9411/health"]
      interval: 10s
      timeout: 5s
      retries: 5
```

### Step 2: Update Dependencies (if needed)

For Spring Boot 3.x, you need these dependencies in your `pom.xml`:

```xml
<!-- Micrometer Tracing (replaces Sleuth in Spring Boot 3) -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-brave</artifactId>
</dependency>

<!-- Zipkin Reporter -->
<dependency>
    <groupId>io.zipkin.reporter2</groupId>
    <artifactId>zipkin-reporter-brave</artifactId>
</dependency>

<!-- Micrometer Core (for custom metrics) -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-core</artifactId>
</dependency>

<!-- Actuator (already included, but ensure it's there) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### Step 3: Update application.yml

**Remove old Sleuth configuration** (deprecated in Spring Boot 3):
```yaml
# ❌ REMOVE THIS (deprecated):
spring:
  sleuth:
    sampler:
      probability: 1.0
```

**Add new Micrometer Tracing configuration**:

```yaml
# ✅ ADD THIS (Spring Boot 3 compatible):
spring:
  application:
    name: animal-microservice  # or your service name
  
  # Micrometer Tracing Configuration
  zipkin:
    base-url: http://localhost:9411
    enabled: true
  
  # Tracing Configuration
  sleuth:
    sampler:
      probability: 1.0  # 1.0 = 100% of requests traced (use 0.1 for production)

# Management endpoints for metrics and tracing
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,httptrace,circuitbreakers,retries,timelimiters
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true
  tracing:
    sampling:
      probability: 1.0  # 100% sampling rate
```

### Step 4: Start Zipkin

```bash
# Option 1: Using Docker Compose
docker-compose up -d zipkin

# Option 2: Standalone Docker
docker run -d -p 9411:9411 openzipkin/zipkin
```

### Step 5: Access Zipkin UI

Open your browser:
```
http://localhost:9411
```

## 📈 Using Micrometer for Custom Metrics

### Example: Track Business Metrics

```java
package com.tnc.animal.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

@Service
public class AnimalService {
    
    private final Counter animalsCreatedCounter;
    private final Counter animalsAdoptedCounter;
    private final Timer adoptionTimer;
    
    public AnimalService(MeterRegistry meterRegistry) {
        // Create counters for business metrics
        this.animalsCreatedCounter = Counter.builder("animals.created")
            .description("Total number of animals created")
            .tag("service", "animal-microservice")
            .register(meterRegistry);
            
        this.animalsAdoptedCounter = Counter.builder("animals.adopted")
            .description("Total number of animals adopted")
            .tag("service", "animal-microservice")
            .register(meterRegistry);
            
        // Create timer for measuring adoption process duration
        this.adoptionTimer = Timer.builder("animals.adoption.process")
            .description("Time taken to process animal adoption")
            .register(meterRegistry);
    }
    
    public Animal createAnimal(Animal animal) {
        // Your business logic
        Animal saved = animalRepository.save(animal);
        
        // Increment counter
        animalsCreatedCounter.increment();
        
        return saved;
    }
    
    public void adoptAnimal(Long animalId) {
        // Measure time taken for adoption
        Timer.Sample sample = Timer.start();
        
        try {
            // Your adoption logic
            Animal animal = animalRepository.findById(animalId).orElseThrow();
            animal.setAdopted(true);
            animalRepository.save(animal);
            
            // Increment counter
            animalsAdoptedCounter.increment();
        } finally {
            // Stop timer and record duration
            sample.stop(adoptionTimer);
        }
    }
}
```

### Example: Track HTTP Request Metrics

```java
package com.tnc.animal.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/animals")
public class AnimalController {
    
    private final Counter getAnimalsCounter;
    private final Counter postAnimalCounter;
    
    public AnimalController(MeterRegistry meterRegistry) {
        this.getAnimalsCounter = Counter.builder("http.requests")
            .description("HTTP GET requests for animals")
            .tag("method", "GET")
            .tag("endpoint", "/animals")
            .register(meterRegistry);
            
        this.postAnimalCounter = Counter.builder("http.requests")
            .description("HTTP POST requests for animals")
            .tag("method", "POST")
            .tag("endpoint", "/animals")
            .register(meterRegistry);
    }
    
    @GetMapping
    public List<Animal> getAllAnimals() {
        getAnimalsCounter.increment();
        return animalService.getAllAnimals();
    }
    
    @PostMapping
    public Animal createAnimal(@RequestBody Animal animal) {
        postAnimalCounter.increment();
        return animalService.createAnimal(animal);
    }
}
```

### Example: Track Database Query Performance

```java
package com.tnc.animal.repository;

import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Repository;

@Repository
public class AnimalRepository {
    
    private final Timer dbQueryTimer;
    
    public AnimalRepository(MeterRegistry meterRegistry) {
        this.dbQueryTimer = Timer.builder("database.query.duration")
            .description("Database query execution time")
            .tag("operation", "findAll")
            .register(meterRegistry);
    }
    
    public List<Animal> findAllWithMetrics() {
        Timer.Sample sample = Timer.start();
        try {
            return findAll();
        } finally {
            sample.stop(dbQueryTimer);
        }
    }
}
```

## 🔍 Using Zipkin for Distributed Tracing

### Automatic Tracing

With the configuration above, Zipkin automatically traces:
- HTTP requests between services
- Database queries
- RabbitMQ message publishing/consuming
- Service-to-service calls via Feign/RestTemplate

### View Traces in Zipkin

1. **Start all services**
2. **Make some API calls** through the API Gateway
3. **Open Zipkin UI**: `http://localhost:9411`
4. **Click "Run Query"** to see all traces
5. **Click on a trace** to see the full request flow

### Example Trace Flow

```
User Request → API Gateway (8765)
              ↓
              Animal Service (8093)
              ↓
              Database Query
              ↓
              RabbitMQ Publish
              ↓
              Shelter Service (8092)
              ↓
              User Service (8091)
```

## 📊 Accessing Metrics

### Via Actuator Endpoints

```bash
# All metrics
curl http://localhost:8093/actuator/metrics

# Specific metric
curl http://localhost:8093/actuator/metrics/animals.created

# Prometheus format (for Grafana)
curl http://localhost:8093/actuator/prometheus

# HTTP traces
curl http://localhost:8093/actuator/httptrace
```

### Metrics Available by Default

- `http.server.requests` - HTTP request metrics
- `jvm.memory.used` - JVM memory usage
- `jvm.gc.pause` - Garbage collection metrics
- `process.cpu.usage` - CPU usage
- `system.cpu.usage` - System CPU usage
- Custom metrics you create

## 🎯 Real-World Examples

### Problem: "Animal adoption is slow"

**Micrometer shows:**
```
High response times for /animals/adopt endpoint
```

**Zipkin shows:**
```
Request spends 80% of time in database query
```

**Solution:**
Optimize database query or add caching

### Problem: "System is failing"

**Micrometer shows:**
```
High error rates
```

**Zipkin shows:**
```
Failures happen in RabbitMQ communication
```

**Solution:**
Check RabbitMQ health or adjust circuit breaker settings

## 🔧 Configuration for All Services

Update these files with the configuration above:
- `micro_as_animal/src/main/resources/application.yml`
- `micro_as_shelter/src/main/resources/application.yml`
- `micro_as_user/src/main/resources/application.yml`
- `api-gateway-as/src/main/resources/application.yml`

## 🐳 Docker Compose Update

Add Zipkin service to your `docker-compose.yml` (see Step 1 above).

## 📝 Summary

1. ✅ Add Zipkin to docker-compose.yml
2. ✅ Update dependencies in pom.xml
3. ✅ Replace Sleuth config with Micrometer Tracing config
4. ✅ Start Zipkin server
5. ✅ Access Zipkin UI at http://localhost:9411
6. ✅ Create custom metrics with MeterRegistry
7. ✅ View traces automatically in Zipkin

## 🚀 Next Steps

- Integrate with **Prometheus** for metrics storage
- Add **Grafana** dashboards for visualization
- Set up alerts based on metrics
- Configure sampling rate for production (0.1 = 10% of requests)

