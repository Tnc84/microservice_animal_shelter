package com.tnc.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Internal Token Service for microservice-to-microservice communication
 * Generates and validates internal JWT tokens for secure service communication
 */
@Component
public class InternalTokenService {
    
    @Value("${jwt.internal.secret:internalSecretKey}")
    private String internalJwtSecret;
    
    @Value("${jwt.internal.expiration:600000}")
    private long internalJwtExpirationInMs;
    
    private SecretKey getInternalSigningKey() {
        return Keys.hmacShaKeyFor(internalJwtSecret.getBytes());
    }
    
    /**
     * Generate internal JWT token for microservice communication
     */
    public String generateInternalToken(String userId, String username, String authorities) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + internalJwtExpirationInMs);
        
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("authorities", authorities)
                .claim("tokenType", "internal")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getInternalSigningKey())
                .compact();
    }
    
    /**
     * Validate internal JWT token
     */
    public boolean validateInternalToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getInternalSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException e) {
            System.err.println("Invalid internal JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("Internal JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("Internal JWT token is unsupported: " + e.getMessage());
        } catch (SignatureException e) {
            System.err.println("Internal JWT signature invalid: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Internal JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Get username from internal JWT token
     */
    public String getUsernameFromInternalToken(String token) {
        return Jwts.parser()
                .verifyWith(getInternalSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
    
    /**
     * Get user ID from internal JWT token
     */
    public String getUserIdFromInternalToken(String token) {
        return Jwts.parser()
                .verifyWith(getInternalSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("userId", String.class);
    }
    
    /**
     * Get authorities from internal JWT token
     */
    public String getAuthoritiesFromInternalToken(String token) {
        return Jwts.parser()
                .verifyWith(getInternalSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("authorities", String.class);
    }
    
    /**
     * Get token expiration time
     */
    public Date getExpirationFromInternalToken(String token) {
        return Jwts.parser()
                .verifyWith(getInternalSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }
    
    /**
     * Check if internal token is expired
     */
    public boolean isInternalTokenExpired(String token) {
        try {
            Date expiration = getExpirationFromInternalToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
