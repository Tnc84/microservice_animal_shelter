package com.tnc.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for managing internal tokens used for inter-microservice communication.
 * Provides token generation and validation for internal API calls.
 */
@Slf4j
@Service
public class InternalTokenService {

    @Value("${internal.token.secret:internal-secret-key}")
    private String internalTokenSecret;

    @Value("${internal.token.expiration:3600000}") // 1 hour default
    private Long internalTokenExpiration;

    private final Map<String, Long> tokenCache = new HashMap<>();

    /**
     * Generate an internal token for microservice communication.
     *
     * @param serviceName the name of the service requesting the token
     * @return the generated internal token
     */
    public String generateInternalToken(String serviceName) {
        String token = serviceName + "-" + System.currentTimeMillis() + "-" + internalTokenSecret.hashCode();
        tokenCache.put(token, System.currentTimeMillis() + internalTokenExpiration);
        log.debug("Generated internal token for service: {}", serviceName);
        return token;
    }

    /**
     * Generate an internal token with user context for microservice communication.
     *
     * @param userId the user ID
     * @param username the username
     * @param authorities the user authorities
     * @return the generated internal token
     */
    public String generateInternalToken(String userId, String username, String authorities) {
        String token = userId + "-" + username + "-" + authorities + "-" + System.currentTimeMillis() + "-" + internalTokenSecret.hashCode();
        tokenCache.put(token, System.currentTimeMillis() + internalTokenExpiration);
        log.debug("Generated internal token for user: {} with authorities: {}", username, authorities);
        return token;
    }

    /**
     * Validate an internal token.
     *
     * @param token the token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateInternalToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        // Check if token exists in cache and is not expired
        Long expirationTime = tokenCache.get(token);
        if (expirationTime == null || System.currentTimeMillis() > expirationTime) {
            if (expirationTime != null) {
                tokenCache.remove(token); // Clean up expired token
            }
            return false;
        }

        log.debug("Internal token validated successfully");
        return true;
    }

    /**
     * Extract service name from internal token.
     *
     * @param token the internal token
     * @return the service name or null if invalid
     */
    public String extractServiceName(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }

        try {
            String[] parts = token.split("-");
            if (parts.length >= 3) {
                return parts[0];
            }
        } catch (Exception e) {
            log.warn("Failed to extract service name from token: {}", e.getMessage());
        }

        return null;
    }

    /**
     * Clean up expired tokens from cache.
     */
    public void cleanupExpiredTokens() {
        long currentTime = System.currentTimeMillis();
        tokenCache.entrySet().removeIf(entry -> currentTime > entry.getValue());
        log.debug("Cleaned up expired internal tokens");
    }
}
