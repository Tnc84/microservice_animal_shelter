package com.tnc.userManagement.controller;

import com.tnc.userManagement.repository.entity.RefreshToken;
import com.tnc.userManagement.service.IUserService;
import com.tnc.userManagement.service.TokenRefreshService;
import com.tnc.userManagement.service.constant.SecurityConstant;
import com.tnc.userManagement.service.exception.EmailExistException;
import com.tnc.userManagement.service.model.UserDomain;
import com.tnc.userManagement.service.security.UserPrincipal;
import com.tnc.userManagement.service.security.util.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Optional;

/**
 * Authentication Controller for JWT token generation
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication endpoints for JWT token generation")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final IUserService userService;
    private final TokenRefreshService tokenRefreshService;

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticate user and return JWT tokens")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest, 
                                            HttpServletRequest request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.email(),
                loginRequest.password()
            )
        );

        // Get user details
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserDomain user = userPrincipal.getUser();

        // Generate token pair (access + refresh)
        JwtTokenProvider.TokenPair tokenPair = jwtTokenProvider.generateTokenPair(userPrincipal);
        
        // Create and save refresh token
        String deviceInfo = request.getHeader("User-Agent");
        String ipAddress = getClientIpAddress(request);
        RefreshToken refreshToken = tokenRefreshService.createRefreshToken(
            user.getUserId(), deviceInfo, ipAddress);

        // Create HttpOnly cookie for refresh token
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
                .secure(false) // Set to true in production with HTTPS
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        // Return response with access token in header and refresh token in cookie
        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, tokenPair.accessToken())
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            .body(new LoginResponse(tokenPair.accessToken(), user.getUserId(), user.getEmail(), user.getRole()));
    }

    @PostMapping("/register")
    @Operation(summary = "User Registration", description = "Register new user and return JWT tokens")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest registerRequest, 
                                               HttpServletRequest request) 
            throws EmailExistException {
        
        // Create new user
        UserDomain newUser = userService.addNewUserWithPassword(
            registerRequest.firstName(),
            registerRequest.lastName(),
            registerRequest.email(),
            registerRequest.password(),
            "USER", // Default role
            true,
            true
        );

        UserPrincipal userPrincipal = new UserPrincipal(newUser);
        
        // Generate token pair (access + refresh)
        JwtTokenProvider.TokenPair tokenPair = jwtTokenProvider.generateTokenPair(userPrincipal);
        
        // Create and save refresh token
        String deviceInfo = request.getHeader("User-Agent");
        String ipAddress = getClientIpAddress(request);
        RefreshToken refreshToken = tokenRefreshService.createRefreshToken(
            newUser.getUserId(), deviceInfo, ipAddress);

        // Create HttpOnly cookie for refresh token
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
                .secure(false) // Set to true in production with HTTPS
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
            .header(HttpHeaders.AUTHORIZATION, tokenPair.accessToken())
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            .body(new LoginResponse(tokenPair.accessToken(), newUser.getUserId(), newUser.getEmail(), newUser.getRole()));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token", description = "Generate new access token using refresh token")
    public ResponseEntity<RefreshResponse> refreshToken(HttpServletRequest request) {
        // Get refresh token from cookie
        String refreshTokenValue = getRefreshTokenFromCookie(request);
        
        if (refreshTokenValue == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new RefreshResponse(null, "Refresh token not found"));
        }

        // Validate and generate new access token
        Optional<String> newAccessToken = tokenRefreshService.refreshAccessToken(refreshTokenValue);
        
        if (newAccessToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new RefreshResponse(null, "Invalid or expired refresh token"));
        }

        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, newAccessToken.get())
            .body(new RefreshResponse(newAccessToken.get(), "Token refreshed successfully"));
    }

    @PostMapping("/logout")
    @Operation(summary = "User Logout", description = "Logout user and invalidate tokens")
    public ResponseEntity<LogoutResponse> logout(HttpServletRequest request) {
        // Get refresh token from cookie
        String refreshTokenValue = getRefreshTokenFromCookie(request);
        
        if (refreshTokenValue != null) {
            // Revoke refresh token
            tokenRefreshService.revokeRefreshToken(refreshTokenValue);
        }

        // Clear refresh token cookie
        ResponseCookie clearCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
            .body(new LogoutResponse("Logged out successfully"));
    }

    // DTOs for request/response using records
    public record LoginRequest(
            String email,
            String password
    ) {}

    public record RegisterRequest(
            String firstName,
            String lastName,
            String email,
            String password
    ) {}

    public record LoginResponse(
            String token,
            String userId,
            String email,
            String role
    ) {}

    public record RefreshResponse(
            String accessToken,
            String message
    ) {}

    public record LogoutResponse(
            String message
    ) {}

    // Helper methods
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
