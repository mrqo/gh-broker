package com.example.ghbroker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
    RepoController.class,
    RepoService.class,
}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes=RedisConfiguration.class)
@AutoConfigureWebTestClient
@EnableAutoConfiguration
@EnableReactiveFeignClients
public class RepoIT {
    private static final String BASE_URL = "http://localhost:8080";

    @Autowired
    private WebTestClient webClient;

    @Test
    void getRepository_Found() {
        String user = "apache";
        String repoName = "spark";

        webClient.get()
            .uri(BASE_URL + "/repo/{owner}/{repository}", user, repoName)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
                .jsonPath("$.fullName").isEqualTo("apache/spark")
                .jsonPath("$.watchers").isNumber()
                .jsonPath("$.createdAt").isNotEmpty();
    }

    @Test
    void getRepository_NotFound() {
        String user = "apache";
        String repoName = "sparkssss2";

        webClient.get()
            .uri(BASE_URL + "/repo/{owner}/{repository}", user, repoName)
            .exchange()
            .expectStatus().isNotFound();
    }
}
