package com.example.ghbroker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RepoService.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes=RedisConfiguration.class)
@EnableAutoConfiguration
public class RepoCachingTest {
    @Autowired
    private RepoService service;

    @Autowired
    private RedisCacheManager cacheManager;

    @MockBean
    private GithubApi api;

    @Test
    void askForNonCachedRepo_ExpectToBeCached() {
        String user = "mrqo";
        String repoName = "ConvAPI";

        RepoModel model = RepoTestUtils.createFirstTestModel(user, repoName);
        String cacheKey = RepoService.makeCacheKey(user, repoName);

        Mockito.when(api.getRepo(user, repoName))
            .thenReturn(Mono.just(Optional.of(model)));

        cacheManager.getCache(RepoService.CACHE)
            .evict(RepoService.makeCacheKey(user, repoName));

        Assert.isNull(
            cacheManager.getCache(RepoService.CACHE).get(cacheKey),
            "Repository is still cached"
        );

        service.getRepo(user, repoName).subscribe();

        Assert.notNull(
            cacheManager.getCache(RepoService.CACHE).get(cacheKey),
            "Repository is not cached"
        );

        Mockito.verify(api, times(1)).getRepo(user, repoName);
    }

    /**
     * Repository is being cached, but source data changes in between calls.
     * Cache should be present and obsolete.
     */
    @Test
    void askForCachedRepo_ExpectAlreadyCachedObsolete() {
        String user = "mrqo";
        String repoName = "ghb";

        RepoModel firstModel = RepoTestUtils.createFirstTestModel(user, repoName);
        RepoModel secondModel = RepoTestUtils.createSecondTestModel(user, repoName);
        String cacheKey = RepoService.makeCacheKey(user, repoName);

        Mockito.when(api.getRepo(user, repoName))
                .thenReturn(Mono.just(Optional.of(firstModel)));

        cacheManager.getCache(RepoService.CACHE)
                .evict(RepoService.makeCacheKey(user, repoName));

        service.getRepo(user, repoName).subscribe();

        Assert.notNull(
            cacheManager.getCache(RepoService.CACHE).get(cacheKey),
            "Repository is not cached"
        );

        Mockito.when(api.getRepo(user, repoName))
                .thenReturn(Mono.just(Optional.of(secondModel)));

        service.getRepo(user, repoName).subscribe();

        RepoModel cachedModel = (RepoModel)cacheManager.getCache(RepoService.CACHE).get(cacheKey).get();

        Assert.state(
            cachedModel.getStars() != secondModel.getStars(),
                "Number of watchers in cache should be invalid"
        );
    }
}
