package org.tnc.pethotelmicroservice;


import java.math.BigInteger;
import java.util.Date;

public class Room {
    private BigInteger id;
    private int capacity;
    private String roomType;
    private boolean isAvailable;
    private String roomDescription;
    private Date startDate;
    private Date updated;
    private Date endDate;
    // Indexes: `idx_room_number`, `idx_room_available`
}
