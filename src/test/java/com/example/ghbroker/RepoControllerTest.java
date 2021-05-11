package com.example.ghbroker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RepoController.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes=RedisConfiguration.class)
@AutoConfigureWebTestClient
@EnableAutoConfiguration
public class RepoControllerTest {
    private static final String BASE_URL = "http://localhost:8080";

    @MockBean
    RepoService service;

    @Autowired
    private WebTestClient webClient;

    @Test
    void getRepository_Found() {
        String user = "mrqo";
        String repoName = "ConvAPI";

        RepoModel model = new RepoModel();
        model.setId(0);
        model.setFullName(user + "/" + repoName);
        model.setDescription("description");
        model.setCreatedAt(Instant.now());
        model.setWatchers(3);

        Mockito.when(service.getRepo(user, repoName)).thenReturn(Mono.just(model));

        webClient.get()
            .uri(BASE_URL + "/repo/{owner}/{repository}", user, repoName)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
                .jsonPath("$.fullName").isEqualTo(model.getFullName())
                .jsonPath("$.description").isEqualTo(model.getDescription())
                .jsonPath("$.watchers").isEqualTo(model.getWatchers())
                .jsonPath("$.createdAt").isNotEmpty();

        Mockito.verify(service, times(1)).getRepo(user, repoName);
    }

    @Test
    void getRepository_NotFound() {
        String user = "mrqo";
        String repoName = "ConvAPI";

        Mockito.when(service.getRepo(user, repoName)).thenReturn(Mono.empty());

        webClient.get()
            .uri(BASE_URL + "/repo/{owner}/{repository}", user, repoName)
            .exchange()
            .expectStatus().isNotFound();

        Mockito.verify(service, times(1)).getRepo(user, repoName);
    }
}
