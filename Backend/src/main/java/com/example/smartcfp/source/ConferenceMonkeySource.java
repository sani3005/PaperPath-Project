package com.example.smartcfp.source;

import com.example.smartcfp.model.Conference;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ConferenceMonkeySource implements ConferenceSource {

    private static final String API_URL =
            "https://inomics.com/api/v1/listings/conferences";
    private static final int MAX_PAGES = 100;
    private static final Pattern DEADLINE = Pattern.compile(
            "(?i)(?:abstract|full\\s+paper|paper|submission)\\s+"
                    + "(?:submission\\s+)?deadline\\s*[:\\-]?\\s*([^.;\\n]+)"
    );

    private final RestClient restClient = RestClient.create();

    @Override
    public String getSourceName() {
        return "ConferenceMonkey";
    }

    @Override
    public List<Conference> scrape() throws IOException {
        Map<String, Conference> conferencesByUrl = new LinkedHashMap<>();
        int pagesWithoutNewConferences = 0;

        for (int page = 0; page < MAX_PAGES; page++) {
            Map<?, ?> response = fetchPage(page);
            Object itemListValue = response.get("itemListElement");
            if (!(itemListValue instanceof List<?> items)) {
                throw new IOException(
                        "ConferenceMonkey API response has no itemListElement list");
            }
            if (items.isEmpty()) {
                break;
            }

            int previousSize = conferencesByUrl.size();
            for (Object item : items) {
                if (item instanceof Map<?, ?> record) {
                    Conference conference = toConference(record);
                    if (conference != null) {
                        conferencesByUrl.putIfAbsent(
                                conference.getWebsite(), conference);
                    }
                }
            }

            if (conferencesByUrl.size() == previousSize) {
                pagesWithoutNewConferences++;
                if (pagesWithoutNewConferences >= 2) {
                    break;
                }
            } else {
                pagesWithoutNewConferences = 0;
            }

            if (page == MAX_PAGES - 1) {
                throw new IOException(
                        "ConferenceMonkey API pagination exceeded the page limit");
            }
        }

        return new ArrayList<>(conferencesByUrl.values());
    }

    private Map<?, ?> fetchPage(int page) throws IOException {
        try {
            Map<?, ?> response = restClient.get()
                    .uri(API_URL + "?page=" + page)
                    .retrieve()
                    .body(Map.class);
            if (response == null) {
                throw new IOException("ConferenceMonkey API returned an empty response");
            }
            return response;
        } catch (RestClientException ex) {
            throw new IOException("Could not fetch ConferenceMonkey listings", ex);
        }
    }

    Conference toConference(Map<?, ?> item) {
        if (!"conference".equalsIgnoreCase(stringValue(item.get("inomics:type")))) {
            return null;
        }

        String title = firstText(item.get("name"), item.get("title"));
        String website = stringValue(item.get("url"));
        if (title.isBlank() || website.isBlank()) {
            return null;
        }

        String descriptionHtml = stringValue(item.get("description"));
        String description = Jsoup.parse(descriptionHtml).text().trim();
        if (description.length() > 5000) {
            description = description.substring(0, 5000);
        }

        String location = location(item.get("jobLocation"));
        String topics = joinValues(item.get("inomics:disciplineLabels"),
                item.get("keywords"));
        String startDate = dateValue(item.get("startDate"));
        String endDate = dateValue(item.get("endDate"));
        String eventDates = startDate.isBlank()
                ? endDate
                : endDate.isBlank() || startDate.equals(endDate)
                ? startDate
                : startDate + " - " + endDate;

        return new Conference(
                null,
                title,
                title,
                emptyToNull(location),
                deadline(description),
                website,
                getSourceName(),
                emptyToNull(topics),
                emptyToNull(stringValue(item.get("inomics:moreInfoUrl"))),
                emptyToNull(eventDates),
                emptyToNull(description)
        );
    }

    private String location(Object locationValue) {
        if (!(locationValue instanceof Map<?, ?> place)
                || !(place.get("address") instanceof Map<?, ?> address)) {
            return "";
        }

        String city = stringValue(address.get("addressLocality"));
        String region = stringValue(address.get("addressRegion"));
        String country = stringValue(address.get("addressCountry"));
        String locality = city.isBlank() || city.equalsIgnoreCase(region)
                ? (city.isBlank() ? region : city)
                : region.isBlank() ? city : city + ", " + region;
        if (locality.isBlank()) {
            return country;
        }
        return country.isBlank() ? locality : locality + ", " + country;
    }

    private String joinValues(Object... values) {
        List<String> results = new ArrayList<>();
        for (Object value : values) {
            if (value instanceof List<?> list) {
                for (Object entry : list) {
                    String text = stringValue(entry);
                    addUnique(results, text);
                }
            } else {
                String text = stringValue(value);
                addUnique(results, text);
            }
        }
        return String.join(", ", results);
    }

    private void addUnique(List<String> results, String value) {
        if (!value.isBlank() && results.stream()
                .noneMatch(existing -> existing.equalsIgnoreCase(value))) {
            results.add(value);
        }
    }

    private String deadline(String description) {
        Matcher matcher = DEADLINE.matcher(description);
        return matcher.find() ? matcher.group(1).trim() : "";
    }

    private String dateValue(Object value) {
        String date = stringValue(value);
        if (date.isBlank()) {
            return "";
        }
        try {
            return OffsetDateTime.parse(date).toLocalDate().toString();
        } catch (DateTimeParseException ex) {
            return date;
        }
    }

    private String firstText(Object first, Object second) {
        String value = stringValue(first);
        return value.isBlank() ? stringValue(second) : value;
    }

    private String stringValue(Object value) {
        return value == null ? "" : value.toString().trim();
    }

    private String emptyToNull(String value) {
        return value.isBlank() ? null : value;
    }
}
