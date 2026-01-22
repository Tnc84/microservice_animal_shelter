package org.tnc.pethotelmicroservice.service.domain;

import java.time.LocalDateTime;

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
