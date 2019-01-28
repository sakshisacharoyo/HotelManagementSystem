package com.hms.hotelmanagementsystem.service;

import com.hms.hotelmanagementsystem.entities.Booking;
import com.hms.hotelmanagementsystem.entities.Hotel;
import com.hms.hotelmanagementsystem.entities.RoomAvailability;
import com.hms.hotelmanagementsystem.entities.User;
import com.hms.hotelmanagementsystem.utilities.JestConnectivity;
import com.hms.hotelmanagementsystem.utilities.Pair;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
public class BookingService {


    @Autowired
    HotelService hotelService;

    @Autowired
    RedisService redisService;

    @Autowired
    CityService cityService;

    @Autowired
    UserService userService;

    @Autowired
    JestConnectivity jestConnector;

    @Qualifier("jestClient")
    @Autowired
    JestClient client;




    public List<Booking> getAllBookings(){

        List<Booking> bookingList = new ArrayList<Booking>();
        try{

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            QueryBuilder matchQuery = QueryBuilders.matchAllQuery();
            searchSourceBuilder.query(matchQuery);
            Search search = new Search.Builder(searchSourceBuilder.size(1000).toString()).addIndex("booking").addType("doc").build();
            SearchResult result = client.execute(search);
            bookingList = result.getSourceAsObjectList(Booking.class,false);

        }catch(IOException ex){
            System.out.println("Exception in getting all bookings" + ex);
        }
        return bookingList;
    }



