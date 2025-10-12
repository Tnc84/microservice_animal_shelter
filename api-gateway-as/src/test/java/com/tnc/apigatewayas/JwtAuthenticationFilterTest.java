package com.tnc.apigatewayas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JwtAuthenticationFilter
 * Tests JWT token extraction and request modification
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private ServerWebExchange exchange;
    private ServerHttpRequest request;
    private GatewayFilterChain filterChain;

    @BeforeEach
    void setUp() {
        exchange = mock(ServerWebExchange.class);
        request = mock(ServerHttpRequest.class);
        filterChain = mock(GatewayFilterChain.class);
        
        when(exchange.getRequest()).thenReturn(request);
    }

    @Test
    void filter_WithValidBearerToken_ShouldAddTokenToHeaders() {
        // Arrange
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test";
        String authHeader = "Bearer " + token;
        
        when(request.getHeaders()).thenReturn(mock(org.springframework.http.HttpHeaders.class));
        when(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION)).thenReturn(authHeader);
        when(request.mutate()).thenReturn(mock(ServerHttpRequest.Builder.class));
        when(request.mutate().header(anyString(), anyString())).thenReturn(mock(ServerHttpRequest.Builder.class));
        when(request.mutate().header(anyString(), anyString()).build()).thenReturn(request);
        when(exchange.mutate()).thenReturn(mock(ServerWebExchange.Builder.class));
        when(exchange.mutate().request(any(ServerHttpRequest.class))).thenReturn(mock(ServerWebExchange.Builder.class));
        when(exchange.mutate().request(any(ServerHttpRequest.class)).build()).thenReturn(exchange);
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = jwtAuthenticationFilter.filter(exchange, filterChain);

        // Assert
        assertNotNull(result);
        result.block(); // Block to verify completion
        
        verify(request).mutate();
        verify(filterChain).filter(any(ServerWebExchange.class));
    }

    @Test
    void filter_WithInvalidTokenFormat_ShouldPassThrough() {
        // Arrange
        String invalidAuthHeader = "InvalidToken";
        
        when(request.getHeaders()).thenReturn(mock(org.springframework.http.HttpHeaders.class));
        when(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION)).thenReturn(invalidAuthHeader);
        when(filterChain.filter(exchange)).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = jwtAuthenticationFilter.filter(exchange, filterChain);

        // Assert
        assertNotNull(result);
        result.block(); // Block to verify completion
        
        verify(filterChain).filter(exchange);
        verify(request, never()).mutate();
    }

    @Test
    void filter_WithNoAuthorizationHeader_ShouldPassThrough() {
        // Arrange
        when(request.getHeaders()).thenReturn(mock(org.springframework.http.HttpHeaders.class));
        when(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        when(filterChain.filter(exchange)).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = jwtAuthenticationFilter.filter(exchange, filterChain);

        // Assert
        assertNotNull(result);
        result.block(); // Block to verify completion
        
        verify(filterChain).filter(exchange);
        verify(request, never()).mutate();
    }

    @Test
    void filter_WithEmptyBearerToken_ShouldPassThrough() {
        // Arrange
        String emptyBearer = "Bearer ";
        
        when(request.getHeaders()).thenReturn(mock(org.springframework.http.HttpHeaders.class));
        when(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION)).thenReturn(emptyBearer);
        when(filterChain.filter(exchange)).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = jwtAuthenticationFilter.filter(exchange, filterChain);

        // Assert
        assertNotNull(result);
        result.block(); // Block to verify completion
        
        verify(filterChain).filter(exchange);
        verify(request, never()).mutate();
    }

    @Test
    void filter_WithValidToken_ShouldExtractTokenCorrectly() {
        // Arrange
        String token = "valid.jwt.token";
        String authHeader = "Bearer " + token;
        
        when(request.getHeaders()).thenReturn(mock(org.springframework.http.HttpHeaders.class));
        when(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION)).thenReturn(authHeader);
        when(request.mutate()).thenReturn(mock(ServerHttpRequest.Builder.class));
        when(request.mutate().header("X-User-Token", token)).thenReturn(mock(ServerHttpRequest.Builder.class));
        when(request.mutate().header("X-User-Token", token).build()).thenReturn(request);
        when(exchange.mutate()).thenReturn(mock(ServerWebExchange.Builder.class));
        when(exchange.mutate().request(any(ServerHttpRequest.class))).thenReturn(mock(ServerWebExchange.Builder.class));
        when(exchange.mutate().request(any(ServerHttpRequest.class)).build()).thenReturn(exchange);
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = jwtAuthenticationFilter.filter(exchange, filterChain);

        // Assert
        assertNotNull(result);
        result.block(); // Block to verify completion
        
        verify(request.mutate()).header("X-User-Token", token);
        verify(filterChain).filter(any(ServerWebExchange.class));
    }

    @Test
    void filter_WithLongToken_ShouldHandleCorrectly() {
        // Arrange
        String longToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        String authHeader = "Bearer " + longToken;
        
        when(request.getHeaders()).thenReturn(mock(org.springframework.http.HttpHeaders.class));
        when(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION)).thenReturn(authHeader);
        when(request.mutate()).thenReturn(mock(ServerHttpRequest.Builder.class));
        when(request.mutate().header(anyString(), anyString())).thenReturn(mock(ServerHttpRequest.Builder.class));
        when(request.mutate().header(anyString(), anyString()).build()).thenReturn(request);
        when(exchange.mutate()).thenReturn(mock(ServerWebExchange.Builder.class));
        when(exchange.mutate().request(any(ServerHttpRequest.class))).thenReturn(mock(ServerWebExchange.Builder.class));
        when(exchange.mutate().request(any(ServerHttpRequest.class)).build()).thenReturn(exchange);
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = jwtAuthenticationFilter.filter(exchange, filterChain);

        // Assert
        assertNotNull(result);
        result.block(); // Block to verify completion
        
        verify(request.mutate()).header("X-User-Token", longToken);
        verify(filterChain).filter(any(ServerWebExchange.class));
    }
}
