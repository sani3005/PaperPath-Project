package com.example.smartcfp.controller;



import com.example.smartcfp.service.ScraperService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

    @RestController
    @RequestMapping("/api/scraper")
    public class ScraperController {

        private final ScraperService scraperService;

        public ScraperController(ScraperService scraperService) {
            this.scraperService = scraperService;
        }




        @GetMapping("/details")
        public List<ScraperService.ConferenceInfo> getDetails()
                throws IOException {
            return scraperService.getConferenceDetails();
        }

    }


