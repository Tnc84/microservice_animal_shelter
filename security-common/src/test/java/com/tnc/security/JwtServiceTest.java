package com.tnc.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for JwtService
 */
@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    
    private JwtService jwtService;
    
    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "testSecretKeyForTestingPurposesOnly");
        ReflectionTestUtils.setField(jwtService, "jwtExpirationInMs", 900000L);
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpirationInMs", 604800000L);
    }
    
    @Test
    void shouldGenerateValidToken() {
        // Given
        String username = "testuser";
        String userId = "123";
        String authorities = "USER,ADMIN";
        
        // When
        String token = jwtService.generateToken(username, userId, authorities);
        
        // Then
        assertThat(token).isNotNull();
        assertThat(jwtService.validateJwtToken(token)).isTrue();
        assertThat(jwtService.getUsernameFromJwtToken(token)).isEqualTo(username);
        assertThat(jwtService.getUserIdFromJwtToken(token)).isEqualTo(userId);
        assertThat(jwtService.getAuthoritiesFromJwtToken(token)).isEqualTo(authorities);
    }
    
    @Test
    void shouldGenerateValidRefreshToken() {
        // Given
        String username = "testuser";
        String userId = "123";
        
        // When
        String refreshToken = jwtService.generateRefreshToken(username, userId);
        
        // Then
        assertThat(refreshToken).isNotNull();
        assertThat(jwtService.validateJwtToken(refreshToken)).isTrue();
        assertThat(jwtService.getUsernameFromJwtToken(refreshToken)).isEqualTo(username);
        assertThat(jwtService.getUserIdFromJwtToken(refreshToken)).isEqualTo(userId);
    }
    
    @Test
    void shouldValidateTokenCorrectly() {
        // Given
        String token = jwtService.generateToken("testuser", "123", "USER");
        
        // When & Then
        assertThat(jwtService.validateJwtToken(token)).isTrue();
        assertThat(jwtService.validateJwtToken("invalid-token")).isFalse();
    }
    
    @Test
    void shouldDetectExpiredToken() {
        // Given
        ReflectionTestUtils.setField(jwtService, "jwtExpirationInMs", -1000L); // Expired token
        String token = jwtService.generateToken("testuser", "123", "USER");
        
        // When & Then
        assertThat(jwtService.isTokenExpired(token)).isTrue();
    }
}
