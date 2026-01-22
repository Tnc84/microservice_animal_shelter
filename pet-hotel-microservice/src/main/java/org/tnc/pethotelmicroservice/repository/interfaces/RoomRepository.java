package org.tnc.pethotelmicroservice.repository.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tnc.pethotelmicroservice.repository.entities.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {

}
