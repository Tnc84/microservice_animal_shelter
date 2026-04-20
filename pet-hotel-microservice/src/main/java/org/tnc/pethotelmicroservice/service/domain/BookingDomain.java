package org.tnc.pethotelmicroservice.service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tnc.pethotelmicroservice.repository.entities.BookingStatus;

import java.time.LocalDateTime;

/**
 * Domain model for Booking - used in service layer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDomain {
    private Long id;
    private Long userId;
    private RoomDomain roomId;
    private String petName;
    private String petSpecies;
    private String petBreed;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private Integer totalDays;
    private BookingStatus status;
    private LocalDateTime startDate;
    private LocalDateTime updatedDate;
    private LocalDateTime endDate;
}
