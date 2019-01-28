package com.hms.hotelmanagementsystem.service;

import com.hms.hotelmanagementsystem.entities.City;
import com.hms.hotelmanagementsystem.entities.Hotel;
import com.hms.hotelmanagementsystem.utilities.JestConnectivity;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class CityService {

    @Autowired
    private StateService stateService ;

    @Autowired
    JestConnectivity jestConnector;

    @Qualifier("jestClient")
    @Autowired
    JestClient client;

    public City getCityByCityNameAndStateId(String cityName , int stateId){

        City city = new City();
        try{


            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            QueryBuilder matchQuery = QueryBuilders.boolQuery()
                    .must(QueryBuilders.matchQuery("cityName",cityName))
                    .must(QueryBuilders.matchQuery("stateId",stateId));
            searchSourceBuilder.query(matchQuery);
            Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("city").addType("doc").build();
            SearchResult result = client.execute(search);
            city = result.getSourceAsObject(City.class,false);
        }
        catch(IOException ex){
                System.out.println(" Error in city search by name and state id , cityservice.getCityIdByCityNameAndStateId failed " + ex );
        }
        return city;
    }
    public void createCity(City city){


        try{

            Index index = new Index.Builder(city).index("city").type("doc").build();
            client.execute(index);

        }
        catch(IOException ex){
            System.out.println(" Exception in creating city , cityservice.createCity failed " + ex);
        }

    }


    public List<City> getAllCity(){

        List<City> cities = new ArrayList<City>();

        try{

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
            Search search = new Search.Builder(searchSourceBuilder.size(300).toString()).addIndex("city").addType("doc").build();
            SearchResult result = client.execute(search);
            cities = result.getSourceAsObjectList(City.class,false);
        }catch (IOException ex){
            System.out.println(" Exception in fetching all city ids , cityservice.getallcityids failed "  + ex);
        }

        return cities;
    }

}
