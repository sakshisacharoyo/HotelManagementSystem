package com.hms.hotelmanagementsystem.Controller;

import com.hms.hotelmanagementsystem.entities.Booking;
import com.hms.hotelmanagementsystem.entities.RoomAvailability;
import com.hms.hotelmanagementsystem.handler.BookingHandler;
import com.hms.hotelmanagementsystem.service.BookingService;
import com.hms.hotelmanagementsystem.utilities.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class BookingController {

    @Autowired
    BookingService bookingService;

    @Autowired
    BookingHandler bookingHandler;



    @RequestMapping(value = "/getAllBookings" , method =  RequestMethod.GET)
    public List<Booking> getAllBookings(){

        List<Booking> bookingList= bookingService.getAllBookings();
        return  bookingList;
    }

    @RequestMapping(value = "/addBooking" , method = RequestMethod.POST)
    public String bookRoom(@RequestBody Booking booking){

        if(booking.getUserId() != null && booking.getCheckInDate() != null && booking.getCheckOutDate() != null && booking.getHotelId()!= null){
            String res = bookingHandler.addBooking(booking);
            System.out.println("booking result " + res);
            return res;
        }
        else{
            return "Incomplete information ";
        }

    }

    @RequestMapping(value = "/cancelBooking" , method = RequestMethod.POST)
    public String cancelBooking(@RequestParam("bookingId") String bookingId){
        String res = bookingHandler.cancelBooking(bookingId);
        return res;
    }

    @RequestMapping(value="/getBookingById" , method = RequestMethod.GET)
    public Booking getBookingById(@RequestParam("bookingId") String bookingId){
        Booking booking = bookingService.getBookingById(bookingId);
        return booking;
    }

    @RequestMapping(value = "/getAllBookingByDate", method = RequestMethod.GET)
    public List<Booking> getAllBookingByDate(@RequestParam("date")String getdate){
        List<Booking> bookings = bookingService.getBookingsByDate(getdate);
        return bookings;
    }

    @RequestMapping(value = "/getAllBookingsByDateAndHotelId" , method = RequestMethod.GET)
    public List<Booking> getAllBookingsByDateAndHotelId(@RequestParam("hotelId") Integer hotelId , @RequestParam("date") String date){
        List<Booking> bookings = bookingService.getBookingByDateAndHotelId(hotelId,date);
        return bookings;
    }

    @RequestMapping(value = "/getAllBookingsByUserIdAndDate" , method = RequestMethod.GET)
    public List<Booking> getAllBookingsByUserIdAndDate(@RequestParam("userId") int userId , @RequestParam("date") String date){
        List<Booking> bookings = bookingService.getBookingByUserIdAndDate(userId,date);
        return bookings;
    }

    @RequestMapping(value = "/getAvailabilityByDateAndHotelId" , method = RequestMethod.GET)
    public Integer getAvailabilityByDateAndHotelId(@RequestParam("date") String date , @RequestParam("hotelId") Integer hotelId){
        Integer  availability= bookingService.getAvailabilityByDateAndHotelId(date,hotelId);
        return  availability;
    }

    @RequestMapping(value = "/getTrendingHotelMap" , method = RequestMethod.GET)
    public Map<Integer, ArrayList<Pair>> gettrending(){
       return bookingService.getTrendingHotels();
    }

    @RequestMapping(value = "/getTrendingHotelsByCityId" , method = RequestMethod.GET)
    public Map<Integer,ArrayList<Pair>> gettrendingHotels(@RequestParam("cityId") Integer cityId){
        return bookingService.getAllTrendingHotelsByCityIdFromCache(cityId);

    }

    @RequestMapping(value = "/getAllTrendingHotels" , method = RequestMethod.GET)
    public Map<Integer, ArrayList<Pair>>  check(){
        return bookingService.getAllTrendingHotelsFromCache();
    }


}
