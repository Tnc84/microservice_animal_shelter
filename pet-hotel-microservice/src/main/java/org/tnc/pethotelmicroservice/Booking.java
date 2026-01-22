package org.tnc.pethotelmicroservice;

import java.math.BigInteger;
import java.util.Date;

public class Booking {
    private BigInteger id;
    private User userId;
    private Room roomId;
    private String petName;
    private String petSpecies;
    private String petBreed;
    private Date checkInDate;
    private Date checkOutDate;
    private int totalDays;
    BookingStatus status;
    private Date startDate;
    private Date updatetDate;
    private Date endDate;

}
