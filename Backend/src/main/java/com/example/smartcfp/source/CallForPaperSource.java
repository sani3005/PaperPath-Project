
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
public class CallForPaperSource implements ConferenceSource {

    private static final String BASE_URL =
            "https://callforpaper.org";

    private static final String SOURCE_NAME =
            "CallForPaper.org";

    @Override
    public String getSourceName() {
        return SOURCE_NAME;
    }

    @Override
    public List<Conference> scrape() throws IOException {

        List<Conference> conferences = new ArrayList<>();

        Document document = Jsoup.connect(BASE_URL)
                .userAgent("Mozilla/5.0")
                .timeout(30000)
                .get();

        Elements titleLinks = document.select("h3 a[href]");

        for (Element titleLink : titleLinks) {

            String longTitle = titleLink.text().trim();
            String website = titleLink.absUrl("href");

            if (longTitle.isEmpty() || website.isEmpty()) {
                continue;
            }

            // Find a suitable parent containing this conference.
            Element card = titleLink;

            while (card != null) {
                if (card.selectFirst("time") != null
                        && card.selectFirst(
                        "a[href*='/country/']") != null) {
                    break;
                }

                card = card.parent();
            }

            if (card == null) {
                System.out.println(
                        "Could not find conference details: "
                                + longTitle);
                continue;
            }

            // Extract location.
            Element countryElement =
                    card.selectFirst("a[href*='/country/']");

            String location = countryElement != null
                    ? countryElement.text().trim()
                    : "";

            // Extract dates.
            Element timeElement = card.selectFirst("time");

            String deadline = "";

            if (timeElement != null) {
                deadline = timeElement.text().trim();
            }

            // Extract categories.
            Elements categoryElements =
                    card.select("a[href*='/categories/']");

            List<String> categories = new ArrayList<>();

            for (Element categoryElement : categoryElements) {
                String category = categoryElement.text().trim();

                if (!category.isEmpty()
                        && !categories.contains(category)) {
                    categories.add(category);
                }
            }

            // Derive short title.
            String shortTitle = longTitle;
            int colonIndex = longTitle.indexOf(':');

            if (colonIndex > 0) {
                shortTitle =
                        longTitle.substring(0, colonIndex).trim();
            }

            // Create Conference object.
            Conference conference = new Conference(
                    null,
                    shortTitle,
                    longTitle,
                    location,
                    deadline,
                    website,
                    SOURCE_NAME
            );

            conferences.add(conference);
        }
        return conferences;
    }
}
