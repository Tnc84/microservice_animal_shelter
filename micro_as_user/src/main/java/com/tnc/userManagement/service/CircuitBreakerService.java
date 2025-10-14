package com.tnc.userManagement.service;

import com.tnc.userManagement.repository.entity.Notification;
import com.tnc.userManagement.repository.NotificationRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Service with circuit breaker patterns for database operations.
 * Provides fallback mechanisms for database failures.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CircuitBreakerService {
    
    private final NotificationRepository notificationRepository;
    
    /**
     * Get all notifications with circuit breaker protection.
     */
    @CircuitBreaker(name = "database", fallbackMethod = "getAllNotificationsFallback")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public CompletableFuture<List<Notification>> getAllNotifications() {
        log.info("Fetching all notifications from database");
        return CompletableFuture.completedFuture(notificationRepository.findAll());
    }
    
    /**
     * Get notifications by user ID with circuit breaker protection.
     */
    @CircuitBreaker(name = "database", fallbackMethod = "getNotificationsByUserIdFallback")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public CompletableFuture<List<Notification>> getNotificationsByUserId(Long userId) {
        log.info("Fetching notifications for user ID: {}", userId);
        return CompletableFuture.completedFuture(notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId));
    }
    
    /**
     * Get paginated notifications with circuit breaker protection.
     */
    @CircuitBreaker(name = "database", fallbackMethod = "getNotificationsPageFallback")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public CompletableFuture<Page<Notification>> getNotificationsPage(Pageable pageable) {
        log.info("Fetching paginated notifications");
        return CompletableFuture.completedFuture(notificationRepository.findAll(pageable));
    }
    
    /**
     * Get unread notifications count with circuit breaker protection.
     */
    @CircuitBreaker(name = "database", fallbackMethod = "getUnreadCountFallback")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public CompletableFuture<Long> getUnreadCount(Long userId) {
        log.info("Fetching unread count for user ID: {}", userId);
        return CompletableFuture.completedFuture(notificationRepository.countByUserIdAndIsReadFalse(userId));
    }
    
    /**
     * Save notification with circuit breaker protection.
     */
    @CircuitBreaker(name = "database", fallbackMethod = "saveNotificationFallback")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public CompletableFuture<Notification> saveNotification(Notification notification) {
        log.info("Saving notification: {}", notification.getTitle());
        return CompletableFuture.completedFuture(notificationRepository.save(notification));
    }
    
    // Fallback methods
    public CompletableFuture<List<Notification>> getAllNotificationsFallback(Exception ex) {
        log.warn("Database circuit breaker open, returning empty list for getAllNotifications. Error: {}", ex.getMessage());
        return CompletableFuture.completedFuture(Collections.emptyList());
    }
    
    public CompletableFuture<List<Notification>> getNotificationsByUserIdFallback(Long userId, Exception ex) {
        log.warn("Database circuit breaker open, returning empty list for user {}. Error: {}", userId, ex.getMessage());
        return CompletableFuture.completedFuture(Collections.emptyList());
    }
    
    public CompletableFuture<Page<Notification>> getNotificationsPageFallback(Pageable pageable, Exception ex) {
        log.warn("Database circuit breaker open, returning empty page. Error: {}", ex.getMessage());
        return CompletableFuture.completedFuture(Page.empty(pageable));
    }
    
    public CompletableFuture<Long> getUnreadCountFallback(Long userId, Exception ex) {
        log.warn("Database circuit breaker open, returning 0 for user {}. Error: {}", userId, ex.getMessage());
        return CompletableFuture.completedFuture(0L);
    }
    
    public CompletableFuture<Notification> saveNotificationFallback(Notification notification, Exception ex) {
        log.warn("Database circuit breaker open, returning null for save. Error: {}", ex.getMessage());
        return CompletableFuture.completedFuture(null);
    }
}
