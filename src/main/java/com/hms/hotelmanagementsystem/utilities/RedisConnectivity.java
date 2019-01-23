package com.hms.hotelmanagementsystem.utilities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import io.lettuce.core.protocol.RedisCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

@Component
@Configuration
public class RedisConnectivity {


    @Value("${redis.url}")
    private  String redisUrl;

    @Bean("redisconnection")
    public   StatefulRedisClusterConnection<String, String>  getRedisConnection(){

        RedisClusterClient redisClient = RedisClusterClient.create(redisUrl);
        StatefulRedisClusterConnection<String, String> connection = redisClient.connect();
        RedisAdvancedClusterCommands<String, String> sync = connection.sync();

        return connection;

    }

    public static  void closeConnection( StatefulRedisClusterConnection<String, String> connection){
         connection.close();
    }

}
