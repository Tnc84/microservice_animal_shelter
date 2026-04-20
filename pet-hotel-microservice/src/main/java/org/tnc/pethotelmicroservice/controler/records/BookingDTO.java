package org.tnc.pethotelmicroservice.controler.records;

import org.tnc.pethotelmicroservice.repository.entities.BookingStatus;

public record BookingDTO(
         RoomDTO roomId,
         String petName,
         String petSpecies,
         String petBreed,
         BookingStatus status) {
}
