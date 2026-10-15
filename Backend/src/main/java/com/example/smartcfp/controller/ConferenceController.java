package com.example.smartcfp.controller;

import com.example.smartcfp.model.Conference;
import com.example.smartcfp.service.ConferenceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conferences")
public class ConferenceController {

    private final ConferenceService conferenceService;

    public ConferenceController(ConferenceService conferenceService) {
        this.conferenceService = conferenceService;
    }

    @GetMapping
    public List<Conference> getAllConference() {
        return conferenceService.getAllConferences();
    }

    @GetMapping("/search")
    public List<Conference> searchConference(
            @RequestParam String keyword) {
        return conferenceService.searchConferences(keyword);
    }



}