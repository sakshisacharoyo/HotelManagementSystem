package com.hms.hotelmanagementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Configuration
@EnableScheduling
public class HotelManagementSystemApplication {

    public static void main(String[] args) {

        SpringApplication.run(HotelManagementSystemApplication.class, args);

      //  RedisConnectivity.redisConnection();

    }

}

