package com.hms.hotelmanagementsystem.utilities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Pair {

    public Integer bookingCount,hotelId;

    @JsonCreator
    public Pair(@JsonProperty("bookingCount") Integer first, @JsonProperty("hotelId") Integer second) {
        this.bookingCount = first;
        this.hotelId = second;
    }



}
