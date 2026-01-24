package com.tnc.userManagement.service;

import com.tnc.resilience.util.ResilienceExecutor;
import com.tnc.userManagement.repository.NotificationRepository;
import com.tnc.userManagement.repository.entity.Notification;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Service for managing user notifications with integrated circuit breaker and retry patterns.
 * Handles creation, retrieval, and management of in-app notifications.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;

    /**
     * Execute a database operation with circuit breaker and retry protection.
     */
    private <R> R executeDatabase(Supplier<R> operation) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("database");
        Retry retry = retryRegistry.retry("database");
        Supplier<R> decoratedSupplier = ResilienceExecutor.decorateSupplier(circuitBreaker, retry, operation);
        return decoratedSupplier.get();
    }
    
    /**
     * Create a new notification for a user.
     * 
     * @param userId the user ID
     * @param title the notification title
     * @param message the notification message
     * @param type the notification type
     * @param animalId optional animal ID if notification is animal-related
     * @param shelterId optional shelter ID if notification is shelter-related
     * @return the created notification
     */
    @Transactional
    public Notification createNotification(Long userId, String title, String message, String type, 
                                        Long animalId, Long shelterId) {
        log.info("Creating notification for user ID: {}, type: {}", userId, type);
        
        return executeDatabase(() -> {
            Notification notification = Notification.builder()
                    .userId(userId)
                    .title(title)
                    .message(message)
                    .type(type)
                    .animalId(animalId)
                    .shelterId(shelterId)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            
            Notification savedNotification = notificationRepository.save(notification);
            log.info("Successfully created notification ID: {} for user ID: {}", savedNotification.getId(), userId);
            return savedNotification;
        });
    }

    /**
     * Create a booking-related notification for a user.
     * 
     * @param userId the user ID
     * @param title the notification title
     * @param message the notification message
     * @param type the notification type
     * @param bookingId the booking ID
     * @return the created notification
     */
    @Transactional
    public Notification createBookingNotification(Long userId, String title, String message, 
                                                   String type, Long bookingId) {
        log.info("Creating booking notification for user ID: {}, bookingId: {}, type: {}", 
                userId, bookingId, type);
        
        return executeDatabase(() -> {
            Notification notification = Notification.builder()
                    .userId(userId)
                    .title(title)
                    .message(message)
                    .type(type)
                    .bookingId(bookingId)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            
            Notification savedNotification = notificationRepository.save(notification);
            log.info("Successfully created booking notification ID: {} for user ID: {}", 
                    savedNotification.getId(), userId);
            return savedNotification;
        });
    }
    
    /**
     * Get paginated notifications for a user.
     * 
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of notifications
     */
    public Page<Notification> getUserNotifications(Long userId, Pageable pageable) {
        log.debug("Retrieving notifications for user ID: {}", userId);
        return executeDatabase(() -> 
            notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
        );
    }
    
    /**
     * Get all unread notifications for a user.
     * 
     * @param userId the user ID
     * @return list of unread notifications
     */
    public List<Notification> getUnreadNotifications(Long userId) {
        log.debug("Retrieving unread notifications for user ID: {}", userId);
        return executeDatabase(() -> 
            notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)
        );
    }
    
    /**
     * Get notification count for a user.
     * 
     * @param userId the user ID
     * @return total notification count
     */
    public long getNotificationCount(Long userId) {
        log.debug("Getting notification count for user ID: {}", userId);
        return executeDatabase(() -> 
            notificationRepository.countByUserIdAndIsReadFalse(userId)
        );
    }
    
    /**
     * Get notifications by type for a user.
     * 
     * @param userId the user ID
     * @param type the notification type
     * @param pageable pagination information
     * @return page of notifications
     */
    public Page<Notification> getNotificationsByType(Long userId, String type, Pageable pageable) {
        log.debug("Retrieving notifications for user ID: {}, type: {}", userId, type);
        return executeDatabase(() -> 
            notificationRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, type, pageable)
        );
    }
    
    /**
     * Mark a notification as read.
     * 
     * @param notificationId the notification ID
     * @param userId the user ID (for security)
     * @return true if marked as read, false if not found or not owned by user
     */
    @Transactional
    public boolean markAsRead(Long notificationId, Long userId) {
        log.info("Marking notification ID: {} as read for user ID: {}", notificationId, userId);
        
        return executeDatabase(() -> {
            Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
            if (notificationOpt.isPresent() && notificationOpt.get().getUserId().equals(userId)) {
                Notification notification = notificationOpt.get();
                notification.markAsRead();
                notificationRepository.save(notification);
                log.info("Successfully marked notification ID: {} as read", notificationId);
                return true;
            }
            
            log.warn("Notification ID: {} not found or not owned by user ID: {}", notificationId, userId);
            return false;
        });
    }
    
    /**
     * Mark all notifications as read for a user.
     * 
     * @param userId the user ID
     * @return number of notifications marked as read
     */
    @Transactional
    public int markAllAsRead(Long userId) {
        log.info("Marking all notifications as read for user ID: {}", userId);
        return executeDatabase(() -> {
            int updatedCount = notificationRepository.markAllAsReadByUserId(userId);
            log.info("Marked {} notifications as read for user ID: {}", updatedCount, userId);
            return updatedCount;
        });
    }
    
    /**
     * Delete a notification.
     * 
     * @param notificationId the notification ID
     * @param userId the user ID (for security)
     * @return true if deleted, false if not found or not owned by user
     */
    @Transactional
    public boolean deleteNotification(Long notificationId, Long userId) {
        log.info("Deleting notification ID: {} for user ID: {}", notificationId, userId);
        
        return executeDatabase(() -> {
            Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
            if (notificationOpt.isPresent() && notificationOpt.get().getUserId().equals(userId)) {
                notificationRepository.deleteById(notificationId);
                log.info("Successfully deleted notification ID: {}", notificationId);
                return true;
            }
            
            log.warn("Notification ID: {} not found or not owned by user ID: {}", notificationId, userId);
            return false;
        });
    }
    
    /**
     * Clean up old notifications (older than specified days).
     * 
     * @param daysToKeep number of days to keep notifications
     * @return number of deleted notifications
     */
    @Transactional
    public int cleanupOldNotifications(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        log.info("Cleaning up notifications older than: {}", cutoffDate);
        
        return executeDatabase(() -> {
            int deletedCount = notificationRepository.deleteOldNotifications(cutoffDate);
            log.info("Deleted {} old notifications", deletedCount);
            return deletedCount;
        });
    }
}
