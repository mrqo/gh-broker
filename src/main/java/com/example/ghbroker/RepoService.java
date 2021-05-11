package com.example.ghbroker;

import org.springframework.cache.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;
import reactor.cache.CacheMono;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;

import java.util.Objects;

@Service
public class RepoService {
    public static String CACHE = "repos";

    private RepoServiceApi repoApi;

    private RedisCacheManager cacheManager;

    public RepoService(RepoServiceApi api, RedisCacheManager cacheManager) {
        this.repoApi = api;
        this.cacheManager = cacheManager;
    }

    public Mono<RepoModel> getRepo(String owner, String repository) {
        return CacheMono
            .lookup(
                repo -> tryGetCachedRepo(repo).map(Signal::next),
                makeCacheKey(owner, repository)
            )
            .onCacheMissResume(repoApi
                .getRepo(owner, repository)
                .flatMap(optional -> optional.map(Mono::just).orElseGet(Mono::empty))
            )
            .andWriteWith((key, signal) -> Mono.fromRunnable(() -> cacheManager.getCache(CACHE).put(key, signal.get())));
    }

    private Mono<RepoModel> tryGetCachedRepo(String key) {
        Object cacheModel = null;
        try {
            Cache.ValueWrapper cache = Objects.requireNonNull(cacheManager.getCache(CACHE)).get(key);
            if (cache != null) {
                cacheModel = cache.get();
            }
        } catch (Exception e) { }

        return (cacheModel == null
            ? Mono.empty()
            : Mono.just((RepoModel)cacheModel)
        );
    }

    public static String makeCacheKey(String owner, String repository) {
        return owner + "/" + repository;
    }
}
