package com.tnc.common.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class InternalTokenServiceTest {

    private InternalTokenService internalTokenService;

    @BeforeEach
    void setUp() throws Exception {
        internalTokenService = new InternalTokenService();

        var secretField = InternalTokenService.class.getDeclaredField("internalJwtSecret");
        secretField.setAccessible(true);
        secretField.set(internalTokenService, "internal-test-secret-key-long-enough-1234567890");

        var expField = InternalTokenService.class.getDeclaredField("internalJwtExpirationInMs");
        expField.setAccessible(true);
        expField.set(internalTokenService, 60000L);
    }

    @Test
    void should_generate_and_validate_token_and_extract_claims() {
        String token = internalTokenService.generateInternalToken("101", "service-user", "ROLE_INTERNAL,ROLE_SYSTEM");

        assertThat(internalTokenService.validateInternalToken(token)).isTrue();
        assertThat(internalTokenService.getUsernameFromInternalToken(token)).isEqualTo("service-user");
        assertThat(internalTokenService.getUserIdFromInternalToken(token)).isEqualTo("101");
        assertThat(internalTokenService.getAuthoritiesFromInternalToken(token)).isEqualTo("ROLE_INTERNAL,ROLE_SYSTEM");

        Date expiration = internalTokenService.getExpirationFromInternalToken(token);
        assertThat(expiration).isAfter(new Date(System.currentTimeMillis() + 1000));
    }

    @Test
    void should_invalidate_tampered_token() {
        String token = internalTokenService.generateInternalToken("101", "service-user", "ROLE_INTERNAL");
        String tampered = token + "x";
        assertThat(internalTokenService.validateInternalToken(tampered)).isFalse();
    }
}


