package org.tnc.pethotelmicroservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tnc.pethotelmicroservice.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {

}
