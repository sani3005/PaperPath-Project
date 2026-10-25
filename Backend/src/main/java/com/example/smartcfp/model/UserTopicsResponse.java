package com.example.smartcfp.model;

import java.util.List;

public class UserTopicsResponse {
    private final String name;
    private final String email;
    private final List<String> topics;

    public UserTopicsResponse(String name, String email, List<String> topics) {
        this.name = name;
        this.email = email;
        this.topics = topics;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getTopics() {
        return topics;
    }
}
