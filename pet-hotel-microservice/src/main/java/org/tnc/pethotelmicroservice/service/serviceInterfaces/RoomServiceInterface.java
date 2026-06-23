package org.tnc.pethotelmicroservice.service.serviceInterfaces;

import org.tnc.pethotelmicroservice.service.domain.RoomDomain;

import java.util.List;

public interface RoomServiceInterface {
    RoomDomain getRoomById(Integer roomId);
    RoomDomain updateRoomCapacity(RoomDomain capacity);
    List<RoomDomain> getAllRooms();
    List<RoomDomain> getAvailableRooms();
    RoomDomain createRoom(RoomDomain roomDomain);
    RoomDomain updateRoom(Integer roomId, RoomDomain roomDomain);
    void deleteRoom(Integer roomId);
}
