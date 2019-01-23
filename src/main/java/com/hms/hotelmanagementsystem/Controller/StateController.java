package com.hms.hotelmanagementsystem.Controller;

import com.hms.hotelmanagementsystem.entities.State;
import com.hms.hotelmanagementsystem.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StateController {

    @Autowired
    StateService stateService;

    @RequestMapping(value = "/createState", method = RequestMethod.POST)
    public void createState(@RequestBody State state){
        stateService.createState(state);
    }
}
