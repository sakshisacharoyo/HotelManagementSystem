package com.hms.hotelmanagementsystem.handler;

import com.hms.hotelmanagementsystem.entities.Booking;
import com.hms.hotelmanagementsystem.entities.Hotel;
import com.hms.hotelmanagementsystem.entities.User;
import com.hms.hotelmanagementsystem.service.BookingService;
import com.hms.hotelmanagementsystem.service.HotelService;
import com.hms.hotelmanagementsystem.service.UserService;
import io.searchbox.core.Index;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Component
public class BookingHandler {

    @Autowired
    HotelService hotelService;

    @Autowired
    BookingService bookingService;



    public String addBooking(Booking booking) {

        String res = "";
        Hotel hotel = new Hotel();
        hotel = hotelService.getHotelById(booking.getHotelId());

        if (hotel != null) {
            Boolean availability = bookingService.checkRoomAvailability(booking.getHotelId(),booking.getCheckInDate(),booking.getCheckOutDate());
            if(hotel.getAvailableRooms() != null && hotel.getPrice() != null){
                if (hotel.getAvailableRooms() > 0 && availability) {

                    bookingService.updateRoomAvailability(booking.getHotelId(),booking.getCheckInDate(),booking.getCheckOutDate());

                    int price = hotel.getPrice();
                    LocalDate checkIn = LocalDate.parse(booking.getCheckInDate());
                    LocalDate checkOut = LocalDate.parse(booking.getCheckOutDate());
                    long noOfDaysBetween = ChronoUnit.DAYS.between(checkIn, checkOut);
                    Double amount = new Double(price * noOfDaysBetween);
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                    LocalDateTime now = LocalDateTime.now();
                    String bookingId = "oyo_" + dtf.format(now) + booking.getUserId().toString();
                    booking.setBookingId(bookingId);
                    booking.setAmount(amount);
                    booking.setBookingStatus(Booking.Status.Active);

                    LocalDate currentDate = LocalDate.now();
                    booking.setDateOfBooking(currentDate.toString());

                    res = bookingService.addBooking(booking);
                }
                else{
                        return "No rooms Available in this hotel";
                    }
                }
                else{
                    return "No rooms Available in this hotel";
                }
            }else {
            res = "hotel not found !!";
        }

        return res;
    }

    public String cancelBooking(String bookingId){

        String res = "";
        Booking booking = bookingService.getBookingById(bookingId);
        if(booking != null){
            int hotelId = booking.getHotelId();
            Hotel hotel = hotelService.getHotelById(hotelId);
            if(hotel != null){
                if(booking.getBookingStatus() != Booking.Status.Inactive) {
                    res = bookingService.updateRoomAvailabilityOnCancellation(booking);
                    res += bookingService.cancelBooking(bookingId);
                }
                else{
                    res = "booking already cancelled , No such booking exists !!";
                }
            }
            else{
                return "Hotel not found !!";
            }
        }

        return res;
    }

}
