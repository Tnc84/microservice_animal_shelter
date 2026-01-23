package org.tnc.pethotelmicroservice.service.serviceInterfaces;

import org.tnc.pethotelmicroservice.service.domain.BookingDomain;

import java.util.List;

public interface BookingServiceInterface {
    BookingDomain getBookingById(long bookingId);
    List<BookingDomain> getAllBookings();
    BookingDomain addBooking(BookingDomain booking);
    BookingDomain updateBooking(BookingDomain booking);
}
