package com.example.smartcfp.source;

import com.example.smartcfp.model.Conference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AllConferenceAlertSource implements ConferenceSource {

    private static final String LISTING_URL = "https://www.allconferencealert.com/";
    private static final String SOURCE_NAME = "AllConferenceAlert";
    private static final Logger logger =
            LoggerFactory.getLogger(AllConferenceAlertSource.class);
    private static final Pattern ACRONYM = Pattern.compile("\\(([^()]+)\\)\\s*$");
    private static final Pattern DEADLINE = Pattern.compile(
            "(?i)(?:(?:paper\\s+)?(?:submission|abstract|registration|early\\s*bird|conference)\\s+)?"
                    + "deadline\\s*[:\\-]?\\s*(.*)$");

    @Override
    public String getSourceName() {
        return SOURCE_NAME;
    }

    @Override
    public List<Conference> scrape() throws IOException {
        Document listing = Jsoup.connect(LISTING_URL)
                .userAgent("Mozilla/5.0")
                .timeout(30000)
                .get();

        List<Conference> conferences = parseListing(listing);
        for (Conference conference : conferences) {
            try {
                Document detail = Jsoup.connect(conference.getWebsite())
                        .userAgent("Mozilla/5.0")
                        .timeout(15000)
                        .get();
                mergeDetails(conference, detail);
            } catch (IOException ex) {
                logger.warn("Could not fetch AllConferenceAlert details for {}",
                        conference.getWebsite(), ex);
            }
        }
        return conferences;
    }

    List<Conference> parseListing(Document document) {
        List<Conference> conferences = new ArrayList<>();
        for (Element card : document.select("li.event-card")) {
            Element link = card.selectFirst("div.text-lg a[href], a[href*='/conference/']");
            if (link == null) {
                continue;
            }

            String website = absoluteUrl(link, "href");
            String title = link.ownText().trim();
            if (title.isBlank()) {
                title = link.text().trim();
            }
            if (website.isBlank() || title.isBlank()) {
                continue;
            }

            String shortTitle = title;
            Matcher acronym = ACRONYM.matcher(title);
            if (acronym.find()) {
                shortTitle = acronym.group(1).trim();
            }

            String location = iconValue(card, "fa-map-marker-alt");
            String eventDates = dates(card);
            String deadline = findDeadline(card);

            conferences.add(new Conference(
                    null,
                    shortTitle,
                    title,
                    textOrNull(location),
                    textOrNull(deadline),
                    website,
                    SOURCE_NAME,
                    topics(card),
                    null,
                    textOrNull(eventDates),
                    null
            ));
        }
        return conferences;
    }

    void mergeDetails(Conference conference, Document detail) {
        Element title = detail.selectFirst("h1");
        if (title != null && !title.text().isBlank()) {
            String fullTitle = title.text().trim();
            conference.setLongTitle(fullTitle);
            Matcher acronym = ACRONYM.matcher(fullTitle);
            conference.setShortTitle(acronym.find()
                    ? acronym.group(1).trim()
                    : fullTitle);
        }

        String location = iconValue(detail, "fa-map-marker-alt");
        if (hasText(location)) {
            conference.setLocation(location);
        }

        String eventDates = dates(detail);
        if (hasText(eventDates)) {
            conference.setEventDates(eventDates);
        }

        String deadline = findDeadline(detail);
        if (hasText(deadline)) {
            conference.setDeadline(deadline);
        }

        String registrationUrl = registrationUrl(detail);
        if (hasText(registrationUrl)) {
            conference.setRegistrationUrl(registrationUrl);
        }

        String description = detail.select(".prose p").eachText().stream()
                .filter(AllConferenceAlertSource::hasText)
                .reduce((first, next) -> first + "\n\n" + next)
                .orElse("");
        if (!description.isBlank()) {
            conference.setDescription(description.length() > 5000
                    ? description.substring(0, 5000)
                    : description);
        }

        String detailTopics = topics(detail);
        if (hasText(detailTopics)) {
            conference.setTopics(detailTopics);
        }
    }

    private String dates(Element root) {
        Elements timeElements = root.select("time");
        if (!timeElements.isEmpty()) {
            List<String> values = timeElements.eachText().stream()
                    .map(String::trim)
                    .filter(AllConferenceAlertSource::hasText)
                    .toList();
            if (!values.isEmpty()) {
                return String.join(" - ", values);
            }
            values = timeElements.eachAttr("datetime").stream()
                    .map(String::trim)
                    .filter(AllConferenceAlertSource::hasText)
                    .toList();
            if (!values.isEmpty()) {
                return String.join(" - ", values);
            }
        }

        Element calendar = root.selectFirst("i.fa-calendar-alt");
        if (calendar != null) {
            Element value = calendar.closest("div.flex.items-center.gap-3");
            if (value != null) {
                return value.text().trim();
            }
        }
        return "";
    }

    private String iconValue(Element root, String iconClass) {
        Element icon = root.selectFirst("i." + iconClass);
        if (icon == null) {
            return "";
        }
        Element value = icon.closest("div.flex.items-center.gap-3");
        if (value == null) {
            value = icon.parent();
        }
        return value == null ? "" : value.text().trim();
    }

    private String findDeadline(Element root) {
        for (Element element : root.select("[class*=deadline], [id*=deadline], li, p, dt, dd")) {
            String text = element.text().trim();
            if (text.isBlank() || text.length() > 300) {
                continue;
            }
            Matcher matcher = DEADLINE.matcher(text);
            if (!matcher.find()) {
                continue;
            }

            String value = matcher.group(1).trim();
            if (!value.isBlank()) {
                return value;
            }
            Element sibling = element.nextElementSibling();
            if (sibling != null && !sibling.text().isBlank()) {
                return sibling.text().trim();
            }
            if (element.parent() != null) {
                sibling = element.parent().nextElementSibling();
                if (sibling != null && !sibling.text().isBlank()) {
                    return sibling.text().trim();
                }
            }
        }
        return "";
    }

    private String registrationUrl(Element root) {
        for (Element link : root.select("a[href]")) {
            String label = link.text().trim().toLowerCase(Locale.ROOT);
            if (!label.contains("register")) {
                continue;
            }
            String url = absoluteUrl(link, "href");
            if (url.startsWith("https://") || url.startsWith("http://")) {
                return url;
            }
        }
        return "";
    }

    private String topics(Element root) {
        return String.join(", ", root.select(
                "[rel=tag], .event-tags a, .tags a, a[href*='/topic/']").eachText());
    }

    private String absoluteUrl(Element element, String attribute) {
        String url = element.absUrl(attribute).trim();
        return url.isBlank() ? element.attr(attribute).trim() : url;
    }

    private String textOrNull(String value) {
        return hasText(value) ? value : null;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
