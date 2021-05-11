package com.example.ghbroker;

import feign.Headers;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@ReactiveFeignClient(name = "repo-api", url = "https://api.github.com", decode404 = true)
@Headers({ "Accept: application/json" })
@EnableCaching
public interface RepoServiceApi {

    @GetMapping(value = "/repos/{owner}/{repository}")
    Mono<Optional<RepoModel>> getRepo(@RequestParam("owner") String owner, @RequestParam("repository") String repository);
}
