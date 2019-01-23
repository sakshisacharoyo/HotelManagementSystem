package com.hms.hotelmanagementsystem.Controller;

import com.hms.hotelmanagementsystem.entities.User;
import com.hms.hotelmanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService ;


    @RequestMapping(value = "/addUser" , method = RequestMethod.POST)
    public void createUser(@RequestBody User user){
        userService.addUser(user);
    }
}
