# Actuator Endpoints Troubleshooting Guide

## ❌ Common Mistakes

### 1. **Zipkin Does NOT Have Actuator Endpoints**

**WRONG:**
```
http://localhost:9411/actuator/metrics  ❌
```

**CORRECT:**
```
http://localhost:9411  ✅ (Zipkin UI - no actuator endpoints)
```

**Why:** Zipkin is a separate Java application (not Spring Boot), so it doesn't have Spring Boot Actuator endpoints.

### 2. **Actuator Endpoints Are on Spring Boot Services**

Actuator endpoints are available on your **Spring Boot microservices**, not on Zipkin.

**Correct URLs:**
- Animal Service: `http://localhost:8093/actuator/metrics`
- Shelter Service: `http://localhost:8092/actuator/metrics`
- User Service: `http://localhost:8091/actuator/metrics`
- API Gateway: `http://localhost:8765/actuator/metrics`

## 🔍 Troubleshooting Steps

### Step 1: Verify Service is Running

```bash
# Check if service is running
curl http://localhost:8093/actuator/health

# Should return:
# {"status":"UP"}
```

**If you get "Connection refused":**
- Service is not running
- Start the service: `mvn spring-boot:run` or `docker-compose up`

### Step 2: Check Actuator Configuration

Verify `application.yml` has:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,httptrace
```

### Step 3: Check if Actuator Dependency is Present

Verify `pom.xml` has:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### Step 4: Check for Security Filters

If you have Spring Security, actuator endpoints might be blocked. Check security configuration.

### Step 5: Verify Port Number

Make sure you're using the correct port:
- Animal Service: **8093** (not 8091, 8092, etc.)
- Shelter Service: **8092**
- User Service: **8091**
- API Gateway: **8765**

## ✅ Quick Test Commands

```bash
# Test health endpoint (should work if service is running)
curl http://localhost:8093/actuator/health

# Test metrics endpoint
curl http://localhost:8093/actuator/metrics

# List all available metrics
curl http://localhost:8093/actuator/metrics | jq

# Get specific metric
curl http://localhost:8093/actuator/metrics/http.server.requests

# Prometheus format
curl http://localhost:8093/actuator/prometheus
```

## 🐛 Common Issues & Solutions

### Issue 1: "404 Not Found" on `/actuator/metrics`

**Possible Causes:**
1. Service not running
2. Wrong port number
3. Actuator dependency missing
4. Metrics endpoint not exposed in configuration

**Solution:**
- Check service is running: `curl http://localhost:8093/actuator/health`
- Verify `management.endpoints.web.exposure.include` includes `metrics`
- Check `pom.xml` has `spring-boot-starter-actuator`

### Issue 2: "Connection Refused"

**Cause:** Service is not running

**Solution:**
```bash
# Start the service
cd micro_as_animal
mvn spring-boot:run
```

### Issue 3: "401 Unauthorized" or "403 Forbidden"

**Cause:** Spring Security is blocking actuator endpoints

**Solution:**
- Check security configuration
- Ensure actuator endpoints are excluded from security
- Or configure security to allow actuator access

### Issue 4: Empty Metrics Response

**Cause:** No metrics collected yet (service just started)

**Solution:**
- Make some API calls first
- Wait a few seconds for metrics to be collected
- Try accessing `/actuator/metrics/http.server.requests` directly

## 📊 What Each Endpoint Does

| Endpoint | Description | Example URL |
|----------|-------------|-------------|
| `/actuator/health` | Service health status | `http://localhost:8093/actuator/health` |
| `/actuator/metrics` | List all metrics | `http://localhost:8093/actuator/metrics` |
| `/actuator/metrics/{name}` | Specific metric value | `http://localhost:8093/actuator/metrics/http.server.requests` |
| `/actuator/prometheus` | Prometheus format | `http://localhost:8093/actuator/prometheus` |
| `/actuator/httptrace` | HTTP request traces | `http://localhost:8093/actuator/httptrace` |
| `/actuator/info` | Application info | `http://localhost:8093/actuator/info` |

## 🎯 Zipkin vs Actuator - Understanding the Difference

### Zipkin (Port 9411)
- **Purpose:** Distributed tracing visualization
- **Type:** Separate Java application
- **Endpoints:** Only Zipkin UI (`/`, `/api/v2/spans`, etc.)
- **No Actuator:** Zipkin is NOT a Spring Boot app

### Actuator Endpoints
- **Purpose:** Spring Boot application monitoring
- **Type:** Built into Spring Boot services
- **Endpoints:** `/actuator/*`
- **Available on:** Your microservices (8091, 8092, 8093, 8765)

## 🔧 Verification Checklist

- [ ] Service is running (check with `/actuator/health`)
- [ ] Correct port number used
- [ ] `spring-boot-starter-actuator` dependency in `pom.xml`
- [ ] `management.endpoints.web.exposure.include` includes `metrics`
- [ ] Service has handled some requests (metrics need data)
- [ ] No security blocking actuator endpoints
- [ ] Not trying to access actuator on Zipkin (port 9411)

## 💡 Quick Verification Script

```bash
# Test all services
echo "Testing Animal Service..."
curl http://localhost:8093/actuator/health
echo -e "\n\nTesting Shelter Service..."
curl http://localhost:8092/actuator/health
echo -e "\n\nTesting User Service..."
curl http://localhost:8091/actuator/health
echo -e "\n\nTesting API Gateway..."
curl http://localhost:8765/actuator/health
```

If all return `{"status":"UP"}`, then services are running and actuator is working.

