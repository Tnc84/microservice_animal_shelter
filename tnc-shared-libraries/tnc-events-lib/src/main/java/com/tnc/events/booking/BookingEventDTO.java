package com.tnc.events.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for booking events published/consumed via RabbitMQ.
 * Shared between Pet Hotel Service (publisher) and consuming services (User, Notification).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingEventDTO {

    private Long bookingId;
    private String eventType;
    private Long userId;
    private String userEmail;
    private Long roomId;
    private String roomType;
    private String petName;
    private String petSpecies;
    private String petBreed;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime checkInDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime checkOutDate;

    private Integer totalDays;
    private BigDecimal totalPrice;
    private String bookingStatus;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private String description;

    // Event type constants
    public static final String EVENT_BOOKING_CREATED = "BOOKING_CREATED";
    public static final String EVENT_BOOKING_CONFIRMED = "BOOKING_CONFIRMED";
    public static final String EVENT_BOOKING_CANCELLED = "BOOKING_CANCELLED";
    public static final String EVENT_BOOKING_COMPLETED = "BOOKING_COMPLETED";
    public static final String EVENT_BOOKING_UPDATED = "BOOKING_UPDATED";

    // Booking status constants
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_NO_SHOW = "NO_SHOW";
}
