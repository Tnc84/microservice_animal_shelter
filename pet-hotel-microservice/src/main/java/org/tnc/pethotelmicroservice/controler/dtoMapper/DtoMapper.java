package org.tnc.pethotelmicroservice.controler.dtoMapper;

import org.mapstruct.Mapper;
import org.tnc.pethotelmicroservice.controler.records.BookingDTO;
import org.tnc.pethotelmicroservice.controler.records.RoomDTO;
import org.tnc.pethotelmicroservice.service.domain.BookingDomain;
import org.tnc.pethotelmicroservice.service.domain.RoomDomain;

@Mapper(componentModel = "spring")
public interface DtoMapper {
    BookingDomain bookingDtoToBookingDomain(BookingDTO bookingDTO);
    BookingDTO bookingDomainToBookingDto(BookingDomain bookingDomain);
    RoomDomain roomDtoToRoomDomain(RoomDTO roomDTO);
    RoomDTO roomDomainToRoomDto(RoomDomain roomDomain);
}
