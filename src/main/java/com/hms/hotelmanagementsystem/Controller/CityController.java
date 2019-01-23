package com.hms.hotelmanagementsystem.Controller;

import com.hms.hotelmanagementsystem.entities.City;
import com.hms.hotelmanagementsystem.entities.State;
import com.hms.hotelmanagementsystem.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CityController {

    @Autowired
    CityService cityService;

    @RequestMapping(value = "/createCity", method = RequestMethod.POST)
    public void createState(@RequestBody City city){
        cityService.createCity(city);
    }
}

