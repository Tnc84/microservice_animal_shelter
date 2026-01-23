package org.tnc.pethotelmicroservice.controler;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tnc.pethotelmicroservice.controler.dtoMapper.DtoMapper;
import org.tnc.pethotelmicroservice.controler.records.RoomDTO;
import org.tnc.pethotelmicroservice.service.serviceInterfaces.RoomServiceInterface;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final DtoMapper dtoMapper;
    private final RoomServiceInterface roomService;

    @GetMapping
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        var rooms = dtoMapper.listRoomDomainToRoomDto(roomService.getAllRooms());
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable Integer id) {
        var roomDomain = roomService.getRoomById(id);
        if (roomDomain == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dtoMapper.roomDomainToRoomDto(roomDomain));
    }

    @GetMapping("/available")
    public ResponseEntity<List<RoomDTO>> getAvailableRooms() {
        var availableRooms = dtoMapper.listRoomDomainToRoomDto(roomService.getAvailableRooms());
        return ResponseEntity.ok(availableRooms);
    }

    // TODO: Add @PreAuthorize("hasRole('ADMIN')") or similar security annotation
    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(@RequestBody RoomDTO roomDTO) {
        var roomDomain = dtoMapper.roomDtoToRoomDomain(roomDTO);
        var createdRoom = roomService.createRoom(roomDomain);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dtoMapper.roomDomainToRoomDto(createdRoom));
    }

    // TODO: Add @PreAuthorize("hasRole('ADMIN')") or similar security annotation
    @PutMapping("/{id}")
    public ResponseEntity<RoomDTO> updateRoom(@PathVariable Integer id, @RequestBody RoomDTO roomDTO) {
        var roomDomain = dtoMapper.roomDtoToRoomDomain(roomDTO);
        var updatedRoom = roomService.updateRoom(id, roomDomain);
        if (updatedRoom == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dtoMapper.roomDomainToRoomDto(updatedRoom));
    }

    @PutMapping("/{id}/capacity")
    public ResponseEntity<RoomDTO> updateRoomCapacity(@PathVariable Integer id, @RequestBody RoomDTO roomDTO) {
        var roomDomain = dtoMapper.roomDtoToRoomDomain(roomDTO);
        var updatedRoom = roomService.updateRoomCapacity(roomDomain);
        if (updatedRoom == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dtoMapper.roomDomainToRoomDto(updatedRoom));
    }

    // TODO: Add @PreAuthorize("hasRole('ADMIN')") or similar security annotation
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Integer id) {
        var roomDomain = roomService.getRoomById(id);
        if (roomDomain == null) {
            return ResponseEntity.notFound().build();
        }
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
