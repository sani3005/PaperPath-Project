package com.example.smartcfp.service;

import com.example.smartcfp.model.Conference;
import com.example.smartcfp.repository.ConferenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConferenceService {
    private final ConferenceRepository repository;

    public ConferenceService(ConferenceRepository repository) {
        this.repository = repository;
    }

    public List<Conference> getAllConferences() {
        return repository.findAllByOrderByIdDesc();
    }

    public List<Conference> searchConferences(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllConferences();
        }
        return repository.searchConferences(keyword.trim());
    }
}


