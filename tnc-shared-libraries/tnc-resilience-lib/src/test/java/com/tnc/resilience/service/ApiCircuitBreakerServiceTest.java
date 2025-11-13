package com.tnc.resilience.service;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiCircuitBreakerServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private CircuitBreakerRegistry circuitBreakerRegistry;
    private RetryRegistry retryRegistry;
    private TimeLimiterRegistry timeLimiterRegistry;

    @InjectMocks
    private ApiCircuitBreakerService apiCircuitBreakerService;

    @BeforeEach
    void setUp() {
        // Create real CircuitBreakerRegistry with test configuration
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .slidingWindowSize(10)
                .failureRateThreshold(50f)
                .waitDurationInOpenState(Duration.ofMillis(1000))
                .build();
        circuitBreakerRegistry = CircuitBreakerRegistry.of(circuitBreakerConfig);
        
        // Create real RetryRegistry with test configuration
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(100))
                .build();
        retryRegistry = RetryRegistry.of(retryConfig);
        
        // Create real TimeLimiterRegistry with test configuration
        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(5))
                .build();
        timeLimiterRegistry = TimeLimiterRegistry.of(timeLimiterConfig);
        
        // Inject the real registries using Spring Test utility
        ReflectionTestUtils.setField(apiCircuitBreakerService, "circuitBreakerRegistry", circuitBreakerRegistry);
        ReflectionTestUtils.setField(apiCircuitBreakerService, "retryRegistry", retryRegistry);
        ReflectionTestUtils.setField(apiCircuitBreakerService, "timeLimiterRegistry", timeLimiterRegistry);
    }

    @Test
    void testGet() {
        // Given
        String url = "http://test.com/api";
        String expectedResponse = "test response";
        ResponseEntity<String> mockResponse = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        
        when(restTemplate.getForEntity(url, String.class)).thenReturn(mockResponse);

        // When
        ResponseEntity<String> result = apiCircuitBreakerService.get(url, String.class);

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResponse, result.getBody());
    }

    @Test
    void testPost() {
        // Given
        String url = "http://test.com/api";
        String request = "test request";
        String expectedResponse = "test response";
        ResponseEntity<String> mockResponse = new ResponseEntity<>(expectedResponse, HttpStatus.CREATED);
        
        when(restTemplate.postForEntity(url, request, String.class)).thenReturn(mockResponse);

        // When
        ResponseEntity<String> result = apiCircuitBreakerService.post(url, request, String.class);

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(expectedResponse, result.getBody());
    }

    @Test
    void testExecuteApi() {
        // Given
        String expectedResult = "test result";
        
        // When
        String result = apiCircuitBreakerService.executeApi("test", () -> expectedResult);

        // Then
        assertEquals(expectedResult, result);
    }
}
