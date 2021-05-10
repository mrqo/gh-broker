package com.example.ghbroker;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RepoService {
    private RepoServiceApi repoApi;

    public RepoService(RepoServiceApi api) {
        this.repoApi = api;
    }

    @Cacheable("repos")
    public Mono<RepoModel> getRepo(String owner, String repository) {
        return repoApi.getRepo(owner, repository).cache();
    }
}
