package com.hms.hotelmanagementsystem.service;

import com.hms.hotelmanagementsystem.entities.User;
import com.hms.hotelmanagementsystem.utilities.JestConnectivity;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;

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


}
