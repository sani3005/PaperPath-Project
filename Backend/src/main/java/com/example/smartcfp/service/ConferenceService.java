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
        return repository.findAll();
    }

    public List<Conference> searchConferences(String keyword) {
        return repository.searchConferences(keyword);
    }
}



