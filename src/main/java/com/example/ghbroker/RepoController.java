package com.example.ghbroker;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class RepoController {
    private RepoService repoService;

    private ModelMapper modelMapper = new ModelMapper();

    public RepoController(RepoService repoService) {
        this.repoService = repoService;
    }

    @GetMapping("/repo/{owner}/{repository}")
    Mono<RepoDto> getRepo(@PathVariable String owner, @PathVariable String repository) {
        return repoService.getRepo(owner, repository)
            .map(repo -> modelMapper.map(repo, RepoDto.class));
    }

}
