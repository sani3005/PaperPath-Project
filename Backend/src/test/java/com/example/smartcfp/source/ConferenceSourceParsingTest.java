package com.example.smartcfp.source;

import com.example.smartcfp.model.Conference;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConferenceSourceParsingTest {

    @Test
    void wikiCfpStoresConferenceUrlAndCorrectMetadata() {
        Document document = Jsoup.parse("""
                <table>
                  <tr><td>1</td><td><a href="/cfp/servlet/event.showcfp?eventid=7">AI 2027</a></td></tr>
                  <tr><td>May 1-2, 2027</td><td>Tokyo</td><td>Jan 10, 2027</td></tr>
                </table>
                """, "https://www.wikicfp.com/cfp/");

        List<Conference> conferences = new WikiCfpSource().parse(document);

        assertEquals(1, conferences.size());
        Conference conference = conferences.get(0);
        assertEquals("Tokyo", conference.getLocation());
        assertEquals("Jan 10, 2027", conference.getDeadline());
        assertEquals(
                "https://www.wikicfp.com/cfp/servlet/event.showcfp?eventid=7",
                conference.getWebsite()
        );
        assertEquals("WikiCFP", conference.getSourceName());
    }

    @Test
    void callForPaperKeepsConferenceWhenOptionalDetailsAreMissing() {
        Document document = Jsoup.parse("""
                <h3><a href="/conference/example-2027">Example: Conference 2027</a></h3>
                """, "https://callforpaper.org/");

        List<Conference> conferences =
                new CallForPaperSource().parse(document);

        assertEquals(1, conferences.size());
        Conference conference = conferences.get(0);
        assertEquals("Example", conference.getShortTitle());
        assertEquals("Not available", conference.getLocation());
        assertEquals("Not available", conference.getDeadline());
        assertEquals(
                "https://callforpaper.org/conference/example-2027",
                conference.getWebsite()
        );
        assertEquals("CallForPaper.org", conference.getSourceName());
    }

    @Test
    void ieeeMapsSearchApiConferenceIntoDatabaseEntity() {
        Conference conference = new Ieee().toConference(java.util.Map.of(
                "eventId", 12345,
                "eventTitle", "2027 AI-Driven Smart Healthcare",
                "startDate", "2027-02-26",
                "endDate", "2027-02-27",
                "location", java.util.Map.of(
                        "city", "Kolkata",
                        "region", "",
                        "country", "India"
                )
        ));

        assertEquals("2027 AI-Driven Smart Healthcare", conference.getShortTitle());
        assertEquals("Kolkata, India", conference.getLocation());
        assertEquals("2027-02-26 - 2027-02-27", conference.getDeadline());
        assertEquals("IEEE", conference.getSourceName());
        assertEquals(
                "https://conferences.ieee.org/conferences_events/"
                        + "conferences/conferencedetails/12345",
                conference.getWebsite()
        );
    }
}
