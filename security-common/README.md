# Security Common Library

A reusable security library for microservices providing JWT token management, authentication, and authorization utilities.

## Features

- **JWT Token Management**: Generate, validate, and parse JWT tokens
- **Internal Token System**: Secure microservice-to-microservice communication
- **Security Filters**: Pre-built filters for token validation
- **Security Utilities**: Helper methods for authentication and authorization
- **Auto-configuration**: Automatic Spring Boot configuration

## Usage

### 1. Add Dependency

```xml
<dependency>
    <groupId>com.tnc</groupId>
    <artifactId>security-common</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. Configuration

Add to your `application.yml`:

```yaml
jwt:
  secret: your-secret-key
  expiration: 900000  # 15 minutes
  refresh-expiration: 604800000  # 7 days
  internal:
    secret: your-internal-secret-key
    expiration: 600000  # 10 minutes
```

### 3. Use in Microservices

#### API Gateway
```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/auth/**").permitAll()
                .anyExchange().authenticated()
            )
            .build();
    }
}
```

#### Microservices
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, 
                                        InternalTokenService internalTokenService) throws Exception {
        return http
            .addFilterBefore(new InternalTokenAuthorizationFilter(internalTokenService), 
                           UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated()
            )
            .build();
    }
}
```

## Components

### JwtService
- Generate JWT tokens
- Validate JWT tokens
- Extract user information from tokens
- Handle token expiration

### InternalTokenService
- Generate internal tokens for microservice communication
- Validate internal tokens
- Extract user information from internal tokens

### InternalTokenAuthorizationFilter
- Filter for validating internal tokens
- Automatically sets Spring Security context
- Handles authentication for microservices

### SecurityUtils
- Get current authenticated user
- Check user authorities
- Security context utilities

### SecurityConstants
- Common security constants
- HTTP headers
- Role definitions
- Public URL patterns

## Security Flow

```
1. Client → API Gateway (JWT validation)
2. API Gateway → Microservice (Internal token)
3. Microservice → InternalTokenAuthorizationFilter (Token validation)
4. Microservice → Business Logic (Authenticated user)
```

## Best Practices

1. **Use short-lived tokens** (15-30 minutes for access tokens)
2. **Implement refresh tokens** for long-term sessions
3. **Validate tokens in every microservice**
4. **Use HTTPS** for all communications
5. **Rotate secrets** regularly
6. **Implement token blacklisting** for logout

## Testing

```java
@SpringBootTest
class SecurityIntegrationTest {
    
    @Autowired
    private JwtService jwtService;
    
    @Test
    void shouldGenerateAndValidateToken() {
        String token = jwtService.generateToken("user", "123", "USER");
        assertThat(jwtService.validateJwtToken(token)).isTrue();
    }
}
```

## Reusability

This library can be used across multiple projects:

1. **Animal Shelter Project** (current)
2. **E-commerce Platform**
3. **Healthcare System**
4. **Banking Application**
5. **Any microservices architecture**

## Dependencies

- Spring Boot 3.5.5+
- Spring Security 6+
- Java 21+
- JJWT 0.12.3+

## License

Internal use only - TNC Corporation
