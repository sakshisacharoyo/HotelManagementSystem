package com.hms.hotelmanagementsystem.entities;

import io.searchbox.annotations.JestId;

import java.time.LocalDate;
import java.util.Date;

public class Booking {

    @JestId
    private String bookingId;

    private Integer hotelId;
    private Integer userId;
    private String checkOutDate;
    private Double amount;
    private String dateOfBooking;
    private Status  bookingStatus;
    private String checkInDate;


    public String getDateOfBooking() {

        return dateOfBooking;
    }

    public void setDateOfBooking(String dateOfBooking) {
        this.dateOfBooking = dateOfBooking;
    }

    public enum  Status{

        Active , Inactive ;
    }


    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }


    public Status getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(Status bookingStatus) {
        this.bookingStatus = bookingStatus;
    }


    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getHotelId() {
        return hotelId;
    }

    public void setHotelId(Integer hotelId) {
        this.hotelId = hotelId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }


    @Override
    public String toString() {
        return "Booking{" +
                "bookingId='" + bookingId + '\'' +
                ", hotelId=" + hotelId +
                ", userId=" + userId +
                ", bookingStatus=" + bookingStatus +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", amount=" + amount +
                '}';
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }


}
