package org.tnc.pethotelmicroservice.repository.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tnc.pethotelmicroservice.repository.entities.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

}
