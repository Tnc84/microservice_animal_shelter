package org.tnc.pethotelmicroservice.repository.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tnc.pethotelmicroservice.repository.entities.Room;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Room getRoomById(long roomId);
    List<Room> findByIsAvailableTrue();
}
