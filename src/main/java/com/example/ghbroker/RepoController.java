package com.example.ghbroker;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class RepoController {
    private RepoService repoService;

    public RepoController(RepoService repoService) {
        this.repoService = repoService;
    }

    @GetMapping("/repo/{owner}/{repository}")
    Mono<RepoModel> getRepo(@PathVariable String owner, @PathVariable String repository) {
        return repoService.getRepo(owner, repository);
    }
}
