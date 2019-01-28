package com.hms.hotelmanagementsystem.service;

import com.hms.hotelmanagementsystem.entities.State;
import com.hms.hotelmanagementsystem.utilities.JestConnectivity;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class StateService {


    @Autowired
    JestConnectivity jestConnector;

    @Qualifier("jestClient")
    @Autowired
    JestClient client;

    public void createState(State state){


        try{

            Index index = new Index.Builder(state).index("state").type("doc").build();
            client.execute(index);

        }
        catch(IOException ex){
            System.out.println("Exception in creating state  , Stateservice.createState failed" + ex);
        }

    }

    public State getStateByName(String stateName){
        State state = new State() ;
        try{


                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
                searchSourceBuilder.query(QueryBuilders.matchQuery("stateName",stateName));
                Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("state").addType("doc").build();
                SearchResult result = client.execute(search);
                state = result.getSourceAsObject(State.class,false);


        }
        catch(IOException ex){
            System.out.println("exception in searching state by name" + ex);
        }
        return state;
    }

    public List<State> getAllStates(){
        List<State> states = new ArrayList<State>();
        try{
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
            Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("state").addType("doc").build();
            SearchResult result = client.execute(search);
            states = result.getSourceAsObjectList(State.class,false);
        }catch(IOException ex){
            System.out.println("Exception in searching all states " +ex);
        }
        return states;
    }


}
