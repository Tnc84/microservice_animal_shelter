package com.tnc.userManagement.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing user notifications.
 * Stores in-app notifications for users about various system events.
 */
@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "title", nullable = false, length = 255)
    private String title;
    
    @Column(name = "message", nullable = false, length = 1000)
    private String message;
    
    @Column(name = "type", nullable = false, length = 50)
    private String type; // ANIMAL_ADOPTED, ANIMAL_CREATED, ANIMAL_UPDATED, etc.
    
    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;
    
    @Column(name = "animal_id")
    private Long animalId; // Reference to the animal if notification is animal-related
    
    @Column(name = "shelter_id")
    private Long shelterId; // Reference to the shelter if notification is shelter-related
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    // Notification type constants
    public static final String TYPE_ANIMAL_CREATED = "ANIMAL_CREATED";
    public static final String TYPE_ANIMAL_UPDATED = "ANIMAL_UPDATED";
    public static final String TYPE_ANIMAL_ADOPTED = "ANIMAL_ADOPTED";
    public static final String TYPE_ANIMAL_DELETED = "ANIMAL_DELETED";
    public static final String TYPE_SHELTER_UPDATE = "SHELTER_UPDATE";
    public static final String TYPE_SYSTEM = "SYSTEM";
    
    /**
     * Mark notification as read.
     */
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }
}
