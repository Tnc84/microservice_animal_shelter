package org.tnc.pethotelmicroservice.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tnc.pethotelmicroservice.repository.entities.BookingStatus;
import org.tnc.pethotelmicroservice.repository.interfaces.BookingRepository;
import org.tnc.pethotelmicroservice.repository.interfaces.RoomRepository;
import org.tnc.pethotelmicroservice.service.domain.BookingDomain;
import org.tnc.pethotelmicroservice.service.serviceInterfaces.BookingServiceInterface;
import org.tnc.pethotelmicroservice.service.serviceMapper.ServiceMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingServiceInterface {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final ServiceMapper serviceMapper;

    @Override
    public BookingDomain getBookingById(long bookingId) {
        return serviceMapper.bookingToBookingDomain(bookingRepository.getBookingById(bookingId));
    }

    @Override
    public List<BookingDomain> getAllBookings() {
        return serviceMapper.ListBookingToBookingDomain(bookingRepository.findAll());
    }

    @Override
    public List<BookingDomain> getBookingsByUserId(Long userId) {
        return serviceMapper.ListBookingToBookingDomain(bookingRepository.findByUserId(userId));
    }

    @Override
    public List<BookingDomain> getBookingsByRoomId(Long roomId) {
        return serviceMapper.ListBookingToBookingDomain(bookingRepository.findByRoomIdId(roomId));
    }

    @Override
    public BookingDomain addBooking(BookingDomain bookingDomain) {
        var bookingEntity = serviceMapper.bookingDomainToBooking(bookingDomain);
        
        // Fetch the actual room entity by ID (since DTO only contains room ID)
        if (bookingEntity.getRoomId() != null && bookingEntity.getRoomId().getId() != null) {
            var roomEntity = roomRepository.getRoomById(bookingEntity.getRoomId().getId());
            if (roomEntity == null) {
                throw new IllegalArgumentException("Room with ID " + bookingEntity.getRoomId().getId() + " not found");
            }
            bookingEntity.setRoomId(roomEntity);
        } else {
            throw new IllegalArgumentException("Room ID is required for booking");
        }
        
        // Set status automatically to PENDING - ignore what client sent
        bookingEntity.setStatus(BookingStatus.PENDING);
        
        var savedBooking = bookingRepository.save(bookingEntity);
        return serviceMapper.bookingToBookingDomain(savedBooking);
    }

    @Override
    public BookingDomain updateBooking(Long bookingId, BookingDomain bookingDomain) {
        var existingBooking = bookingRepository.getBookingById(bookingId);
        if (existingBooking == null) {
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
        
        // Only update status if provided (don't allow setting to invalid states)
        if (bookingEntity.getStatus() != null) {
            existingBooking.setStatus(bookingEntity.getStatus());
        }
        
        var updatedBooking = bookingRepository.save(existingBooking);
        return serviceMapper.bookingToBookingDomain(updatedBooking);
    }

    @Override
    public void cancelBooking(Long bookingId) {
        var existingBooking = bookingRepository.getBookingById(bookingId);
        if (existingBooking != null) {
            existingBooking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(existingBooking);
        }
    }

    @Override
    public boolean checkRoomAvailability(Long roomId, LocalDateTime checkIn, LocalDateTime checkOut) {
        var overlappingBookings = bookingRepository.findOverlappingBookings(roomId, checkIn, checkOut);
        return overlappingBookings.isEmpty();
    }
}
