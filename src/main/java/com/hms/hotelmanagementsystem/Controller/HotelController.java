package com.hms.hotelmanagementsystem.Controller;

import com.google.gson.JsonObject;
import com.hms.hotelmanagementsystem.entities.Booking;
import com.hms.hotelmanagementsystem.entities.Hotel;
import com.hms.hotelmanagementsystem.service.BookingService;
import com.hms.hotelmanagementsystem.service.HotelService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @Autowired
    private BookingService bookingService;

    @RequestMapping(value = "/createHotel", method = RequestMethod.POST)
    public String addHotel(@RequestBody Hotel hotel){
        return hotelService.createHotel(hotel);
    }

    @RequestMapping(value = "/getHotelByIds" , method =  RequestMethod.GET)
    public List<Hotel> searchHotel(@RequestParam("hotelIds") Set<Integer> hotelIds){
       List<Hotel> searchedHotels =  hotelService.searchHotel(hotelIds);
        return searchedHotels;
    }

    @RequestMapping(value = "/getHotelById" , method = RequestMethod.GET)
    public Hotel searchHotelById(@RequestParam("hotelId") Integer hotelId){
        Hotel searchedHotel = hotelService.getHotelById(hotelId);
        return searchedHotel;
    }

    @RequestMapping(value =  "/searchByCityIdAndStateId" , method = RequestMethod.GET)
    public List<Hotel> searchHotelByCityIdAndStateId(@RequestParam("cityId") int cityId , @RequestParam("stateId") int stateId){
        List<Hotel>  searchedHotels = hotelService.searchHotelByCityIdAndStateId(cityId, stateId);
        return searchedHotels;
    }

    @RequestMapping(value = "/searchByCityNameAndStateName" , method = RequestMethod.GET)
    public List<Hotel> searchByCityNameAndStateName(@RequestParam("cityName") String cityName , @RequestParam("stateName") String stateName){
        List<Hotel> searchedHotels = hotelService.searchByCityNameAndStateName(cityName, stateName);
        return searchedHotels;
    }

    @RequestMapping(value = "/searchByState" , method = RequestMethod.GET)
    public List<Hotel> searchByStateName(@RequestParam("stateName") String stateName){
        List<Hotel> searchedHotels = hotelService.searchByState(stateName);
        return searchedHotels;
    }

    @RequestMapping(value = "/searchByStateId" , method = RequestMethod.GET)
    public List<Hotel> searchByStateId(@RequestParam("stateId") Integer stateId){
        List<Hotel> searchedHotels = hotelService.searchByStateId(stateId);
        return searchedHotels;
    }

    @RequestMapping(value = "/searchByCityId" , method = RequestMethod.GET)
    public List<Hotel> searchByCityId(@RequestParam("cityId") Integer stateId){
        List<Hotel> searchedHotels = hotelService.searchByCityId(stateId);
        return searchedHotels;
    }

    @RequestMapping(value = "/deleteHotel" , method = RequestMethod.POST)
    public String deleteHotelById(@RequestParam("hotelId") int hotelId){
       return  hotelService.deleteByHotelId(hotelId);
    }


    @RequestMapping(value = "/updateHotel" , method = RequestMethod.POST)
    public String updateHotelById(@RequestBody Hotel hotel , @RequestParam("hotelId") Integer hotelId ) {
        hotel.setHotelId(hotelId);
        Hotel oldhotel = hotelService.getHotelById(hotelId);

            if(oldhotel != null && oldhotel.getAvailableRooms() != null && hotel.getAvailableRooms()!=null){

            int diff = hotel.getAvailableRooms() - oldhotel.getAvailableRooms();
            bookingService.updateRoomAvailabilityOnHotelUpdate(hotelId , diff);
        }

        return hotelService.updateHotel(hotel);
    }

    @RequestMapping(value = "/getHotelByHotelName" , method = RequestMethod.GET)
    public List<Hotel> getHotelByHotelName(@RequestParam("hotelName") String hotelName){
        List<Hotel> hotels = hotelService.getHotelByName(hotelName);
        return hotels;
    }



}
