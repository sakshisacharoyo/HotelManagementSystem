package com.hms.hotelmanagementsystem.service;

import com.hms.hotelmanagementsystem.entities.City;
import com.hms.hotelmanagementsystem.entities.Hotel;

import com.hms.hotelmanagementsystem.entities.State;
import com.hms.hotelmanagementsystem.utilities.JestConnectivity;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.searchbox.client.JestClient;
import io.searchbox.core.*;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

@Service
public class HotelService {



    @PostConstruct
    void test() {
        System.out.println("TEST");
    }

    @Autowired
    private CityService cityService;

    @Autowired
    private StateService stateService;

    @Autowired
    JestConnectivity jestConnector;

    @Qualifier("jestClient")
    @Autowired
    JestClient client;



    public String createHotel(Hotel h) {


        if(h.getHotelId() != null){
            try {


                Index index = new Index.Builder(h).index("oyorooms").type("doc").build();
                client.execute(index);
                return "Hotel added successfully !!";

            } catch (IOException ex) {
                return "Exception  in creating new  hotel , hotelservice.createHotel failed" + ex;
            }
        }else{
            return "hotel id not found !!";
        }

    }

    public List<Hotel> searchHotel(Set<Integer> hotelIds) {

        Hotel hotel = new Hotel();
        List<Hotel> hotels = new ArrayList<>();
        try {


            for (Integer hotelId : hotelIds) {

                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
                searchSourceBuilder.query(QueryBuilders.matchQuery("hotelId", hotelId));
                Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("oyorooms").addType("doc").build();
                SearchResult result = client.execute(search);
                hotel = result.getSourceAsObject(Hotel.class, false);
                if(hotel != null){

                    hotels.add(hotel);
                }

            }


        } catch (IOException ex) {
            System.out.println("Exception in hotel search , hotelservice.searchHotel failed " + ex);
        }


        return hotels;
    }

