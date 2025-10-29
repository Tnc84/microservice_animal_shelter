package com.tnc.resilience.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiCircuitBreakerServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ApiCircuitBreakerService apiCircuitBreakerService;

    @BeforeEach
    void setUp() {
        // Mock the autowired dependencies
        // Note: In a real test, you would use @MockBean with Spring Boot Test
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
    void testExecuteWithResilience() {
        // Given
        String expectedResult = "test result";
        
        // When
        String result = apiCircuitBreakerService.executeWithResilience("test", () -> expectedResult);

        // Then
        assertEquals(expectedResult, result);
    }
}
