package com.example.smartcfp.source;

import com.example.smartcfp.model.Conference;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        assertEquals("2027-02-26 - 2027-02-27", conference.getEventDates());
        assertNull(conference.getDeadline());
        assertEquals("IEEE", conference.getSourceName());
        assertEquals(
                "https://conferences.ieee.org/conferences_events/"
                        + "conferences/conferencedetails/12345",
                conference.getWebsite()
        );
    }

    @Test
    void conferenceMonkeyMapsConferenceApiRecordAndIgnoresNonConferenceItems() {
        ConferenceMonkeySource source = new ConferenceMonkeySource();
        Conference conference = source.toConference(java.util.Map.of(
                "inomics:type", "conference",
                "name", "International Conference on Applied Economics",
                "url", "https://inomics.com/conference/example-123",
                "description", "<p>Abstract Submission Deadline: December 1, 2026.</p>",
                "startDate", "2026-11-18T08:00:00Z",
                "endDate", "2026-11-20T22:59:59Z",
                "jobLocation", java.util.Map.of("address", java.util.Map.of(
                        "addressLocality", "New York City",
                        "addressRegion", "New York",
                        "addressCountry", "US"
                )),
                "inomics:disciplineLabels", List.of("Economics", "Finance"),
                "keywords", List.of("conference", "economics"),
                "inomics:moreInfoUrl", "https://example.org/register"
        ));

        assertEquals("International Conference on Applied Economics",
                conference.getLongTitle());
        assertEquals("New York City, New York, US", conference.getLocation());
        assertEquals("2026-11-18 - 2026-11-20", conference.getEventDates());
        assertEquals("December 1, 2026", conference.getDeadline());
        assertEquals("Economics, Finance, conference", conference.getTopics());
        assertEquals("https://example.org/register", conference.getRegistrationUrl());
        assertEquals("ConferenceMonkey", conference.getSourceName());
        assertNull(source.toConference(java.util.Map.of(
                "inomics:type", "job",
                "name", "Not a conference",
                "url", "https://inomics.com/job/example-456"
        )));
    }

    @Test
    void call4PaperParsesConferenceAndJournalCardsWithSubjects() {
        Document document = Jsoup.parse("""
                <a href="/cfp/detail/event/ABC123">
                  <div class="col-xs-12 col-sm-12 col-md-6 top-banner special">
                    <div class="cfp_ad_type">[Events]</div>
                    <h4 class="cfp_ad_title_abbr">AIIM 2026</h4>
                    <div class="cfp_ad_title">International Conference on Artificial Intelligence</div>
                    <div class="cfp_ad_date">2026-11-27 ~ 2026-11-29</div>
                    <div class="cfp_ad_location">China, Guangzhou</div>
                    <div class="cfp_ad_subject">Artificial Intelligence</div>
                  </div>
                </a>
                <a href="/cfp/detail/journal/XYZ789J">
                  <div class="col-xs-12 col-sm-12 col-md-6 top-banner special">
                    <div class="cfp_ad_type">[Journal]</div>
                    <h4 class="cfp_ad_title_abbr">J. Algorithms Complex Syst.</h4>
                    <div class="cfp_ad_title">Journal of Algorithms in Complex Systems</div>
                    <div class="cfp_ad_subject">Databases &amp; Information Systems</div>
                  </div>
                </a>
                """, "https://www.call4paper.com/");

        List<Conference> conferences = new Call4Paper().parse(document);

        assertEquals(2, conferences.size());
        assertEquals("AIIM 2026", conferences.get(0).getShortTitle());
        assertEquals("China, Guangzhou", conferences.get(0).getLocation());
        assertEquals("2026-11-27 ~ 2026-11-29", conferences.get(0).getDeadline());
        assertEquals("Artificial Intelligence", conferences.get(0).getTopics());
        assertEquals(
                "https://www.call4paper.com/cfp/detail/event/ABC123",
                conferences.get(0).getWebsite()
        );
        assertEquals("J. Algorithms Complex Syst.", conferences.get(1).getShortTitle());
        assertEquals("Not available", conferences.get(1).getDeadline());
        assertEquals("Databases & Information Systems", conferences.get(1).getTopics());
        assertEquals("Call4Paper.com", conferences.get(1).getSourceName());
    }

    @Test
    void call4PaperExtractsDirectRegistrationUrlFromEventDetailPage() {
        Document document = Jsoup.parse("""
                <ul class="event-desc">
                  <li>URL: <a class="city-link" href="https://registration.example.org/aiim">
                    https://registration.example.org/aiim
                  </a></li>
                  <li class="address">Event Date: 2026-11-27 ~ 2026-11-29</li>
                  <li>Location: <a href="/listByCity">Guangzhou, China</a></li>
                </ul>
                """, "https://www.call4paper.com/detail/event/ABC123");

        assertEquals(
                "https://registration.example.org/aiim",
                new Call4Paper().parseRegistrationUrl(document)
        );
    }

    @Test
    void allConferenceAlertParsesCardTitleLocationAndEventDatesSeparately() {
        Document document = Jsoup.parse("""
                <ul>
                  <li class="event-card">
                    <div class="text-lg font-semibold">
                      <a href="https://www.allconferencealert.com/conference/smart-cities-boston-3023186">
                        <p>
                          <span><i class="fas fa-map-marker-alt"></i><span>Boston, USA</span></span>
                          <span><i class="fas fa-calendar-alt"></i>
                            <time datetime="2026-11-02">02 Nov 2026</time>
                            <time datetime="2026-11-03">03 Nov 2026</time>
                          </span>
                        </p>
                        International Conference on Smart Cities and AI Applications (ICSCAA)
                      </a>
                    </div>
                  </li>
                </ul>
                """, "https://www.allconferencealert.com/");

        List<Conference> conferences = new AllConferenceAlertSource().parseListing(document);

        assertEquals(1, conferences.size());
        Conference conference = conferences.get(0);
        assertEquals("ICSCAA", conference.getShortTitle());
        assertEquals(
                "International Conference on Smart Cities and AI Applications (ICSCAA)",
                conference.getLongTitle()
        );
        assertEquals("Boston, USA", conference.getLocation());
        assertEquals("02 Nov 2026 - 03 Nov 2026", conference.getEventDates());
        assertNull(conference.getDeadline());
    }

    @Test
    void allConferenceAlertParsesDescriptionRegistrationAndSubmissionDeadline() {
        Document document = Jsoup.parse("""
                <h1>International Conference on AI Applications (ICAIA)</h1>
                <div class="mt-4 flex flex-col sm:flex-row sm:items-center gap-y-3 sm:gap-x-6">
                  <div class="flex items-center gap-3 mr-2">
                    <i class="fas fa-calendar-alt"></i>
                    <span>November 2, 2026</span> - <span>November 3, 2026</span>
                  </div>
                  <div class="flex items-center gap-3">
                    <i class="fas fa-map-marker-alt"></i><span>Liverpool, UK</span>
                  </div>
                </div>
                <p>Paper Submission Deadline: December 1, 2026</p>
                <a href="https://registration.example.org/icaia">Register Now</a>
                <div class="prose"><p>Researchers will discuss applied artificial intelligence.</p></div>
                """, "https://www.allconferencealert.com/conference/icaia");
        Conference conference = new Conference(
                null, "ICAIA", "ICAIA", "Unknown", null,
                "https://www.allconferencealert.com/conference/icaia",
                "AllConferenceAlert"
        );

        new AllConferenceAlertSource().mergeDetails(conference, document);

        assertEquals("ICAIA", conference.getShortTitle());
        assertEquals("Liverpool, UK", conference.getLocation());
        assertEquals("November 2, 2026 - November 3, 2026", conference.getEventDates());
        assertEquals("December 1, 2026", conference.getDeadline());
        assertEquals("https://registration.example.org/icaia", conference.getRegistrationUrl());
        assertEquals(
                "Researchers will discuss applied artificial intelligence.",
                conference.getDescription()
        );
    }
}
