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

@RestController
public class HotelController {

    @Autowired
    private HotelService hotelService;


    @RequestMapping(value = "/createHotel", method = RequestMethod.POST)
    public void addHotel(@RequestBody Hotel hotel){
        hotelService.createHotel(hotel);
    }

    @RequestMapping(value = "/getHotelByIds" , method =  RequestMethod.GET)
    public List<Hotel> searchHotel(@RequestParam("hotelIds")List<Integer> hotelIds){
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

    @RequestMapping(value = "/deleteHotel" , method = RequestMethod.GET)
    public void deleteHotelById(@RequestParam("hotelId") int hotelId){
        hotelService.deleteByHotelId(hotelId);
    }

    @RequestMapping(value = "/updateHotel" , method = RequestMethod.POST)
    public void updateHotelById(@RequestBody Hotel hotel) {
        hotelService.updateHotel(hotel);
    }

    @RequestMapping(value = "/getHotelByHotelName" , method = RequestMethod.GET)
    public List<Hotel> getHotelByHotelName(@RequestParam("hotelName") String hotelName){
        List<Hotel> hotels = hotelService.getHotelByName(hotelName);
        return hotels;
    }

}
