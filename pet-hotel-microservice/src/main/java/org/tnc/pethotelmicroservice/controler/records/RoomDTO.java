package org.tnc.pethotelmicroservice.controler.records;

public record RoomDTO(int capacity,
                      String roomType,
                      boolean isAvailable,
                      String roomDescription) {

}
