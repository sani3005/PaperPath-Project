package com.example.smartcfp.source;

import com.example.smartcfp.model.Conference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class Ieee implements ConferenceSource {

    private static final String API_URL =
            "https://conference-api.ieee.org/public/searchfacet";
    private static final String SEARCH_TERM = "202";
    private static final String CONFERENCE_URL =
            "https://conferences.ieee.org/conferences_events/"
                    + "conferences/conferencedetails/";

    private final RestClient restClient = RestClient.create();

    @Override
    public String getSourceName() {
        return "IEEE";
    }

    @Override
    public List<Conference> scrape() throws IOException {
        List<Conference> conferences = new ArrayList<>();
        int page = 0;
        int totalResults;
        int pageSize;

        do {
            Map<?, ?> entity = fetchPage(page);
            Object resultValue = entity.get("results");
            if (!(resultValue instanceof List<?> results)) {
                throw new IOException("IEEE search response has no results list");
            }

            totalResults = numberValue(entity.get("totalResults"), results.size());
            pageSize = numberValue(entity.get("pageSize"), results.size());
            if (pageSize <= 0) {
                throw new IOException("IEEE search response has an invalid page size");
            }

            for (Object result : results) {
                if (result instanceof Map<?, ?> event) {
                    Conference conference = toConference(event);
                    if (conference != null) {
                        conferences.add(conference);
                    }
                }
            }

            if (results.isEmpty()) {
                break;
            }
            page++;
        } while ((long) page * pageSize < totalResults);

        return conferences;
    }

    private Map<?, ?> fetchPage(int page) throws IOException {
        String form = "q=" + URLEncoder.encode(SEARCH_TERM, StandardCharsets.UTF_8)
                + "&pos=" + page;

        try {
            Map<?, ?> response = restClient.post()
                    .uri(API_URL)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(Map.class);

            if (response == null || !(response.get("entity") instanceof Map<?, ?> entity)) {
                throw new IOException("IEEE search API returned an invalid response");
            }
            return entity;
        } catch (RestClientException ex) {
            throw new IOException("Could not fetch IEEE conference search results", ex);
        }
    }

    Conference toConference(Map<?, ?> event) {
        String title = stringValue(event.get("eventTitle"));
        String eventId = stringValue(event.get("eventId"));
        if (title.isBlank() || eventId.isBlank()) {
            return null;
        }

        String startDate = stringValue(event.get("startDate"));
        String endDate = stringValue(event.get("endDate"));
        String eventDate = startDate.isBlank()
                ? "Not available"
                : endDate.isBlank() || startDate.equals(endDate)
                ? startDate
                : startDate + " - " + endDate;

        String location = "Not available";
        if (event.get("location") instanceof Map<?, ?> address) {
            String city = stringValue(address.get("city"));
            String region = stringValue(address.get("region"));
            String country = stringValue(address.get("country"));
            String locality = region.isBlank() || city.equalsIgnoreCase(region)
                    ? city
                    : city.isBlank() ? region : city + ", " + region;
            String fullLocation = locality.isBlank()
                    ? country
                    : country.isBlank() ? locality : locality + ", " + country;
            if (!fullLocation.isBlank()) {
                location = fullLocation;
            }
        }

        String website = CONFERENCE_URL
                + URLEncoder.encode(eventId, StandardCharsets.UTF_8);
        return new Conference(
                null,
                title,
                title,
                location,
                eventDate,
                website,
                getSourceName()
        );
    }

    private static int numberValue(Object value, int fallback) {
        return value instanceof Number number ? number.intValue() : fallback;
    }

    private static String stringValue(Object value) {
        return value == null ? "" : value.toString().trim();
    }
}
