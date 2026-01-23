package org.tnc.pethotelmicroservice.service.serviceImpl;

import jakarta.servlet.ServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tnc.pethotelmicroservice.repository.interfaces.BookingRepository;
import org.tnc.pethotelmicroservice.service.domain.BookingDomain;
import org.tnc.pethotelmicroservice.service.serviceInterfaces.BookingServiceInterface;
import org.tnc.pethotelmicroservice.service.serviceMapper.ServiceMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingServiceInterface {

    private final BookingRepository bookingRepository;
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
    public BookingDomain addBooking(BookingDomain bookingDomain) {
        var savedBooking = bookingRepository.save(serviceMapper.bookingDomainToBooking(bookingDomain));
        return serviceMapper.bookingToBookingDomain(savedBooking);
    }

    @Override
    public BookingDomain updateBooking(BookingDomain booking) {
        return null;
    }

}
