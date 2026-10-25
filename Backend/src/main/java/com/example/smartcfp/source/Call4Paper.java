package com.example.smartcfp.source;

import com.example.smartcfp.model.Conference;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class Call4Paper implements ConferenceSource {

    private static final String BASE_URL = "https://www.call4paper.com/";
    private static final String SOURCE_NAME = "Call4Paper.com";
    private static final Logger logger =
            LoggerFactory.getLogger(Call4Paper.class);

    @Override
    public String getSourceName() {
        return SOURCE_NAME;
    }

    @Override
    public List<Conference> scrape() throws IOException {
        Document document = Jsoup.connect(BASE_URL)
                .userAgent("Mozilla/5.0")
                .timeout(30000)
                .get();
        List<Conference> conferences = parse(document);
        for (Conference conference : conferences) {
            if (conference.getWebsite().contains("/event/")) {
                try {
                    conference.setRegistrationUrl(
                            fetchRegistrationUrl(conference.getWebsite()));
                } catch (IOException ex) {
                    logger.warn("Could not fetch registration link for {}",
                            conference.getShortTitle(), ex);
                }
            }
        }
        return conferences;
    }

    List<Conference> parse(Document document) {
        List<Conference> conferences = new ArrayList<>();

        for (Element card : document.select("div.top-banner.special")) {
            Element link = card.closest("a[href]");
            Element titleElement = card.selectFirst(".cfp_ad_title");
            if (link == null || titleElement == null) {
                continue;
            }

            String title = titleElement.text().trim();
            String website = link.absUrl("href");
            if (title.isBlank() || website.isBlank()) {
                continue;
            }

            Element abbreviationElement = card.selectFirst(".cfp_ad_title_abbr");
            String shortTitle = abbreviationElement == null
                    ? title
                    : abbreviationElement.text().trim();
            if (shortTitle.isBlank()) {
                shortTitle = title;
            }

            String subjects = String.join(", ",
                    card.select(".cfp_ad_subject").eachText());

            conferences.add(new Conference(
                    null,
                    shortTitle,
                    title,
                    textOrUnavailable(textOf(card, ".cfp_ad_location")),
                    textOrUnavailable(textOf(card, ".cfp_ad_date")),
                    website,
                    SOURCE_NAME,
                    subjects
            ));
        }

        return conferences;
    }

    String parseRegistrationUrl(Document document) {
        for (Element listItem : document.select(".event-desc li")) {
            if (!listItem.ownText().trim().toLowerCase(Locale.ROOT).startsWith("url:")) {
                continue;
            }

            Element link = listItem.selectFirst("a[href]");
            if (link == null) {
                return null;
            }

            String url = link.absUrl("href").trim();
            if (url.isBlank()) {
                url = link.attr("href").trim();
            }
            if (url.startsWith("https://") || url.startsWith("http://")) {
                return url;
            }
        }
        return null;
    }

    private String fetchRegistrationUrl(String detailUrl) throws IOException {
        Document detailPage = Jsoup.connect(detailUrl)
                .userAgent("Mozilla/5.0")
                .timeout(15000)
                .get();
        return parseRegistrationUrl(detailPage);
    }

    private String textOf(Element card, String selector) {
        Element element = card.selectFirst(selector);
        return element == null ? "" : element.text().trim();
    }

    private String textOrUnavailable(String value) {
        return value.isBlank() ? "Not available" : value;
    }
}
