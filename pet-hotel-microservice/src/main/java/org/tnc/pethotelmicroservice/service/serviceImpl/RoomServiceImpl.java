package org.tnc.pethotelmicroservice.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tnc.pethotelmicroservice.repository.interfaces.RoomRepository;
import org.tnc.pethotelmicroservice.service.domain.RoomDomain;
import org.tnc.pethotelmicroservice.service.serviceInterfaces.RoomServiceInterface;
import org.tnc.pethotelmicroservice.service.serviceMapper.ServiceMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomServiceInterface {

    private final RoomRepository roomRepository;
    private final ServiceMapper serviceMapper;

    @Override
    public RoomDomain getRoomById(Integer roomId) {
        return serviceMapper.roomToRoomDomain(roomRepository.getRoomById(roomId));
    }

    @Override
    public RoomDomain updateRoomCapacity(RoomDomain roomDomain) {
        var roomEntity = serviceMapper.roomDomainToRoom(roomDomain);
        var existingRoom = roomRepository.getRoomById(roomEntity.getId());
        existingRoom.setCapacity(roomEntity.getCapacity());
        var updatedRoom = roomRepository.save(existingRoom);
        return serviceMapper.roomToRoomDomain(updatedRoom);
    }

    @Override
    public List<RoomDomain> getAllRooms() {
        return serviceMapper.ListRoomToRoomDomain(roomRepository.findAll());
    }
}
