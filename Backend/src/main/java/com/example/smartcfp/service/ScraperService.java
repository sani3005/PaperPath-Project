
package com.example.smartcfp.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScraperService {

    public List<ConferenceInfo> getConferenceDetails()
            throws IOException {

        String url = "https://www.wikicfp.com/cfp/";

        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(15000)
                .get();

        List<ConferenceInfo> conferences = new ArrayList<>();

        Elements links = doc.select("a[href*='event.showcfp']");

        for (Element link : links) {
            String title = link.text().trim();
            String href = link.absUrl("href");

            if (!title.isEmpty()) {
                conferences.add(
                        new ConferenceInfo(title, href)
                );
            }
        }

        return conferences;
    }

    public static class ConferenceInfo {
        private final String title;
        private final String website;

        public ConferenceInfo(String title, String website) {
            this.title = title;
            this.website = website;
        }

        public String getTitle() {
            return title;
        }

        public String getWebsite() {
            return website;
        }
    }
}
