# TNC Shared Libraries - Usage Examples

This document provides practical examples of how to use the TNC shared libraries in your microservices.

## 🔐 **Security Library Usage**

### **1. Add Dependency**
```xml
<dependency>
    <groupId>com.tnc.security</groupId>
    <artifactId>tnc-security-lib</artifactId>
    <version>1.0.0</version>
</dependency>
```

### **2. Use JWT Utility**
```java
@Service
public class AuthService {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public String generateToken(String username) {
        return jwtUtil.generateToken(username);
    }
    
    public boolean validateToken(String token, String username) {
        return jwtUtil.validateToken(token, username);
    }
    
    public String extractUsername(String token) {
        return jwtUtil.extractUsername(token);
    }
}
```

### **3. Configuration**
```yaml
# application.yml
jwt:
  secret: your-secret-key-here
  expiration: 86400000  # 24 hours
```

## 🛡️ **Resilience Library Usage**

### **1. Add Dependency**
```xml
<dependency>
    <groupId>com.tnc.resilience</groupId>
    <artifactId>tnc-resilience-lib</artifactId>
    <version>1.0.0</version>
</dependency>
```

### **2. Use Base Circuit Breaker Service**
```java
@Service
public class AnimalService extends BaseCircuitBreakerService<Animal, Long> {
    
    @Autowired
    private AnimalRepository animalRepository;
    
    @Override
    protected CrudRepository<Animal, Long> getRepository() {
        return animalRepository;
    }
    
    // All CRUD operations now have circuit breaker, retry, and timeout protection
}
```

### **3. Use API Circuit Breaker Service**
```java
@Service
public class ExternalApiService {
    
    @Autowired
    private ApiCircuitBreakerService apiCircuitBreakerService;
    
    public ResponseEntity<String> callExternalApi(String url) {
        return apiCircuitBreakerService.get(url, String.class);
    }
    
    public ResponseEntity<User> createUser(User user) {
        return apiCircuitBreakerService.post("/api/users", user, User.class);
    }
}
```

### **4. Configuration**
```yaml
# application.yml
resilience4j:
  circuitbreaker:
    instances:
      database:
        failure-rate-threshold: 50
        wait-duration-in-open-state: 30s
  retry:
    instances:
      database:
        max-attempts: 3
        wait-duration: 1s
```

## 📖 **Swagger Library Usage**

### **1. Add Dependency**
```xml
<dependency>
    <groupId>com.tnc.swagger</groupId>
    <artifactId>tnc-swagger-lib</artifactId>
    <version>1.0.0</version>
</dependency>
```

### **2. Auto-Configuration**
The library automatically configures Swagger/OpenAPI. No additional code needed!

### **3. Access Documentation**
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

### **4. Custom Configuration (Optional)**
```java
@Configuration
public class CustomSwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("My Microservice API")
                        .version("1.0.0")
                        .description("Custom API documentation"));
    }
}
```

## 🐳 **Docker Library Usage**

### **1. Add Dependency**
```xml
<dependency>
    <groupId>com.tnc.docker</groupId>
    <artifactId>tnc-docker-lib</artifactId>
    <version>1.0.0</version>
</dependency>
```

### **2. Use Docker Template**
```bash
# Copy template to your microservice
cp tnc-docker-lib/target/classes/templates/Dockerfile.template ./Dockerfile

# Replace placeholders
sed -i 's/{{APP_NAME}}/my-microservice/g' Dockerfile
sed -i 's/{{JAR_FILE}}/my-microservice-1.0.0.jar/g' Dockerfile
sed -i 's/{{PORT}}/8080/g' Dockerfile
```

### **3. Use Build Script**
```bash
# Make script executable
chmod +x tnc-docker-lib/target/classes/scripts/build-docker.sh

# Build Docker image
./build-docker.sh my-microservice my-microservice-1.0.0.jar 8080
```

## 🔧 **Complete Microservice Example**

### **pom.xml**
```xml
<dependencies>
    <!-- TNC Shared Libraries -->
    <dependency>
        <groupId>com.tnc.security</groupId>
        <artifactId>tnc-security-lib</artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>com.tnc.resilience</groupId>
        <artifactId>tnc-resilience-lib</artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>com.tnc.swagger</groupId>
        <artifactId>tnc-swagger-lib</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

### **Service Implementation**
```java
@Service
public class AnimalService extends BaseCircuitBreakerService<Animal, Long> {
    
    @Autowired
    private AnimalRepository animalRepository;
    
    @Override
    protected CrudRepository<Animal, Long> getRepository() {
        return animalRepository;
    }
    
    // All methods inherit circuit breaker, retry, and timeout protection
}
```

### **Controller**
```java
@RestController
@RequestMapping("/api/animals")
public class AnimalController {
    
    @Autowired
    private AnimalService animalService;
    
    @GetMapping("/{id}")
    public ResponseEntity<Animal> getAnimal(@PathVariable Long id) {
        Optional<Animal> animal = animalService.findById(id);
        return animal.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
}
```

### **application.yml**
```yaml
# JWT Configuration
jwt:
  secret: your-secret-key-here
  expiration: 86400000

# Resilience4j Configuration
resilience4j:
  circuitbreaker:
    instances:
      database:
        failure-rate-threshold: 50
        wait-duration-in-open-state: 30s
  retry:
    instances:
      database:
        max-attempts: 3
        wait-duration: 1s

# Swagger Configuration
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
```

## 🚀 **Migration from Duplicated Code**

### **Before (Duplicated)**
```java
@Service
public class AnimalService {
    
    @CircuitBreaker(name = "database", fallbackMethod = "fallbackFindById")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public Optional<Animal> findById(Long id) {
        return animalRepository.findById(id);
    }
    
    public Optional<Animal> fallbackFindById(Long id, Exception ex) {
        log.error("Fallback for findById: {}", ex.getMessage());
        return Optional.empty();
    }
}
```

### **After (Using Shared Library)**
```java
@Service
public class AnimalService extends BaseCircuitBreakerService<Animal, Long> {
    
    @Autowired
    private AnimalRepository animalRepository;
    
    @Override
    protected CrudRepository<Animal, Long> getRepository() {
        return animalRepository;
    }
    
    // findById, save, deleteById methods are automatically available
    // with circuit breaker, retry, and timeout protection
}
```

## 📋 **Benefits**

- ✅ **No Code Duplication** - Common functionality is centralized
- ✅ **Consistent Behavior** - All microservices use the same patterns
- ✅ **Easy Maintenance** - Update once, benefit everywhere
- ✅ **Better Testing** - Test libraries independently
- ✅ **Faster Development** - Focus on business logic, not infrastructure

## 🎯 **Next Steps**

1. **Add dependencies** to your microservices
2. **Extend base classes** where applicable
3. **Remove duplicated code** from microservices
4. **Test thoroughly** to ensure everything works
5. **Deploy and monitor** the new setup
