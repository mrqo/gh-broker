package com.example.ghbroker;

import org.springframework.cache.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;
import reactor.cache.CacheMono;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class RepoService {
    private static String CACHE = "repos";

    private RepoServiceApi repoApi;

    private RedisCacheManager cacheManager;

    public RepoService(RepoServiceApi api, RedisCacheManager cacheManager) {
        this.repoApi = api;
        this.cacheManager = cacheManager;
    }

    public Mono<RepoModel> getRepo(String owner, String repository) {



        return CacheMono
            .lookup(
                repo -> {
                    Object cacheModel = null;
                    try {
                        Cache.ValueWrapper cache = Objects.requireNonNull(cacheManager.getCache(CACHE)).get(repo);
                        if (cache != null) {
                            cacheModel = cache.get();
                        }
                    } catch (Exception e) {

                    }

                    if (cacheModel == null) {
                        return Mono.<RepoModel>empty().map(Signal::next);
                    }
                    return Mono.<RepoModel>just((RepoModel)cacheModel).map(Signal::next);
                },
                makeCacheKey(owner, repository)
            )
            .onCacheMissResume(repoApi.getRepo(owner, repository))
            .andWriteWith((key, signal) -> Mono.fromRunnable(() -> cacheManager.getCache(CACHE).put(key, signal.get())));
    }

    private String makeCacheKey(String owner, String repository) {
        return owner + "/" + repository;
    }
}
