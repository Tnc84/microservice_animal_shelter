package org.tnc.pethotelmicroservice.repository.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.tnc.pethotelmicroservice.repository.entities.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking getBookingById(long id);
    List<Booking> findByUserId(Long userId);
    List<Booking> findByRoomIdId(Long roomId);
    
    @Query("SELECT b FROM Booking b WHERE b.roomId.id = :roomId " +
           "AND b.status != 'CANCELLED' " +
           "AND ((b.checkInDate <= :checkIn AND b.checkOutDate > :checkIn) " +
           "OR (b.checkInDate < :checkOut AND b.checkOutDate >= :checkOut) " +
           "OR (b.checkInDate >= :checkIn AND b.checkOutDate <= :checkOut))")
    List<Booking> findOverlappingBookings(@Param("roomId") Long roomId, 
                                          @Param("checkIn") LocalDateTime checkIn, 
                                          @Param("checkOut") LocalDateTime checkOut);
}
