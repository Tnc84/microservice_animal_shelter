package org.tnc.pethotelmicroservice.service.serviceInterfaces;

import org.tnc.pethotelmicroservice.service.domain.BookingDomain;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingServiceInterface {
    BookingDomain getBookingById(long bookingId);
    List<BookingDomain> getAllBookings();
    List<BookingDomain> getBookingsByUserId(Long userId);
    List<BookingDomain> getBookingsByRoomId(Long roomId);
    BookingDomain addBooking(BookingDomain booking);
    BookingDomain updateBooking(Long bookingId, BookingDomain booking);
    void cancelBooking(Long bookingId);
    BookingDomain confirmBooking(Long bookingId);
    BookingDomain completeBooking(Long bookingId);
    boolean checkRoomAvailability(Long roomId, LocalDateTime checkIn, LocalDateTime checkOut);
}
