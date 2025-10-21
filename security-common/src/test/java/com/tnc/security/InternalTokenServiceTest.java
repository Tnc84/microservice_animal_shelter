package com.tnc.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for InternalTokenService
 */
@ExtendWith(MockitoExtension.class)
class InternalTokenServiceTest {
    
    private InternalTokenService internalTokenService;
    
    @BeforeEach
    void setUp() {
        internalTokenService = new InternalTokenService();
        ReflectionTestUtils.setField(internalTokenService, "internalJwtSecret", "testInternalSecretKeyForTestingPurposesOnly");
        ReflectionTestUtils.setField(internalTokenService, "internalJwtExpirationInMs", 600000L);
    }
    
    @Test
    void shouldGenerateValidInternalToken() {
        // Given
        String userId = "123";
        String username = "testuser";
        String authorities = "USER,ADMIN";
        
        // When
        String internalToken = internalTokenService.generateInternalToken(userId, username, authorities);
        
        // Then
        assertThat(internalToken).isNotNull();
        assertThat(internalTokenService.validateInternalToken(internalToken)).isTrue();
        assertThat(internalTokenService.getUsernameFromInternalToken(internalToken)).isEqualTo(username);
        assertThat(internalTokenService.getUserIdFromInternalToken(internalToken)).isEqualTo(userId);
        assertThat(internalTokenService.getAuthoritiesFromInternalToken(internalToken)).isEqualTo(authorities);
    }
    
    @Test
    void shouldValidateInternalTokenCorrectly() {
        // Given
        String internalToken = internalTokenService.generateInternalToken("123", "testuser", "USER");
        
        // When & Then
        assertThat(internalTokenService.validateInternalToken(internalToken)).isTrue();
        assertThat(internalTokenService.validateInternalToken("invalid-token")).isFalse();
    }
    
    @Test
    void shouldDetectExpiredInternalToken() {
        // Given
        ReflectionTestUtils.setField(internalTokenService, "internalJwtExpirationInMs", -1000L); // Expired token
        String internalToken = internalTokenService.generateInternalToken("123", "testuser", "USER");
        
        // When & Then
        assertThat(internalTokenService.isInternalTokenExpired(internalToken)).isTrue();
    }
}
