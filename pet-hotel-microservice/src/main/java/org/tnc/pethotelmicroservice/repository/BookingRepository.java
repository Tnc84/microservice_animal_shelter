package org.tnc.pethotelmicroservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tnc.pethotelmicroservice.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {

}
