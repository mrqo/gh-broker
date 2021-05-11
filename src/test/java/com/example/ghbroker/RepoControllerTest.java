package com.example.ghbroker;

import com.example.ghbroker.config.ServerConfig;
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

import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
    RepoController.class,
    ServerConfig.class
}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes=RedisConfiguration.class)
@AutoConfigureWebTestClient
@EnableAutoConfiguration
public class RepoControllerTest {
    @Autowired
    private ServerConfig serverConfig;

    @MockBean
    RepoService service;

    @Autowired
    private WebTestClient webClient;

    @Test
    void getRepository_Found() {
        String user = "mrqo";
        String repoName = "ConvAPI";

        RepoModel model = RepoTestUtils.createFirstTestModel(user, repoName);

        Mockito.when(service.getRepo(user, repoName)).thenReturn(Mono.just(model));

        webClient.get()
            .uri(serverConfig.getUri() + "/repo/{owner}/{repository}", user, repoName)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
                .jsonPath("$.fullName").isEqualTo(model.getFullName())
                .jsonPath("$.description").isEqualTo(model.getDescription())
                .jsonPath("$.stars").isEqualTo(model.getStars())
                .jsonPath("$.createdAt").isNotEmpty();

        Mockito.verify(service, times(1)).getRepo(user, repoName);
    }

    @Test
    void getRepository_NotFound() {
        String user = "mrqo";
        String repoName = "ConvAPI";

        Mockito.when(service.getRepo(user, repoName)).thenReturn(Mono.empty());

        webClient.get()
            .uri(serverConfig.getUri() + "/repo/{owner}/{repository}", user, repoName)
            .exchange()
            .expectStatus().isNotFound();

        Mockito.verify(service, times(1)).getRepo(user, repoName);
    }
}
