package org.tnc.pethotelmicroservice.repository.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter @Setter
@RequiredArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private int capacity;
    private String roomType;
    private boolean isAvailable;
    private String roomDescription;
    private LocalDateTime startDate;
    private LocalDateTime updated;
    private LocalDateTime endDate;
    // Indexes: `idx_room_number`, `idx_room_available`


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return capacity == room.capacity && isAvailable == room.isAvailable && Objects.equals(id, room.id) && Objects.equals(roomType, room.roomType) && Objects.equals(roomDescription, room.roomDescription) && Objects.equals(startDate, room.startDate) && Objects.equals(updated, room.updated) && Objects.equals(endDate, room.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, capacity, roomType, isAvailable, roomDescription, startDate, updated, endDate);
    }
}
