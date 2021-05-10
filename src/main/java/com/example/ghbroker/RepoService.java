package com.example.ghbroker;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RepoService {
    private RepoServiceApi repoApi;

    public RepoService(RepoServiceApi api) {
        this.repoApi = api;
    }

    public Mono<RepoModel> getRepo(String owner, String repository) {
        return repoApi.getRepo(owner, repository);
    }
}
