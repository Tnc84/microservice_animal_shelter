package org.tnc.pethotelmicroservice.controler.records;

public record RoomDTO(Long id,
                      int capacity,
                      String roomType,
                      boolean isAvailable,
                      String roomDescription) {

}