    public Hotel getHotelById(Integer hotelId){

        Hotel hotel = new Hotel();
        try{


            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchQuery("hotelId", hotelId));
            Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("oyorooms").addType("doc").build();
            SearchResult result = client.execute(search);
            hotel = result.getSourceAsObject(Hotel.class, false);

        }catch(IOException ex){
            System.out.println(" Exception in fetching hotel by id , hotelservice.getHotelById failed " + ex);
        }
        return hotel;
    }

    public List<Hotel> searchHotelByCityIdAndStateId(Integer cityId, Integer stateId) {

        List<Hotel> hotels = new ArrayList<>();
        try {


            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            QueryBuilder matchQuery = QueryBuilders.boolQuery()
                    .must(QueryBuilders.matchQuery("cityId", cityId))
                    .must(QueryBuilders.matchQuery("stateId", stateId));
            searchSourceBuilder.query(matchQuery);
            Search search = new Search.Builder(searchSourceBuilder.size(100).toString()).addIndex("oyorooms").addType("doc").build();
            SearchResult result = client.execute(search);
            hotels = result.getSourceAsObjectList(Hotel.class, false);
        } catch (IOException ex) {
            System.out.println(" Error in hotel search by city and state , hotelService.searchHotelByCityIdAndStateId failed  " + ex);
        }

        return hotels;
    }

    public List<Hotel> searchByCityNameAndStateName(String cityName, String stateName) {

        List<Hotel> hotels = new ArrayList<>();
        State state = stateService.getStateByName(stateName);
        if(state != null){
            int stateId = state.getStateId();
            City city = cityService.getCityByCityNameAndStateId(cityName, stateId);
            if(city != null){
                Integer cityId = city.getCityId();
                hotels = searchHotelByCityIdAndStateId(cityId, stateId);
            }

        }

        return hotels;
    }

    public List<Hotel> searchByState(String stateName) {

        List<Hotel> hotels = new ArrayList<>();
        State state = stateService.getStateByName(stateName);
        if(state != null){
            Integer stateId = state.getStateId();
            try {


                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
                searchSourceBuilder.query(QueryBuilders.matchQuery("stateId", stateId));
                Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("oyorooms").addType("doc").build();
                SearchResult result = client.execute(search);
                hotels = result.getSourceAsObjectList(Hotel.class, false);
            } catch (IOException ex) {
                System.out.println("Exception in hotel search by city and state , hotelService.searchBystate failed " + ex);
            }
        }

        return hotels;
    }

    public List<Hotel> searchByStateId(Integer stateId){

            List<Hotel> hotels = new ArrayList<>();

        try{

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchQuery("stateId", stateId));
            Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("oyorooms").addType("doc").build();
            SearchResult result = client.execute(search);
            hotels = result.getSourceAsObjectList(Hotel.class, false);

        } catch(IOException ex){
            System.out.println("Exception in hotel search by state Id " + ex);
        }
        return hotels;
    }

    public List<Hotel> searchByCityId(Integer cityId){

        List<Hotel> hotels = new ArrayList<>();

        try{

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchQuery("cityId", cityId));
            Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("oyorooms").addType("doc").build();
            SearchResult result = client.execute(search);
            hotels = result.getSourceAsObjectList(Hotel.class, false);

        } catch(IOException ex){
            System.out.println("Exception in hotel search by state Id " + ex);
        }
        return hotels;
    }

    public String deleteByHotelId(Integer hotelId) {

        try {


            Delete delete = new Delete.Builder(Integer.toString(hotelId))
                    .index("oyorooms").type("doc").build();
            client.execute(delete);
            return "hotel deleted successfully !!";

        } catch (IOException ex) {
            return "Exception in hotel deletion in elastic search , hotelservice.deleteByhotelId failed " + ex;
        }


    }

        private enum UpdatePayload {
          DOC("doc"),
          DOC_AS_UPSERT("doc_as_upsert");

          private final String key;

           UpdatePayload(String key) {
            this.key = key;
           }
       }

         private Map<String, Object> getUpdatePayload(Hotel hotel, boolean upsert) {
            Map<String, Object> payload = new HashMap<>();
            payload.put(UpdatePayload.DOC.key, hotel);
            payload.put(UpdatePayload.DOC_AS_UPSERT.key, upsert);
            return payload;
         }

         public String updateHotel(Hotel hotel) {

                 String res = "";
                 Update updateAction = new Update.Builder(getUpdatePayload(hotel, false)).index("oyorooms").type("doc").id(Integer.toString(hotel.getHotelId()))
                .build();

          try {

              DocumentResult up =    client.execute(updateAction);

              if(up.isSucceeded()){

                  return "hotel updated successfully !!";
              }
              else{

                  return "hotel not found !!";
              }

           } catch (IOException e) {

            return "Exception in update in hotel , hotelservice.getupdatebypayload failed" ;

            }

       }

    public List<Hotel> getHotelByName(String name){
        List<Hotel> hotels = new ArrayList<Hotel>();
        try{

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            QueryBuilder fuzzyquery = QueryBuilders.matchQuery("hotelName",name).fuzziness("AUTO");
            QueryBuilder prefixQuery = QueryBuilders.prefixQuery("hotelName",name);
            QueryBuilder matchquery = QueryBuilders.boolQuery().should(fuzzyquery).should(prefixQuery);
            searchSourceBuilder.query(matchquery);
            Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("oyorooms").addType("doc").build();
            SearchResult result = client.execute(search);
            hotels = result.getSourceAsObjectList(Hotel.class, false);

        }catch(IOException ex){
            System.out.println("Exception in fetching hotels by name , hotelService.getHotelByname failed  " + ex);
        }

        return hotels;
    }


    public List<Hotel> getAllHotels(){

        List<Hotel> hotels = new ArrayList<Hotel>();
        try{


            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
            Search search = new Search.Builder(searchSourceBuilder.size(1000).toString()).addIndex("oyorooms").addType("doc").build();
            SearchResult result = client.execute(search);
            hotels = result.getSourceAsObjectList(Hotel.class,false);

        }catch(IOException ex){

            System.out.println(" Exception in fetching hotels  , hotelService.getAllHotels failed " + ex);

        }
        return hotels;

    }


}
