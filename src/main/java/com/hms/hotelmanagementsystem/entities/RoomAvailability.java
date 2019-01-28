package com.hms.hotelmanagementsystem.entities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.searchbox.annotations.JestId;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomAvailability {


    private Integer hotelId;
    private Integer availableRooms;
    private String date;


    public Integer getHotelId() {
        return hotelId;
    }

    public void setHotelId(Integer hotelId) {
        this.hotelId = hotelId;
    }

    public Integer getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(Integer availableRooms) {
        this.availableRooms = availableRooms;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }



}
