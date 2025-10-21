package com.tnc.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

/**
 * JWT Service for token generation, validation, and parsing
 * Reusable across multiple microservices and projects
 */
@Component
public class JwtService {
    
    @Value("${jwt.secret:mySecretKey}")
    private String jwtSecret;
    
    @Value("${jwt.expiration:900000}")
    private long jwtExpirationInMs;
    
    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshTokenExpirationInMs;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    
    /**
     * Generate JWT token with user information
     */
    public String generateToken(String username, String userId, String authorities) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("authorities", authorities)
                .setIssuedAt(new java.util.Date())
                .setExpiration(new java.util.Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(getSigningKey())
                .compact();
    }
    
    /**
     * Generate refresh token
     */
    public String generateRefreshToken(String username, String userId) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("tokenType", "refresh")
                .setIssuedAt(new java.util.Date())
                .setExpiration(new java.util.Date(System.currentTimeMillis() + refreshTokenExpirationInMs))
                .signWith(getSigningKey())
                .compact();
    }
    
    /**
     * Get username from JWT token
     */
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
    
    /**
     * Get user ID from JWT token
     */
    public String getUserIdFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("userId", String.class);
    }
    
    /**
     * Get authorities from JWT token
     */
    public String getAuthoritiesFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("authorities", String.class);
    }
    
    /**
     * Validate JWT token
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            System.err.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("JWT token is unsupported: " + e.getMessage());
        } catch (SignatureException e) {
            System.err.println("JWT signature invalid: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Get token expiration time
     */
    public java.util.Date getExpirationFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }
    
    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            java.util.Date expiration = getExpirationFromToken(token);
            return expiration.before(new java.util.Date());
        } catch (Exception e) {
            return true;
        }
    }
}
