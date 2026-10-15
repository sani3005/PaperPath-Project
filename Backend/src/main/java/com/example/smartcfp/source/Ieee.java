package com.example.smartcfp.source;

import com.example.smartcfp.model.Conference;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class Ieee implements  ConferenceSource {


    @Override
    public String getSourceName() {
        return "Ieee";
    }

    @Override
    public List<Conference> scrape() throws IOException {

        String url = "https://callforpaper.org/search";

        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (compatible; SmartCFP/1.0)")
                .timeout(15000)
                .get();

        List<Conference> results = new ArrayList<>();

        Elements links = doc.select("a[href*='event.showcfp']");

        for (Element link : links) {
            String title = link.text().trim();
            String website = link.absUrl("href");

            if (!title.isEmpty() && !website.isEmpty()) {
                Conference conference = new Conference(
                        null,
                        title,
                        "",
                        "",
                        "",
                        "",
                        website
                );

                conference.setSourceName(getSourceName());
                results.add(conference);
            }
        }

        return results;
    }
}
