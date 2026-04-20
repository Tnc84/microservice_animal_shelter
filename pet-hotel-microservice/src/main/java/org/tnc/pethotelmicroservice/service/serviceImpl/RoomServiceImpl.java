package org.tnc.pethotelmicroservice.service.serviceImpl;

import com.tnc.resilience.util.ResilienceExecutor;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tnc.pethotelmicroservice.repository.interfaces.RoomRepository;
import org.tnc.pethotelmicroservice.service.domain.RoomDomain;
import org.tnc.pethotelmicroservice.service.serviceInterfaces.RoomServiceInterface;
import org.tnc.pethotelmicroservice.service.serviceMapper.ServiceMapper;

import java.util.List;
import java.util.function.Supplier;

/**
 * Room service implementation with integrated circuit breaker and retry patterns.
 * Works with domain models and uses mapper for entity conversion.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomServiceInterface {

    private final RoomRepository roomRepository;
    private final ServiceMapper serviceMapper;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;

    /**
     * Execute a database operation with circuit breaker and retry protection.
     */
    private <R> R executeDatabase(Supplier<R> operation) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("database");
        Retry retry = retryRegistry.retry("database");
        Supplier<R> decoratedSupplier = ResilienceExecutor.decorateSupplier(circuitBreaker, retry, operation);
        return decoratedSupplier.get();
    }

    @Override
    public RoomDomain getRoomById(Integer roomId) {
        log.debug("Getting room by ID: {}", roomId);
        return executeDatabase(() -> 
            serviceMapper.roomToRoomDomain(roomRepository.getRoomById(roomId))
        );
    }

    @Override
    public RoomDomain updateRoomCapacity(RoomDomain roomDomain) {
        log.info("Updating room capacity for room ID: {}", roomDomain.getId());
        
        return executeDatabase(() -> {
            var roomEntity = serviceMapper.roomDomainToRoom(roomDomain);
            var existingRoom = roomRepository.getRoomById(roomEntity.getId());
            if (existingRoom == null) {
                log.warn("Room not found with ID: {}", roomEntity.getId());
                return null;
            }
            existingRoom.setCapacity(roomEntity.getCapacity());
            var updatedRoom = roomRepository.save(existingRoom);
            log.info("Room capacity updated: {}", roomEntity.getId());
            return serviceMapper.roomToRoomDomain(updatedRoom);
        });
    }

    @Override
    public List<RoomDomain> getAllRooms() {
        log.debug("Getting all rooms");
        return executeDatabase(() -> 
            serviceMapper.ListRoomToRoomDomain(roomRepository.findAll())
        );
    }

    @Override
    public List<RoomDomain> getAvailableRooms() {
        log.debug("Getting available rooms");
        return executeDatabase(() -> 
            serviceMapper.ListRoomToRoomDomain(roomRepository.findByIsAvailableTrue())
        );
    }

    @Override
    public RoomDomain createRoom(RoomDomain roomDomain) {
        log.info("Creating new room: type={}", roomDomain.getRoomType());
        
        return executeDatabase(() -> {
            var roomEntity = serviceMapper.roomDomainToRoom(roomDomain);
            var savedRoom = roomRepository.save(roomEntity);
            log.info("Room created with ID: {}", savedRoom.getId());
            return serviceMapper.roomToRoomDomain(savedRoom);
        });
    }

    @Override
    public RoomDomain updateRoom(Integer roomId, RoomDomain roomDomain) {
        log.info("Updating room ID: {}", roomId);
        
        return executeDatabase(() -> {
            var existingRoom = roomRepository.getRoomById(roomId.longValue());
            if (existingRoom == null) {
                log.warn("Room not found with ID: {}", roomId);
                return null;
            }
            
            var roomEntity = serviceMapper.roomDomainToRoom(roomDomain);
            existingRoom.setCapacity(roomEntity.getCapacity());
            existingRoom.setRoomType(roomEntity.getRoomType());
            existingRoom.setAvailable(roomEntity.isAvailable());
            existingRoom.setRoomDescription(roomEntity.getRoomDescription());
            
            var updatedRoom = roomRepository.save(existingRoom);
            log.info("Room updated: {}", roomId);
            return serviceMapper.roomToRoomDomain(updatedRoom);
        });
    }

    @Override
    public void deleteRoom(Integer roomId) {
        log.info("Deleting room ID: {}", roomId);
        
        executeDatabase(() -> {
            roomRepository.deleteById(roomId.longValue());
            log.info("Room deleted: {}", roomId);
            return null;
        });
    }
}
