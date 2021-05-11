package com.example.ghbroker;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RepoModel implements Serializable {
    private long id;

    private String fullName;

    private String description;

    private String cloneUrl;

    @JsonProperty("watchers")
    private int stars;

    private Instant createdAt;
}
