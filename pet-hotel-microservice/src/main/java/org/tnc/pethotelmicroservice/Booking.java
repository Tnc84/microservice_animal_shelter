package org.tnc.pethotelmicroservice;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String userId;

    @OneToOne(cascade = CascadeType.ALL)
    private Room roomId;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id) && Objects.equals(userId, booking.userId) && Objects.equals(roomId, booking.roomId) && Objects.equals(petName, booking.petName) && Objects.equals(petSpecies, booking.petSpecies) && Objects.equals(petBreed, booking.petBreed) && Objects.equals(checkInDate, booking.checkInDate) && Objects.equals(checkOutDate, booking.checkOutDate) && Objects.equals(totalDays, booking.totalDays) && status == booking.status && Objects.equals(startDate, booking.startDate) && Objects.equals(updatedDate, booking.updatedDate) && Objects.equals(endDate, booking.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, roomId, petName, petSpecies, petBreed, checkInDate, checkOutDate, totalDays, status, startDate, updatedDate, endDate);
    }
}
