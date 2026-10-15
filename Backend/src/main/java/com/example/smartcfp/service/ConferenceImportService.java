package com.example.smartcfp.service;


import com.example.smartcfp.model.Conference;
import com.example.smartcfp.repository.ConferenceRepository;
import com.example.smartcfp.source.ConferenceSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConferenceImportService {

    private final List<ConferenceSource> sources;
    private final ConferenceRepository repository;

    public ConferenceImportService(
            List<ConferenceSource> sources,
            ConferenceRepository repository) {
        this.sources = sources;
        this.repository = repository;
    }

    public Map<String, Object> importAll() {

        Map<String, Object> report = new LinkedHashMap<>();
        int inserted = 0;
        int duplicates = 0;

        for (ConferenceSource source : sources) {
            try {
                List<Conference> conferences =
                        source.scrape();

                int sourceInserted = 0;
                int sourceDuplicates = 0;

                for (Conference conference : conferences) {
                    String website = conference.getWebsite();

                    if (website == null || website.isBlank()) {
                        continue;
                    }

                    if (repository.findByWebsite(website).isPresent()) {
                        sourceDuplicates++;
                    } else {
                        repository.save(conference);
                        sourceInserted++;
                    }
                }

                inserted += sourceInserted;
                duplicates += sourceDuplicates;

                report.put(source.getSourceName(),
                        Map.of(
                                "status", "SUCCESS",
                                "found", conferences.size(),
                                "inserted", sourceInserted,
                                "duplicates", sourceDuplicates
                        ));

            } catch (IOException | RuntimeException ex) {
                report.put(source.getSourceName(),
                        Map.of(
                                "status", "FAILED",
                                "message", "Could not import this source"
                        ));
            }
        }

        report.put("totalInserted", inserted);
        report.put("totalDuplicates", duplicates);

        return report;
    }
}
