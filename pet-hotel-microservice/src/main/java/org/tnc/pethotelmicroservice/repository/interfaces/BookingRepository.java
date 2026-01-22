package org.tnc.pethotelmicroservice.repository.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tnc.pethotelmicroservice.repository.entities.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {

}
