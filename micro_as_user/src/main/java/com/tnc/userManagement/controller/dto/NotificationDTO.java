package com.tnc.userManagement.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for notification data in REST API responses.
 * Contains all necessary information for displaying notifications to users.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    
    private Long id;
    private Long userId;
    private String title;
    private String message;
    private String type;
    private Boolean isRead;
    private Long animalId;
    private Long shelterId;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime readAt;
    
    // Notification type constants
    public static final String TYPE_ANIMAL_CREATED = "ANIMAL_CREATED";
    public static final String TYPE_ANIMAL_UPDATED = "ANIMAL_UPDATED";
    public static final String TYPE_ANIMAL_ADOPTED = "ANIMAL_ADOPTED";
    public static final String TYPE_ANIMAL_DELETED = "ANIMAL_DELETED";
    public static final String TYPE_SHELTER_UPDATE = "SHELTER_UPDATE";
    public static final String TYPE_SYSTEM = "SYSTEM";
}
