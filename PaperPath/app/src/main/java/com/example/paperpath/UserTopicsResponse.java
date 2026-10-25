package com.example.paperpath;

import java.util.List;

public class UserTopicsResponse {
    private String name;
    private String email;
    private List<String> topics;

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
