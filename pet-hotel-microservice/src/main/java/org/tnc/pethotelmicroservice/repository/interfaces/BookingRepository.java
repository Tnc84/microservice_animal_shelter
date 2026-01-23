package org.tnc.pethotelmicroservice.repository.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tnc.pethotelmicroservice.repository.entities.Booking;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking getBookingById(long id);
}
