package com.example.ghbroker;

import java.time.Instant;

public class RepoTestUtils {
    public static RepoModel createFirstTestModel(String user, String repoName) {
        RepoModel model = new RepoModel();
        model.setId(0);
        model.setFullName(user + "/" + repoName);
        model.setDescription("description");
        model.setCreatedAt(Instant.now());
        model.setWatchers(3);

        return model;
    }

    public static RepoModel createSecondTestModel(String user, String repoName) {
        RepoModel model = new RepoModel();
        model.setId(1);
        model.setFullName(user + "/" + repoName);
        model.setDescription("desc2");
        model.setCreatedAt(Instant.now());
        model.setWatchers(10);

        return model;
    }
}
