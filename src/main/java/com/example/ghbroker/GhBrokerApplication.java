package com.example.ghbroker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication
@EnableReactiveFeignClients
@EnableCaching
public class GhBrokerApplication {
    public static void main(String[] args) {
        SpringApplication.run(GhBrokerApplication.class, args);
    }
}
