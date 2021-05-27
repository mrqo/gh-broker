package com.example.ghbroker.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServerConfig {
    @Getter
    @Value("${server.host}")
    private String host;

    @Getter
    @Value("${server.port}")
    private String port;

    public String getUri() {
        return "http://" + host + ":" + port;
    }
}
