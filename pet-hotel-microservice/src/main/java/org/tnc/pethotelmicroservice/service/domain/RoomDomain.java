package org.tnc.pethotelmicroservice.service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain model for Room - used in service layer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDomain {
    private Long id;
    private int capacity;
    private String roomType;
    private boolean isAvailable;
    private String roomDescription;
    private LocalDateTime startDate;
    private LocalDateTime updated;
    private LocalDateTime endDate;
}
