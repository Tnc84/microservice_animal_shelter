package com.tnc.userManagement.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for animal events received from RabbitMQ.
 * Contains all necessary information for processing animal-related events.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimalEventDTO {
    
    private Long animalId;
    private String eventType; // CREATED, UPDATED, ADOPTED, DELETED
    private String animalName;
    private String animalType;
    private String animalBreed;
    private Integer animalAge;
    private String animalStatus; // AVAILABLE, ADOPTED, MEDICAL_CARE, etc.
    private Long shelterId;
    private String shelterName;
    private String shelterAddress;
    private Long userId; // User who performed the action (for adoption events)
    private String userEmail; // Email of the user who performed the action
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String description; // Additional context about the event
    
    // Helper methods for event type constants
    public static final String EVENT_ANIMAL_CREATED = "ANIMAL_CREATED";
    public static final String EVENT_ANIMAL_UPDATED = "ANIMAL_UPDATED";
    public static final String EVENT_ANIMAL_ADOPTED = "ANIMAL_ADOPTED";
    public static final String EVENT_ANIMAL_DELETED = "ANIMAL_DELETED";
    
    // Helper methods for animal status constants
    public static final String STATUS_AVAILABLE = "AVAILABLE";
    public static final String STATUS_ADOPTED = "ADOPTED";
    public static final String STATUS_MEDICAL_CARE = "MEDICAL_CARE";
    public static final String STATUS_PENDING_ADOPTION = "PENDING_ADOPTION";
}
