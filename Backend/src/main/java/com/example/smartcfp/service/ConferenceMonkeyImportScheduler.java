package com.example.smartcfp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ConferenceMonkeyImportScheduler {

    private static final Logger logger =
            LoggerFactory.getLogger(ConferenceMonkeyImportScheduler.class);
    private static final String SOURCE_NAME = "ConferenceMonkey";

    private final ConferenceImportService importService;

    public ConferenceMonkeyImportScheduler(ConferenceImportService importService) {
        this.importService = importService;
    }

    @Scheduled(initialDelay = 10_000, fixedDelay = 86_400_000)
    public void importConferences() {
        logger.info("Scheduled ConferenceMonkey import result: {}",
                importService.importSource(SOURCE_NAME));
    }
}
