package org.tnc.pethotelmicroservice.service.serviceMapper;

import org.mapstruct.Mapper;
import org.tnc.pethotelmicroservice.repository.entities.Booking;
import org.tnc.pethotelmicroservice.repository.entities.Room;
import org.tnc.pethotelmicroservice.service.domain.BookingDomain;
import org.tnc.pethotelmicroservice.service.domain.RoomDomain;

@Mapper(componentModel = "spring")
public interface ServiceMapper {
    BookingDomain bookingToBookingDomain(Booking booking);
    Booking bookingDomainToBooking(Booking booking);
    RoomDomain roomToRoomDomain(Room room);
    Room roomDomainToRoom(RoomDomain room);
}
