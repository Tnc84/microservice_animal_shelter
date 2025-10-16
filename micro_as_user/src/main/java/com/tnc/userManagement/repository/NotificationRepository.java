package com.tnc.userManagement.repository;

import com.tnc.userManagement.repository.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for managing notification entities.
 * Provides methods for querying user notifications with various filters.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * Find all notifications for a specific user.
     * 
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of notifications
     */
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * Find all unread notifications for a specific user.
     * 
     * @param userId the user ID
     * @return list of unread notifications
     */
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);
    
    /**
     * Count unread notifications for a specific user.
     * 
     * @param userId the user ID
     * @return count of unread notifications
     */
    long countByUserIdAndIsReadFalse(Long userId);
    
    /**
     * Find notifications by user and type.
     * 
     * @param userId the user ID
     * @param type the notification type
     * @param pageable pagination information
     * @return page of notifications
     */
    Page<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, String type, Pageable pageable);
    
    /**
     * Find notifications created after a specific date.
     * 
     * @param userId the user ID
     * @param afterDate the date to filter from
     * @return list of notifications
     */
    List<Notification> findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(Long userId, LocalDateTime afterDate);
    
    /**
     * Find notifications by animal ID.
     * 
     * @param animalId the animal ID
     * @return list of notifications
     */
    List<Notification> findByAnimalIdOrderByCreatedAtDesc(Long animalId);
    
    /**
     * Mark all notifications as read for a specific user.
     * 
     * @param userId the user ID
     * @return number of updated notifications
     */
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.userId = :userId AND n.isRead = false")
    int markAllAsReadByUserId(@Param("userId") Long userId);
    
    /**
     * Delete old notifications (older than specified days).
     * 
     * @param cutoffDate the cutoff date
     * @return number of deleted notifications
     */
    @Query("DELETE FROM Notification n WHERE n.createdAt < :cutoffDate")
    int deleteOldNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);
}
