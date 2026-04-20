package com.tnc.userManagement.service.security.util;

import com.tnc.userManagement.service.security.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * JWT Token Provider for generating and validating JWT tokens
 */
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret:mySecretKey}")
    private String jwtSecret;

    @Value("${jwt.expiration:900000}") // 15 minutes
    private int jwtExpirationInMs;

    @Value("${jwt.refresh-expiration:604800000}") // 7 days
    private long refreshTokenExpirationInMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Generate Access Token (15 minutes)
     */
    public String generateAccessToken(UserPrincipal userPrincipal) {
        return generateJwtTokenFromUserPrincipal(userPrincipal);
    }

    /**
     * Generate Access Token from Authentication object
     */
    public String generateAccessToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return generateJwtTokenFromUserPrincipal(userPrincipal);
    }

    /**
     * Generate Refresh Token (7 days)
     */
    public String generateRefreshToken(UserPrincipal userPrincipal) {
        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .claim("userId", userPrincipal.getUserId())
                .claim("tokenType", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpirationInMs))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Generate Refresh Token from Authentication object
     */
    public String generateRefreshToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return generateRefreshToken(userPrincipal);
    }

    /**
     * Generate both Access and Refresh tokens
     */
    public TokenPair generateTokenPair(UserPrincipal userPrincipal) {
        String accessToken = generateAccessToken(userPrincipal);
        String refreshToken = generateRefreshToken(userPrincipal);
        return new TokenPair(accessToken, refreshToken);
    }

    /**
     * Generate both Access and Refresh tokens from Authentication
     */
    public TokenPair generateTokenPair(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return generateTokenPair(userPrincipal);
    }

    private String generateJwtTokenFromUserPrincipal(UserPrincipal userPrincipal) {
        String authorities = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .claim("userId", userPrincipal.getUserId())
                .claim("authorities", authorities)
                .claim("role", userPrincipal.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(getSigningKey())
                .compact();
    }

    // /**
    //  * Get username from JWT token
    //  */
    // public String getUsernameFromJwtToken(String token) {
    //     return Jwts.parser()
    //             .verifyWith(getSigningKey())
    //             .build()
    //             .parseSignedClaims(token)
    //             .getPayload()
    //             .getSubject();
    // }

    // /**
    //  * Get user ID from JWT token
    //  */
    // public String getUserIdFromJwtToken(String token) {
    //     return Jwts.parser()
    //             .verifyWith(getSigningKey())
    //             .build()
    //             .parseSignedClaims(token)
    //             .getPayload()
    //             .get("userId", String.class);
    // }

    // /**
    //  * Get authorities from JWT token
    //  */
    // public String getAuthoritiesFromJwtToken(String token) {
    //     return Jwts.parser()
    //             .verifyWith(getSigningKey())
    //             .build()
    //             .parseSignedClaims(token)
    //             .getPayload()
    //             .get("authorities", String.class);
    // }

    // /**
    //  * Validate JWT token
    //  */
    // public boolean validateJwtToken(String authToken) {
    //     try {
    //         Jwts.parser()
    //                 .verifyWith(getSigningKey())
    //                 .build()
    //                 .parseSignedClaims(authToken);
    //         return true;
    //     } catch (MalformedJwtException e) {
    //         System.err.println("Invalid JWT token: " + e.getMessage());
    //     } catch (ExpiredJwtException e) {
    //         System.err.println("JWT token is expired: " + e.getMessage());
    //     } catch (UnsupportedJwtException e) {
    //         System.err.println("JWT token is unsupported: " + e.getMessage());
    //     } catch (IllegalArgumentException e) {
    //         System.err.println("JWT claims string is empty: " + e.getMessage());
    //     }
    //     return false;
    // }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Get token type from JWT token
     */
    public String getTokenType(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("tokenType", String.class);
    }

    /**
     * Check if token is a refresh token
     */
    public boolean isRefreshToken(String token) {
        return "refresh".equals(getTokenType(token));
    }

    /**
     * Token Pair record for returning both tokens
     */
    public record TokenPair(String accessToken, String refreshToken) {}
}