    public Boolean checkRoomAvailability(Integer hotelId, String checkInDate, String checkOutDate) {

        Boolean available = true;
        LocalDate startDate = LocalDate.parse(checkInDate);
        LocalDate endDate = LocalDate.parse(checkOutDate);
        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            try {



                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

                QueryBuilder matchQuery = QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("hotelId", hotelId))
                        .must(QueryBuilders.matchQuery("date", date));

                searchSourceBuilder.query(matchQuery);

                Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("roomavailability").addType("doc").build();

                SearchResult result = client.execute(search);

                RoomAvailability roomAvailability = result.getSourceAsObject(RoomAvailability.class, false);
                if (roomAvailability != null) {
                    Integer availableRooms = roomAvailability.getAvailableRooms();
                    if (availableRooms <= 0) {
                        available = false;
                    }
                }

            } catch (IOException ex) {
                System.out.println("Exception in check availability " + ex);
            }
        }

        return available;
    }

    public String addRoomAvailability(RoomAvailability roomAvailability) {

        String res = "";
        try {



            Index index = new Index.Builder(roomAvailability).index("roomavailability").type("doc").id(roomAvailability.getHotelId().toString() + roomAvailability.getDate().toString()).build();
            client.execute(index);
            res = "Room availability added successfully !!  ";
        } catch (IOException ex) {
            System.out.println(" Error in adding room availability " + ex);
        }
        return res;

    }



    public void updateRoomAvailabilityOnHotelUpdate(Integer hotelId , Integer difference){



        List<RoomAvailability> dateWiseAvailability = new ArrayList<RoomAvailability>();
        try{
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            QueryBuilder matchquery = QueryBuilders.matchQuery("hotelId",hotelId);
            searchSourceBuilder.query(matchquery);
            Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("roomavailability").addType("doc").build();
            SearchResult result = client.execute(search);
            dateWiseAvailability =  result.getSourceAsObjectList(RoomAvailability.class, false);

            for(int i = 0; i<dateWiseAvailability.size();i++){

                RoomAvailability roomAvailability = dateWiseAvailability.get(i);
                int value = roomAvailability.getAvailableRooms();
                value += difference;
                roomAvailability.setAvailableRooms(value);
                if(value<0)
                    roomAvailability.setAvailableRooms(0);
                addRoomAvailability(roomAvailability);
            }

        }catch(IOException ex){
            System.out.println("Exception in room update on hotel update" + ex);
        }
    }


    public String updateRoomAvailability(Integer hotelId, String checkInDate, String checkOutDate) {

        String res = "";
        LocalDate startDate = LocalDate.parse(checkInDate);
        LocalDate endDate = LocalDate.parse(checkOutDate);
        Hotel hotel = hotelService.getHotelById(hotelId);
        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            try {



                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

                QueryBuilder matchQuery = QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("hotelId", hotelId))
                        .must(QueryBuilders.matchQuery("date", date));

                searchSourceBuilder.query(matchQuery);

                Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("roomavailability").addType("doc").build();

                SearchResult result = client.execute(search);

                RoomAvailability roomAvailability = result.getSourceAsObject(RoomAvailability.class, false);
                if (roomAvailability != null) {
                    int value = roomAvailability.getAvailableRooms();
                    value--;
                    roomAvailability.setAvailableRooms(value);
                    res = addRoomAvailability(roomAvailability);
                } else {

                    RoomAvailability roomAvailability1 = new RoomAvailability();

                    roomAvailability1.setHotelId(hotelId);
                    roomAvailability1.setAvailableRooms(hotel.getAvailableRooms() - 1);
                    roomAvailability1.setDate(date.toString());

                    res = addRoomAvailability(roomAvailability1);
                }


            } catch (IOException ex) {
                System.out.println(" Exception in check availability " + ex);
            }
        }
        return res;
    }

     public String addBooking(Booking booking) {

        String res = "";

            try {

                Index index = new Index.Builder(booking).index("booking").type("doc").build();
                client.execute(index);
                res = " Room booked successfully !!  ";
                res = booking.getBookingId();

            } catch (IOException ex) {
                System.out.println("Error in booking " + ex);
            }



        return res;

    }

     public Booking getBookingById(String bookingId) {

        Booking booking = new Booking();
        try {


            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            searchSourceBuilder.query(QueryBuilders.matchQuery("bookingId", bookingId));
            Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("booking").addType("doc").build();

            SearchResult result = client.execute(search);

            booking = result.getSourceAsObject(Booking.class, false);

        } catch (IOException ex) {
            System.out.println("  exception in fetching booking " + ex);
        }
        return booking;
    }

    public String cancelBooking(String bookingId) {

        String res = "";
        try {


            Booking booking = getBookingById(bookingId);
            booking.setBookingStatus(Booking.Status.Inactive);
            Index index = new Index.Builder(booking).index("booking").type("doc").build();
            client.execute(index);
            res = " Booking cancelled successfully !! ";

        } catch (IOException ex) {
            res = " Exception in booking";
            System.out.println(" Exception in booking " + ex);
        }
        return res;
    }

    public List<Booking> getBookingsByDate(String date) {

        List<Booking> bookings = new ArrayList<Booking>();
        try {


            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            QueryBuilder startQuery = QueryBuilders.rangeQuery("checkInDate").lte(date);
            QueryBuilder endQuery = QueryBuilders.rangeQuery("checkOutDate").gt(date);
            QueryBuilder checkQuery = QueryBuilders.matchQuery("bookingStatus", Booking.Status.Active);
            QueryBuilder matchQuery = QueryBuilders.boolQuery().must(startQuery).must(endQuery).must(checkQuery);

            searchSourceBuilder.query(matchQuery);

            Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("booking").addType("doc").build();
            SearchResult result = client.execute(search);

            bookings = result.getSourceAsObjectList(Booking.class, false);

        } catch (IOException ex) {
            System.out.println(" exception in fetching data " + ex);
        }
        return bookings;
    }

    public List<Booking> getBookingByDateAndHotelId(Integer hotelId, String date) {

        List<Booking> bookings = new ArrayList<Booking>();
        try {


            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            QueryBuilder startQuery = QueryBuilders.rangeQuery("checkInDate").lte(date);
            QueryBuilder endQuery = QueryBuilders.rangeQuery("checkOutDate").gt(date);
            QueryBuilder statusQuery = QueryBuilders.matchQuery("bookingStatus", Booking.Status.Active);
            QueryBuilder hotelQuery = QueryBuilders.matchQuery("hotelId", hotelId);
            QueryBuilder matchQuery = QueryBuilders.boolQuery().must(startQuery).must(endQuery).must(statusQuery).must(hotelQuery);

            searchSourceBuilder.query(matchQuery);

            Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("booking").addType("doc").build();
            SearchResult result = client.execute(search);

            bookings = result.getSourceAsObjectList(Booking.class, false);


        } catch (IOException ex) {
            System.out.println("Exception in fetching booking data by hotel id and date" + ex);
        }
        return bookings;
    }

    public List<Booking> getBookingByUserIdAndDate(Integer userId, String date) {

        List<Booking> bookings = new ArrayList<Booking>();
        try {

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            QueryBuilder startQuery = QueryBuilders.rangeQuery("checkInDate").lte(date);
            QueryBuilder endQuery = QueryBuilders.rangeQuery("checkOutDate").gt(date);
            QueryBuilder statusQuery = QueryBuilders.matchQuery("bookingStatus", Booking.Status.Active);
            QueryBuilder hotelQuery = QueryBuilders.matchQuery("userId", userId);
            QueryBuilder matchQuery = QueryBuilders.boolQuery().must(startQuery).must(endQuery).must(statusQuery).must(hotelQuery);

            searchSourceBuilder.query(matchQuery);

            Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("booking").addType("doc").build();
            SearchResult result = client.execute(search);

            bookings = result.getSourceAsObjectList(Booking.class, false);

        } catch (IOException ex) {
            System.out.println("Exception in fetching hotel by user id and date " + ex);
        }
        return bookings;
    }

    public Integer getAvailabilityByDateAndHotelId(String date, Integer hotelId) {

        RoomAvailability availability = new RoomAvailability();
        Integer availablerooms = 0;
        try {


            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            QueryBuilder matchQuery = QueryBuilders.boolQuery()
                    .must(QueryBuilders.matchQuery("hotelId", hotelId))
                    .must(QueryBuilders.matchQuery("date", date));

            searchSourceBuilder.query(matchQuery);

            Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("roomavailability").addType("doc").build();

            SearchResult result = client.execute(search);

            availability = result.getSourceAsObject(RoomAvailability.class, false);


        } catch (IOException ex) {
            System.out.println(ex);
        }
        if(availability != null){
            return availability.getAvailableRooms();
        }
        else{

            Hotel hotel = hotelService.getHotelById(hotelId);
            if(hotel != null){
                availablerooms = hotel.getAvailableRooms();
            }

            return availablerooms;
        }
    }

    public String updateRoomAvailabilityOnCancellation(Booking booking) {

        String res = "";

        LocalDate checkIn = LocalDate.parse(booking.getCheckInDate());
        LocalDate checkOut = LocalDate.parse(booking.getCheckOutDate());

        for (LocalDate date = checkIn; date.isBefore(checkOut); date = date.plusDays(1)) {
            try {



                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

                QueryBuilder matchQuery = QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("hotelId", booking.getHotelId()))
                        .must(QueryBuilders.matchQuery("date", date));


                searchSourceBuilder.query(matchQuery);
                Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("roomavailability").addType("doc").build();

                SearchResult result = client.execute(search);

                RoomAvailability roomAvailability = result.getSourceAsObject(RoomAvailability.class, false);
                int value = roomAvailability.getAvailableRooms();
                value++;
                roomAvailability.setAvailableRooms(value);
                res = addRoomAvailability(roomAvailability);

            } catch (IOException ex) {
                System.out.println("Error while update room availability cancelllation " + ex);
            }

        }
        return res;
    }


    public  Integer getAllBookingsByHotelIdAndDateOfBooking(Integer hotelId , String dateOfBooking){

        List<Booking> bookings = new ArrayList<Booking>();

            try {


                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

                QueryBuilder statusQuery = QueryBuilders.matchQuery("bookingStatus", Booking.Status.Active);
                QueryBuilder hotelQuery = QueryBuilders.matchQuery("hotelId", hotelId);
                QueryBuilder dateQuery = QueryBuilders.matchQuery("dateOfBooking", dateOfBooking);
                QueryBuilder matchQuery = QueryBuilders.boolQuery().must(statusQuery).must(hotelQuery).must(dateQuery);

                searchSourceBuilder.query(matchQuery);

                Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("booking").addType("doc").build();

                SearchResult result = client.execute(search);

                bookings = result.getSourceAsObjectList(Booking.class, false);


            } catch (IOException ex) {
                System.out.println("Exception in fetching booking data by hotel id and date" + ex);
            }
            return bookings.size();

    }


     public Map<Integer, ArrayList<com.hms.hotelmanagementsystem.utilities.Pair>> getTrendingHotels(){

        List<Hotel> hotels = hotelService.getAllHotels();
        LocalDate currentDate = LocalDate.now();
        String dateOfBooking = currentDate.toString();

        Map<Integer, ArrayList<com.hms.hotelmanagementsystem.utilities.Pair>> trendingHotels = new HashMap <Integer, ArrayList<com.hms.hotelmanagementsystem.utilities.Pair>>();

        for(int i = 0;i<hotels.size();i++){

            if(hotels.get(i).getCityId() != null){

                int hotelId = hotels.get(i).getHotelId();
                Integer count = getAllBookingsByHotelIdAndDateOfBooking(hotelId,dateOfBooking);
                Integer cityId = hotels.get(i).getCityId();
                com.hms.hotelmanagementsystem.utilities.Pair hotelCount = new com.hms.hotelmanagementsystem.utilities.Pair(count,hotelId);
                ArrayList <com.hms.hotelmanagementsystem.utilities.Pair> countpair ;

                if(trendingHotels.containsKey(cityId)){


                    countpair = trendingHotels.get(cityId);


                }else{

                    countpair = new ArrayList<com.hms.hotelmanagementsystem.utilities.Pair>();

                }

                countpair.add(hotelCount);

                Collections.sort(countpair,  new Comparator<com.hms.hotelmanagementsystem.utilities.Pair>() {
                    @Override
                    public int compare(com.hms.hotelmanagementsystem.utilities.Pair o1, com.hms.hotelmanagementsystem.utilities.Pair o2) {

                        return o2.bookingCount.compareTo(o1.bookingCount);

                    }
                });
                if(countpair.size()>=5){

                    countpair.remove(countpair.size()-1);

                }

                trendingHotels.put(cityId,countpair);

            }

        }

       return trendingHotels;
    }


    public Map<Integer,ArrayList<Pair>> getAllTrendingHotelsByCityIdFromCache(Integer cityId){

        Map<Integer,ArrayList<Pair>> trendingHotels = new HashMap<Integer, ArrayList<Pair>>();
        String city = cityId.toString();
        String[] cities = new String[1];
        cities[0] = city;
        trendingHotels = redisService.hmget("trends",cities);
        return  trendingHotels;
    }



    public Map<Integer, ArrayList<Pair>>  getAllTrendingHotelsFromCache(){

        return redisService.hgetall("trends");
    }

}