package com.tnc.events.animal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for animal events published/consumed via RabbitMQ.
 * Shared between Animal Service (publisher) and consuming services (Shelter, User).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnimalEventDTO {

    private Long animalId;
    private String eventType;
    private String animalName;
    private String animalType;
    private String animalBreed;
    private Integer animalAge;
    private String animalStatus;
    private Long shelterId;
    private String shelterName;
    private String shelterAddress;
    private Long userId;
    private String userEmail;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private String description;

    // Event type constants
    public static final String EVENT_ANIMAL_CREATED = "ANIMAL_CREATED";
    public static final String EVENT_ANIMAL_UPDATED = "ANIMAL_UPDATED";
    public static final String EVENT_ANIMAL_ADOPTED = "ANIMAL_ADOPTED";
    public static final String EVENT_ANIMAL_DELETED = "ANIMAL_DELETED";

    // Animal status constants
    public static final String STATUS_AVAILABLE = "AVAILABLE";
    public static final String STATUS_ADOPTED = "ADOPTED";
    public static final String STATUS_MEDICAL_CARE = "MEDICAL_CARE";
    public static final String STATUS_PENDING_ADOPTION = "PENDING_ADOPTION";
}
