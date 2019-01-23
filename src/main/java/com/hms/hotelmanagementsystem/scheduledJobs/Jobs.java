package com.hms.hotelmanagementsystem.scheduledJobs;

import com.hms.hotelmanagementsystem.service.BookingService;
import com.hms.hotelmanagementsystem.service.HotelService;
import com.hms.hotelmanagementsystem.service.RedisService;
import com.hms.hotelmanagementsystem.utilities.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class Jobs {


    @Autowired
    HotelService hotelService;

    @Autowired
    BookingService bookingService;

    @Autowired
    RedisService redisService;



    @Scheduled(cron = "0 */2 * ? * *")
    public void updatetrendingHotelCache(){


        System.out.println("job running");
       Map<Integer, ArrayList<Pair>>  trendingHotels = bookingService.getTrendingHotels();

        Iterator< Map.Entry<Integer, ArrayList<Pair>>> itr = trendingHotels.entrySet().iterator();

        while(itr.hasNext())

        {
            Map.Entry<Integer, ArrayList<Pair>> entry = itr.next();
            String key = entry.getKey().toString();
            ArrayList<Pair> values = entry.getValue();
            redisService.set(key,values);
        }

    }


}
