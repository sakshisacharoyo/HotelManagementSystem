package com.hms.hotelmanagementsystem.service;

import com.hms.hotelmanagementsystem.entities.RoomAvailability;
import com.hms.hotelmanagementsystem.entities.User;
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
public class UserService {

    @Autowired
    JestConnectivity jestConnector;

    @Qualifier("jestClient")
    @Autowired
    JestClient client;

    public void addUser(User user){

        try {
            Index index = new Index.Builder(user).index("user").type("doc").build();
            client.execute(index);

        } catch (IOException ex) {
            System.out.println(" Exception in adding user  UserService.adduser method failed " + ex);
        }
    }


    public User getUserById(Integer userId){

        User user  = new User();
        try{

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchQuery("userId",userId));
            Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("user").addType("doc").build();
            SearchResult result = client.execute(search);
            user = result.getSourceAsObject(User.class, false);


        }catch(IOException ex){
            System.out.println(" Exception in getting user by id");
        }
        return user;
    }


    public List<User> getAllUsers(){

        List<User> userList = new ArrayList<User>();
        try{

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
            Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("user").addType("doc").build();
            SearchResult result = client.execute(search);
            userList = result.getSourceAsObjectList(User.class,false);
        }catch(IOException ex){
            System.out.println("Exception in finding all users " + ex);
        }
        return userList;
    }


}
