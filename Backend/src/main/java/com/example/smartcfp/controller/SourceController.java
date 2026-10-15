package com.example.smartcfp.controller;


import com.example.smartcfp.source.ConferenceSource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sources")
public class SourceController {

    private final List<ConferenceSource> sources;

    public SourceController(List<ConferenceSource> sources) {
        this.sources = sources;
    }

    @GetMapping
    public List<String> getSources() {
        return sources.stream()
                .map(ConferenceSource::getSourceName)
                .toList();
    }
}
