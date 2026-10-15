package com.example.smartcfp.controller;

import com.example.smartcfp.service.ConferenceImportService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/sources")
public class ConferenceImportController {

    private final ConferenceImportService importService;

    public ConferenceImportController(
            ConferenceImportService importService) {
        this.importService = importService;
    }

    @PostMapping("/import")
    public Map<String, Object> importConferences() {
        return importService.importAll();
    }
}
