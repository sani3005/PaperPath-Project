package com.example.smartcfp.service;


import com.example.smartcfp.model.Conference;
import com.example.smartcfp.repository.ConferenceRepository;
import com.example.smartcfp.source.ConferenceSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConferenceImportService {

    private static final Logger logger =
            LoggerFactory.getLogger(ConferenceImportService.class);

    private final List<ConferenceSource> sources;
    private final ConferenceRepository repository;

    public ConferenceImportService(
            List<ConferenceSource> sources,
            ConferenceRepository repository) {
        this.sources = sources;
        this.repository = repository;
    }

    public Map<String, Object> importAll() {
        return importSources(sources);
    }

    public Map<String, Object> importSource(String sourceName) {
        if (sourceName == null || sourceName.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Source name is required");
        }

        ConferenceSource source = sources.stream()
                .filter(candidate -> candidate.getSourceName()
                        .equalsIgnoreCase(sourceName.trim()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Conference source not found"));
        return importSources(List.of(source));
    }

    private Map<String, Object> importSources(List<ConferenceSource> sourcesToImport) {
        Map<String, Object> report = new LinkedHashMap<>();
        int inserted = 0;
        int updated = 0;
        int duplicates = 0;

        for (ConferenceSource source : sourcesToImport) {
            try {
                List<Conference> conferences =
                        source.scrape();

                int sourceInserted = 0;
                int sourceUpdated = 0;
                int sourceDuplicates = 0;

                for (Conference conference : conferences) {
                    String website = conference.getWebsite();

                    if (website == null || website.isBlank()) {
                        continue;
                    }

                    var existingConference = repository.findByWebsite(website);
                    if (existingConference.isPresent()) {
                        Conference existing = existingConference.get();
                        existing.setShortTitle(conference.getShortTitle());
                        existing.setLongTitle(conference.getLongTitle());
                        existing.setLocation(conference.getLocation());
                        if (hasText(conference.getDeadline())) {
                            existing.setDeadline(conference.getDeadline());
                        }
                        if (hasText(conference.getEventDates())) {
                            existing.setEventDates(conference.getEventDates());
                        }
                        if (hasText(conference.getDescription())) {
                            existing.setDescription(conference.getDescription());
                        }
                        existing.setSourceName(conference.getSourceName());
                        if (hasText(conference.getTopics())) {
                            existing.setTopics(conference.getTopics());
                        }
                        if (conference.getRegistrationUrl() != null
                                && !conference.getRegistrationUrl().isBlank()) {
                            existing.setRegistrationUrl(conference.getRegistrationUrl());
                        }
                        repository.save(existing);
                        sourceDuplicates++;
                        sourceUpdated++;
                    } else {
                        repository.save(conference);
                        sourceInserted++;
                    }
                }

                inserted += sourceInserted;
                updated += sourceUpdated;
                duplicates += sourceDuplicates;

                report.put(source.getSourceName(),
                        Map.of(
                                "status", "SUCCESS",
                                "found", conferences.size(),
                                "inserted", sourceInserted,
                                "updated", sourceUpdated,
                                "duplicates", sourceDuplicates
                        ));

            } catch (IOException | RuntimeException ex) {
                logger.error("Failed to import conferences from {}",
                        source.getSourceName(), ex);
                report.put(source.getSourceName(),
                        Map.of(
                                "status", "FAILED",
                                "message", errorMessage(ex)
                        ));
            }
        }

        report.put("totalInserted", inserted);
        report.put("totalUpdated", updated);
        report.put("totalDuplicates", duplicates);

        return report;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String errorMessage(Exception exception) {
        String message = exception.getMessage();
        return message == null || message.isBlank()
                ? "Could not import this source (" + exception.getClass().getSimpleName() + ")"
                : message;
    }
}
