package org.tnc.pethotelmicroservice.service.serviceImpl;

import com.tnc.resilience.util.ResilienceExecutor;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tnc.pethotelmicroservice.repository.entities.BookingStatus;
import org.tnc.pethotelmicroservice.repository.interfaces.BookingRepository;
import org.tnc.pethotelmicroservice.repository.interfaces.RoomRepository;
import org.tnc.pethotelmicroservice.service.domain.BookingDomain;
import org.tnc.pethotelmicroservice.service.serviceInterfaces.BookingServiceInterface;
import org.tnc.pethotelmicroservice.service.serviceMapper.ServiceMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

/**
 * Booking service implementation with integrated circuit breaker and retry patterns.
 * Works with domain models and uses mapper for entity conversion.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingServiceInterface {

    private final BookingRepository bookingRepository;
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
    public BookingDomain getBookingById(long bookingId) {
        log.debug("Getting booking by ID: {}", bookingId);
        return executeDatabase(() -> 
            serviceMapper.bookingToBookingDomain(bookingRepository.getBookingById(bookingId))
        );
    }

    @Override
    public List<BookingDomain> getAllBookings() {
        log.debug("Getting all bookings");
        return executeDatabase(() -> 
            serviceMapper.ListBookingToBookingDomain(bookingRepository.findAll())
        );
    }

    @Override
    public List<BookingDomain> getBookingsByUserId(Long userId) {
        log.debug("Getting bookings for user ID: {}", userId);
        return executeDatabase(() -> 
            serviceMapper.ListBookingToBookingDomain(bookingRepository.findByUserId(userId))
        );
    }

    @Override
    public List<BookingDomain> getBookingsByRoomId(Long roomId) {
        log.debug("Getting bookings for room ID: {}", roomId);
        return executeDatabase(() -> 
            serviceMapper.ListBookingToBookingDomain(bookingRepository.findByRoomIdId(roomId))
        );
    }

    @Override
    public BookingDomain addBooking(BookingDomain bookingDomain) {
        log.info("Adding new booking for pet: {}", bookingDomain.getPetName());
        
        return executeDatabase(() -> {
            var bookingEntity = serviceMapper.bookingDomainToBooking(bookingDomain);
            
            // Fetch the actual room entity by ID
            if (bookingEntity.getRoomId() != null && bookingEntity.getRoomId().getId() != null) {
                var roomEntity = roomRepository.getRoomById(bookingEntity.getRoomId().getId());
                if (roomEntity == null) {
                    throw new IllegalArgumentException("Room with ID " + bookingEntity.getRoomId().getId() + " not found");
                }
                bookingEntity.setRoomId(roomEntity);
            } else {
                throw new IllegalArgumentException("Room ID is required for booking");
            }
            
            // Set status automatically to PENDING
            bookingEntity.setStatus(BookingStatus.PENDING);
            
            var savedBooking = bookingRepository.save(bookingEntity);
            log.info("Booking created with ID: {}", savedBooking.getId());
            return serviceMapper.bookingToBookingDomain(savedBooking);
        });
    }

    @Override
    public BookingDomain updateBooking(Long bookingId, BookingDomain bookingDomain) {
        log.info("Updating booking ID: {}", bookingId);
        
        return executeDatabase(() -> {
            var existingBooking = bookingRepository.getBookingById(bookingId);
            if (existingBooking == null) {
                log.warn("Booking not found with ID: {}", bookingId);
                return null;
            }
            
            var bookingEntity = serviceMapper.bookingDomainToBooking(bookingDomain);
            
            // Update room if room ID is provided
            if (bookingEntity.getRoomId() != null && bookingEntity.getRoomId().getId() != null) {
                var roomEntity = roomRepository.getRoomById(bookingEntity.getRoomId().getId());
                if (roomEntity == null) {
                    throw new IllegalArgumentException("Room with ID " + bookingEntity.getRoomId().getId() + " not found");
                }
                existingBooking.setRoomId(roomEntity);
            }
            
            existingBooking.setPetName(bookingEntity.getPetName());
            existingBooking.setPetSpecies(bookingEntity.getPetSpecies());
            existingBooking.setPetBreed(bookingEntity.getPetBreed());
            existingBooking.setCheckInDate(bookingEntity.getCheckInDate());
            existingBooking.setCheckOutDate(bookingEntity.getCheckOutDate());
            
            // Only update status if provided
            if (bookingEntity.getStatus() != null) {
                existingBooking.setStatus(bookingEntity.getStatus());
            }
            
            var updatedBooking = bookingRepository.save(existingBooking);
            log.info("Booking updated successfully: {}", bookingId);
            return serviceMapper.bookingToBookingDomain(updatedBooking);
        });
    }

    @Override
    public void cancelBooking(Long bookingId) {
        log.info("Cancelling booking ID: {}", bookingId);
        
        executeDatabase(() -> {
            var existingBooking = bookingRepository.getBookingById(bookingId);
            if (existingBooking != null) {
                existingBooking.setStatus(BookingStatus.CANCELLED);
                bookingRepository.save(existingBooking);
                log.info("Booking cancelled: {}", bookingId);
            } else {
                log.warn("Booking not found for cancellation: {}", bookingId);
            }
            return null;
        });
    }

    @Override
    public boolean checkRoomAvailability(Long roomId, LocalDateTime checkIn, LocalDateTime checkOut) {
        log.debug("Checking room availability: roomId={}, checkIn={}, checkOut={}", roomId, checkIn, checkOut);
        
        return executeDatabase(() -> {
            var overlappingBookings = bookingRepository.findOverlappingBookings(roomId, checkIn, checkOut);
            boolean isAvailable = overlappingBookings.isEmpty();
            log.debug("Room {} availability: {}", roomId, isAvailable);
            return isAvailable;
        });
    }
}
