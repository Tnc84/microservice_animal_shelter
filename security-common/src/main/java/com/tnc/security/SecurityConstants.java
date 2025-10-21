package com.tnc.security;

/**
 * Security constants for reusable security library
 * Contains common security-related constants used across microservices
 */
public class SecurityConstants {
    
    // HTTP Headers
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String INTERNAL_TOKEN_HEADER = "X-Internal-Token";
    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USER_NAME_HEADER = "X-User-Name";
    public static final String USER_AUTHORITIES_HEADER = "X-User-Authorities";
    
    // Token Types
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String INTERNAL_TOKEN_TYPE = "internal";
    public static final String REFRESH_TOKEN_TYPE = "refresh";
    
    // Security Roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";
    public static final String ROLE_SHELTER_MANAGER = "SHELTER_MANAGER";
    public static final String ROLE_VET = "VET";
    
    // Public URLs (commonly used across microservices)
    public static final String[] PUBLIC_URLS = {
        "/actuator/health",
        "/actuator/info",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/auth/**"
    };
    
    // Security Configuration
    public static final String JWT_SECRET_PROPERTY = "jwt.secret";
    public static final String JWT_EXPIRATION_PROPERTY = "jwt.expiration";
    public static final String JWT_REFRESH_EXPIRATION_PROPERTY = "jwt.refresh-expiration";
    public static final String JWT_INTERNAL_SECRET_PROPERTY = "jwt.internal.secret";
    public static final String JWT_INTERNAL_EXPIRATION_PROPERTY = "jwt.internal.expiration";
    
    // Default Values
    public static final String DEFAULT_JWT_SECRET = "mySecretKey";
    public static final long DEFAULT_JWT_EXPIRATION = 900000L; // 15 minutes
    public static final long DEFAULT_REFRESH_EXPIRATION = 604800000L; // 7 days
    public static final String DEFAULT_INTERNAL_SECRET = "internalSecretKey";
    public static final long DEFAULT_INTERNAL_EXPIRATION = 600000L; // 10 minutes
}
