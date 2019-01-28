package com.hms.hotelmanagementsystem.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hms.hotelmanagementsystem.utilities.Pair;
import com.hms.hotelmanagementsystem.utilities.RedisConnectivity;
import io.lettuce.core.KeyValue;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class RedisService {

    @Autowired
    RedisConnectivity redisConnector;

    @Qualifier("redisconnection")
    @Autowired
    StatefulRedisClusterConnection<String, String> redisconnection;

    public  void set(String key, Object object) {
        try  {
            Gson gson = new Gson();
            redisconnection.sync().set(key, gson.toJson(object));
            String data = redisconnection.sync().get(key);
        } catch (Exception e) {
            System.out.println(" redis set failed " + e);
        }

    }

    public ArrayList<Pair> get(String key){
        ArrayList<Pair> bookingCounts = new ArrayList<Pair>();
        try  {
            String data = redisconnection.sync().get(key);
            ObjectMapper objectMapper = new ObjectMapper();
            bookingCounts = objectMapper.readValue(data, new TypeReference<ArrayList<Pair>>(){});

        } catch (Exception ex) {
            System.out.println("redis get failed " + ex);
        }
        return bookingCounts;
    }

    public  void hmset(String key , Map<Integer,ArrayList<Pair>> trendingHotels , int ttlSeconds) {

        if (CollectionUtils.isEmpty(trendingHotels))
            return;

        try  {
            Gson gson = new Gson();

            redisconnection.sync().hmset(key, trendingHotels.entrySet().stream()
                    .filter(e -> Objects.nonNull(e.getValue()))
                    .collect(Collectors.toMap(e -> e.getKey().toString(), e -> gson.toJson(e.getValue()))));
        } catch (Exception e) {
            System.out.println("Redis hmset failed for key {}, exception: {}" +  e);
        }
        if (ttlSeconds > 0) {
            setKeyExpiry(key, ttlSeconds);
        }


    }

    public void setKeyExpiry(String key, int ttlSeconds) {
        try  {
            if (ttlSeconds > 0) {
                redisconnection.async().expire(key, ttlSeconds);
            }
        } catch (Exception e) {
            System.out.println(" Exception in redis expiry command for key {}, exception: {} " + e);
        }
    }

    public   Map<Integer,ArrayList<Pair>> hmget(String key, String[] hashFields) {
        String res = "";
        Map<Integer,ArrayList<Pair>> finalResponse = new HashMap<Integer, ArrayList<Pair>>();

        try  {
            Gson gson = new Gson();
            List<KeyValue<String, String>> response = redisconnection.sync().hmget(key, hashFields);
            System.out.println(response.toString()) ;
            res = response.toString();

            for(int i = 0 ;i<response.size() ; i++ ){

                if(response.get(i).hasValue()){

                    Integer cityId= Integer.parseInt(response.get(i).getKey());
                    ArrayList<Pair> pairArrayList = new ArrayList<Pair>();
                    ObjectMapper objectMapper = new ObjectMapper();
                    pairArrayList = objectMapper.readValue(response.get(i).getValue(),new TypeReference<ArrayList<Pair>>(){});
                    finalResponse.put(cityId,pairArrayList);
                }

            }

        } catch (Exception e) {
            System.out.println(" Redis hmget failed " + e);
        }
    return finalResponse;
    }


    public  Map<Integer, ArrayList<Pair>> hgetall(String key) {
        Map<Integer, ArrayList<Pair>> finalResponse = new LinkedHashMap<>();
        try  {
            Gson gson = new Gson();
            Map<String, String> response = redisconnection.sync().hgetall(key);
            Type type = new TypeToken<ArrayList<Pair>>() {}.getType();
            if (!CollectionUtils.isEmpty(response)) {
                for (Map.Entry<String, String> entry : response.entrySet()) {
                    finalResponse.put(Integer.parseInt(entry.getKey()), gson.fromJson(entry.getValue(),type));
                }
            }
        } catch (Exception e) {
            System.out.println("redis hgetall failed " + e );
        }
        return finalResponse;
    }





}
