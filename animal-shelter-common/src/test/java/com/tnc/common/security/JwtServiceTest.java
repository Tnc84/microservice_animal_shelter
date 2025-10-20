package com.tnc.common.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();

        // Inject test properties via reflection since @Value is not processed in unit test
        var secretField = JwtService.class.getDeclaredField("jwtSecret");
        secretField.setAccessible(true);
        secretField.set(jwtService, "test-secret-key-should-be-long-enough-1234567890");

        var expField = JwtService.class.getDeclaredField("jwtExpirationInMs");
        expField.setAccessible(true);
        expField.set(jwtService, 60000L);

        var refreshField = JwtService.class.getDeclaredField("refreshTokenExpirationInMs");
        refreshField.setAccessible(true);
        refreshField.set(jwtService, 60000L);
    }

    private String buildToken(String userId, String username, String authorities, Date exp) throws Exception {
        var secretField = JwtService.class.getDeclaredField("jwtSecret");
        secretField.setAccessible(true);
        String secret = (String) secretField.get(jwtService);
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        Date now = new Date();
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("authorities", authorities)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key)
                .compact();
    }

    @Test
    void should_extract_fields_and_validate_valid_token() throws Exception {
        Date exp = new Date(System.currentTimeMillis() + 30000);
        String token = buildToken("42", "alice", "ROLE_USER,ROLE_ADMIN", exp);

        assertThat(jwtService.validateJwtToken(token)).isTrue();
        assertThat(jwtService.getUsernameFromJwtToken(token)).isEqualTo("alice");
        assertThat(jwtService.getUserIdFromJwtToken(token)).isEqualTo("42");
        assertThat(jwtService.getAuthoritiesFromJwtToken(token)).isEqualTo("ROLE_USER,ROLE_ADMIN");
    }

    @Test
    void should_invalidate_expired_token() throws Exception {
        Date exp = new Date(System.currentTimeMillis() - 1000);
        String token = buildToken("7", "bob", "ROLE_USER", exp);

        assertThat(jwtService.validateJwtToken(token)).isFalse();
    }
}


