package com.tnc.security;

import com.tnc.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service for JWT token operations.
 * Provides high-level methods for JWT token management.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtUtil jwtUtil;

    /**
     * Generate a JWT token for a user.
     *
     * @param username the username
     * @param userId the user ID
     * @param authorities the user authorities/roles
     * @return the generated JWT token
     */
    public String generateToken(String username, String userId, String authorities) {
        Map<String, Object> claims = Map.of(
            "userId", userId,
            "authorities", authorities
        );
        return jwtUtil.generateToken(username, claims);
    }

    /**
     * Validate a JWT token.
     *
     * @param token the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateJwtToken(String token) {
        try {
            String username = jwtUtil.extractUsername(token);
            return jwtUtil.validateToken(token, username);
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extract username from JWT token.
     *
     * @param token the JWT token
     * @return the username
     */
    public String getUsernameFromJwtToken(String token) {
        return jwtUtil.extractUsername(token);
    }

    /**
     * Extract authorities from JWT token.
     *
     * @param token the JWT token
     * @return the authorities
     */
    public String getAuthoritiesFromJwtToken(String token) {
        return jwtUtil.extractClaim(token, claims -> (String) claims.get("authorities"));
    }

    /**
     * Extract user ID from JWT token.
     *
     * @param token the JWT token
     * @return the user ID
     */
    public String getUserIdFromJwtToken(String token) {
        return jwtUtil.extractClaim(token, claims -> (String) claims.get("userId"));
    }
}
