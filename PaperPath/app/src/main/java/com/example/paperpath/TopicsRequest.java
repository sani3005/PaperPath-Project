package com.example.paperpath;

import java.util.List;

public class TopicsRequest {
    private final List<String> topics;

    public TopicsRequest(List<String> topics) {
        this.topics = topics;
    }

    public List<String> getTopics() {
        return topics;
    }
}
