package com.tnc.userManagement.service;

import com.tnc.userManagement.repository.RefreshTokenRepository;
import com.tnc.userManagement.repository.entity.RefreshToken;
import com.tnc.userManagement.service.model.UserDomain;
import com.tnc.userManagement.service.security.UserPrincipal;
import com.tnc.userManagement.service.security.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenRefreshService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final IUserService userService;

    /**
     * Create and save refresh token for user
     */
    @Transactional
    public RefreshToken createRefreshToken(String userId, String deviceInfo, String ipAddress) {
        // Revoke existing tokens for this user
        revokeAllUserTokens(userId);
        
        // Generate new refresh token
        String tokenValue = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(7);
        
        RefreshToken refreshToken = RefreshToken.builder()
                .token(tokenValue)
                .userId(userId)
                .expiryDate(expiryDate)
                .createdDate(LocalDateTime.now())
                .isRevoked(false)
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .build();
        
        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Validate refresh token and generate new access token
     */
    @Transactional
    public Optional<String> refreshAccessToken(String refreshTokenValue) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(refreshTokenValue);
        
        if (refreshTokenOpt.isEmpty()) {
            log.warn("Refresh token not found: {}", refreshTokenValue);
            return Optional.empty();
        }
        
        RefreshToken refreshToken = refreshTokenOpt.get();
        
        if (refreshToken.isRevoked()) {
            log.warn("Refresh token is revoked: {}", refreshTokenValue);
            return Optional.empty();
        }
        
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.warn("Refresh token is expired: {}", refreshTokenValue);
            refreshTokenRepository.delete(refreshToken);
            return Optional.empty();
        }
        
        // Get user and generate new access token
        try {
            // Find user by email (assuming userId is email)
            UserDomain user = userService.findByEmail(refreshToken.getUserId());
            if (user == null) {
                log.warn("User not found for refresh token: {}", refreshToken.getUserId());
                return Optional.empty();
            }
            
            UserPrincipal userPrincipal = new UserPrincipal(user);
            String newAccessToken = jwtTokenProvider.generateAccessToken(userPrincipal);
            return Optional.of(newAccessToken);
        } catch (Exception e) {
            log.error("Error generating new access token for user: {}", refreshToken.getUserId(), e);
            return Optional.empty();
        }
    }

    /**
     * Revoke all tokens for a specific user
     */
    @Transactional
    public void revokeAllUserTokens(String userId) {
        refreshTokenRepository.revokeAllTokensByUserId(userId);
        log.info("Revoked all tokens for user: {}", userId);
    }

    /**
     * Revoke specific refresh token
     */
    @Transactional
    public void revokeRefreshToken(String refreshTokenValue) {
        refreshTokenRepository.revokeToken(refreshTokenValue);
        log.info("Revoked refresh token: {}", refreshTokenValue);
    }

    /**
     * Get all valid tokens for a user
     */
    public List<RefreshToken> getUserValidTokens(String userId) {
        return refreshTokenRepository.findValidTokensByUserId(userId);
    }

    /**
     * Clean up expired tokens
     */
    @Transactional
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
        log.info("Cleaned up expired refresh tokens");
    }

    /**
     * Validate refresh token exists and is valid
     */
    public boolean isRefreshTokenValid(String refreshTokenValue) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(refreshTokenValue);
        
        if (refreshTokenOpt.isEmpty()) {
            return false;
        }
        
        RefreshToken refreshToken = refreshTokenOpt.get();
        return !refreshToken.isRevoked() && refreshToken.getExpiryDate().isAfter(LocalDateTime.now());
    }
}
