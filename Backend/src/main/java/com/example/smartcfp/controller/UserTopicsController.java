package com.example.smartcfp.controller;

import com.example.smartcfp.model.TopicsRequest;
import com.example.smartcfp.model.UserTopicsResponse;
import com.example.smartcfp.service.UserTopicsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserTopicsController {

    private final UserTopicsService userTopicsService;

    public UserTopicsController(UserTopicsService userTopicsService) {
        this.userTopicsService = userTopicsService;
    }

    @GetMapping("/{email}/topics")
    public UserTopicsResponse getTopics(@PathVariable String email) {
        return userTopicsService.getTopics(email);
    }

    @PutMapping("/{email}/topics")
    public UserTopicsResponse updateTopics(
            @PathVariable String email,
            @RequestBody TopicsRequest request
    ) {
        return userTopicsService.updateTopics(email, request == null ? null : request.getTopics());
    }
}
