package org.tnc.pethotelmicroservice.service.serviceMapper;

import org.mapstruct.Mapper;
import org.tnc.pethotelmicroservice.repository.entities.Booking;
import org.tnc.pethotelmicroservice.repository.entities.Room;
import org.tnc.pethotelmicroservice.service.domain.BookingDomain;
import org.tnc.pethotelmicroservice.service.domain.RoomDomain;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ServiceMapper {
    BookingDomain bookingToBookingDomain(Booking booking);
    Booking bookingDomainToBooking(BookingDomain bookingDomain);
    RoomDomain roomToRoomDomain(Room room);
    Room roomDomainToRoom(RoomDomain roomDomain);

    List<BookingDomain> ListBookingToBookingDomain(List<Booking> bookingList);
    List<Booking> ListBookingDomainToBooking(List<BookingDomain> bookingDomainList);
    List<RoomDomain> ListRoomToRoomDomain(List<Room> roomList);
    List<Room> ListRoomDomainToRoom(List<RoomDomain> roomDomainList);
}
