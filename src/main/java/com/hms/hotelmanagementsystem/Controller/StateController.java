package com.hms.hotelmanagementsystem.Controller;

import com.hms.hotelmanagementsystem.entities.State;
import com.hms.hotelmanagementsystem.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StateController {

    @Autowired
    StateService stateService;

    @RequestMapping(value = "/createState", method = RequestMethod.POST)
    public void createState(@RequestBody State state){
        stateService.createState(state);

    }

    @RequestMapping(value = "/getAllStates" , method = RequestMethod.GET)
        public List<State> getAllStates(){
            List<State> stateList = stateService.getAllStates();
            return stateList;
        }

}
