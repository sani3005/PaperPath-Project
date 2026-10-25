package com.example.smartcfp.service;

import com.example.smartcfp.model.User;
import com.example.smartcfp.model.UserTopicsResponse;
import com.example.smartcfp.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

@Service
public class UserTopicsService {

    private final UserRepository userRepository;

    public UserTopicsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserTopicsResponse getTopics(String email) {
        User user = findUser(email);
        return toResponse(user);
    }

    @Transactional
    public UserTopicsResponse updateTopics(String email, List<String> topics) {
        if (topics == null || topics.isEmpty()
                || topics.stream().anyMatch(topic -> topic == null || topic.isBlank())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Select at least one valid topic"
            );
        }

        User user = findUser(email);
        user.setTopics(new LinkedHashSet<>(topics.stream().map(String::trim).toList()));
        return toResponse(userRepository.save(user));
    }

    private User findUser(String email) {
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }

        return userRepository.findByEmail(email.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private UserTopicsResponse toResponse(User user) {
        return new UserTopicsResponse(
                user.getName(),
                user.getEmail(),
                user.getTopics().stream().sorted(String.CASE_INSENSITIVE_ORDER).toList()
        );
    }
}
