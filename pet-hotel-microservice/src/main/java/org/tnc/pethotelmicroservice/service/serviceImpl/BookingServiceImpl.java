package org.tnc.pethotelmicroservice.service.serviceImpl;

import com.tnc.resilience.util.ResilienceExecutor;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tnc.pethotelmicroservice.events.BookingEventPublisher;
import org.tnc.pethotelmicroservice.repository.entities.Booking;
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
    private final BookingEventPublisher bookingEventPublisher;

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
    @Transactional
    public BookingDomain addBooking(BookingDomain bookingDomain) {
        log.info("Adding new booking for pet: {}", bookingDomain.getPetName());
        
        BookingDomain savedBookingDomain = executeDatabase(() -> {
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
        
        // Publish booking created event after successful DB commit
        if (savedBookingDomain != null) {
            Booking savedBooking = bookingRepository.getBookingById(savedBookingDomain.getId());
            if (savedBooking != null) {
                bookingEventPublisher.publishBookingCreated(savedBooking);
            }
        }
        
        return savedBookingDomain;
    }

    @Override
    @Transactional
    public BookingDomain updateBooking(Long bookingId, BookingDomain bookingDomain) {
        log.info("Updating booking ID: {}", bookingId);
        
        // Get old status before update
        Booking existingBookingBefore = bookingRepository.getBookingById(bookingId);
        BookingStatus oldStatus = existingBookingBefore != null ? existingBookingBefore.getStatus() : null;
        
        BookingDomain updatedBookingDomain = executeDatabase(() -> {
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
        
        // Publish appropriate event after successful DB commit based on status change
        if (updatedBookingDomain != null) {
            Booking updatedBooking = bookingRepository.getBookingById(bookingId);
            if (updatedBooking != null) {
                BookingStatus newStatus = updatedBooking.getStatus();
                
                // Publish specific event if status changed to a lifecycle state
                if (newStatus != oldStatus && newStatus != null) {
                    switch (newStatus) {
                        case CONFIRMED:
                            bookingEventPublisher.publishBookingConfirmed(updatedBooking);
                            break;
                        case COMPLETED:
                            bookingEventPublisher.publishBookingCompleted(updatedBooking);
                            break;
                        case CANCELLED:
                            bookingEventPublisher.publishBookingCancelled(updatedBooking);
                            break;
                        default:
                            // For other status changes or non-status updates, publish generic update event
                            bookingEventPublisher.publishBookingUpdated(updatedBooking);
                            break;
                    }
                } else {
                    // No status change, publish generic update event
                    bookingEventPublisher.publishBookingUpdated(updatedBooking);
                }
            }
        }
        
        return updatedBookingDomain;
    }

    @Override
    @Transactional
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
        
        // Publish booking cancelled event after successful DB commit
        Booking cancelledBooking = bookingRepository.getBookingById(bookingId);
        if (cancelledBooking != null) {
            bookingEventPublisher.publishBookingCancelled(cancelledBooking);
        }
    }

    @Override
    @Transactional
    public BookingDomain confirmBooking(Long bookingId) {
        log.info("Confirming booking ID: {}", bookingId);
        
        BookingDomain confirmedBookingDomain = executeDatabase(() -> {
            var existingBooking = bookingRepository.getBookingById(bookingId);
            if (existingBooking == null) {
                log.warn("Booking not found with ID: {}", bookingId);
                return null;
            }
            
            existingBooking.setStatus(BookingStatus.CONFIRMED);
            var confirmedBooking = bookingRepository.save(existingBooking);
            log.info("Booking confirmed: {}", bookingId);
            return serviceMapper.bookingToBookingDomain(confirmedBooking);
        });
        
        // Publish booking confirmed event after successful DB commit
        if (confirmedBookingDomain != null) {
            Booking confirmedBooking = bookingRepository.getBookingById(bookingId);
            if (confirmedBooking != null) {
                bookingEventPublisher.publishBookingConfirmed(confirmedBooking);
            }
        }
        
        return confirmedBookingDomain;
    }

    @Override
    @Transactional
    public BookingDomain completeBooking(Long bookingId) {
        log.info("Completing booking ID: {}", bookingId);
        
        BookingDomain completedBookingDomain = executeDatabase(() -> {
            var existingBooking = bookingRepository.getBookingById(bookingId);
            if (existingBooking == null) {
                log.warn("Booking not found with ID: {}", bookingId);
                return null;
            }
            
            existingBooking.setStatus(BookingStatus.COMPLETED);
            var completedBooking = bookingRepository.save(existingBooking);
            log.info("Booking completed: {}", bookingId);
            return serviceMapper.bookingToBookingDomain(completedBooking);
        });
        
        // Publish booking completed event after successful DB commit
        if (completedBookingDomain != null) {
            Booking completedBooking = bookingRepository.getBookingById(bookingId);
            if (completedBooking != null) {
                bookingEventPublisher.publishBookingCompleted(completedBooking);
            }
        }
        
        return completedBookingDomain;
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
