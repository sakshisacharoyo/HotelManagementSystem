package com.hms.hotelmanagementsystem.utilities;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


@Component
@Configuration
public class JestConnectivity {

    @Value("${elasticsearch.url}")
    private String elasticSearchUrl ;


    @Bean("jestClient")
    public  JestClient createClient(){

        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder(elasticSearchUrl).multiThreaded(true).defaultMaxTotalConnectionPerRoute(2).maxTotalConnection(20).build());
        JestClient client = factory.getObject();
        return client;
    }

}
