package com.example.ghbroker;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class RepoModel {
    private String fullName;
    private String description;
    private String cloneUrl;
    private int watchers;
    private Instant createdTime;
}
