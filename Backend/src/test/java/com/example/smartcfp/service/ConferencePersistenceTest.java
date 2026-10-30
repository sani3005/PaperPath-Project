package com.example.smartcfp.service;

import com.example.smartcfp.model.Conference;
import com.example.smartcfp.repository.ConferenceRepository;
import com.example.smartcfp.repository.UserRepository;
import com.example.smartcfp.source.ConferenceSource;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConferencePersistenceTest {

    @Test
    void importSavesNewConferencesAndListingReadsFromRepository() throws Exception {
        Conference conference = new Conference(
                null,
                "Example Conference",
                "Example Conference 2027",
                "Paris",
                "2027-01-15",
                "https://example.org/conference",
                "TestSource"
        );
        ConferenceSource source = mock(ConferenceSource.class);
        ConferenceRepository repository = mock(ConferenceRepository.class);
        when(source.getSourceName()).thenReturn("TestSource");
        when(source.scrape()).thenReturn(List.of(conference));
        when(repository.findByWebsite(conference.getWebsite()))
                .thenReturn(Optional.empty());

        ConferenceImportService importService =
                new ConferenceImportService(List.of(source), repository);
        importService.importAll();

        verify(repository).save(conference);

        when(repository.findAllByOrderByIdDesc()).thenReturn(List.of(conference));
        ConferenceService conferenceService =
                new ConferenceService(repository, mock(UserRepository.class));
        assertEquals(List.of(conference), conferenceService.getAllConferences());
        verify(repository).findAllByOrderByIdDesc();
    }

    @Test
    void importRefreshesExistingConferenceTopics() throws Exception {
        Conference scraped = new Conference(
                null,
                "AI 2027",
                "International Conference on Artificial Intelligence",
                "Tokyo",
                "2027-01-15",
                "https://example.org/ai-2027",
                "Call4Paper.com",
                "Artificial Intelligence",
                "https://registration.example.org/ai-2027",
                "2027-02-10 - 2027-02-12",
                "Researchers present new results in artificial intelligence."
        );
        Conference existing = new Conference(
                3L,
                "AI 2027",
                "International Conference on Artificial Intelligence",
                "Tokyo",
                "2027-01-15",
                scraped.getWebsite(),
                "Call4Paper.com"
        );
        ConferenceSource source = mock(ConferenceSource.class);
        ConferenceRepository repository = mock(ConferenceRepository.class);
        when(source.getSourceName()).thenReturn("Call4Paper.com");
        when(source.scrape()).thenReturn(List.of(scraped));
        when(repository.findByWebsite(scraped.getWebsite()))
                .thenReturn(Optional.of(existing));

        var report = new ConferenceImportService(List.of(source), repository)
                .importSource("call4paper.com");

        assertEquals("Artificial Intelligence", existing.getTopics());
        assertEquals(
                "https://registration.example.org/ai-2027",
                existing.getRegistrationUrl()
        );
        assertEquals("2027-02-10 - 2027-02-12", existing.getEventDates());
        assertEquals(
                "Researchers present new results in artificial intelligence.",
                existing.getDescription()
        );
        assertEquals(1, report.get("totalUpdated"));
        verify(repository).save(existing);
    }

    @Test
    void conferenceListingsAreSortedByNewestEventDateWithUndatedLast() {
        Conference older = conferenceWithEventDate(1L, "2026-11-10 - 2026-11-12");
        Conference newer = conferenceWithEventDate(
                2L, "November 2 - November 3, 2027");
        Conference latest = conferenceWithEventDate(
                3L, "2027-12-01 - 2027-12-03");
        Conference undated = conferenceWithEventDate(4L, null);
        ConferenceRepository repository = mock(ConferenceRepository.class);
        when(repository.findAllByOrderByIdDesc())
                .thenReturn(List.of(undated, older, newer, latest));

        List<Conference> result = new ConferenceService(
                repository, mock(UserRepository.class)).getAllConferences();

        assertEquals(List.of(latest, newer, older, undated), result);
    }

    @Test
    void importReportIncludesWhySourceScrapingFailed() throws Exception {
        ConferenceSource source = mock(ConferenceSource.class);
        ConferenceRepository repository = mock(ConferenceRepository.class);
        when(source.getSourceName()).thenReturn("AllConferenceAlert");
        when(source.scrape()).thenThrow(new IOException(
                "HTTP error fetching URL. Status=403, URL=https://www.allconferencealert.com/"));

        var report = new ConferenceImportService(List.of(source), repository)
                .importSource("AllConferenceAlert");

        @SuppressWarnings("unchecked")
        var sourceReport = (java.util.Map<String, Object>) report.get("AllConferenceAlert");
        assertEquals("FAILED", sourceReport.get("status"));
        assertEquals(
                "HTTP error fetching URL. Status=403, URL=https://www.allconferencealert.com/",
                sourceReport.get("message")
        );
    }

    private Conference conferenceWithEventDate(Long id, String eventDate) {
        Conference conference = new Conference(
                id, "Conference " + id, "Conference " + id,
                "Online", null, "https://example.org/" + id, "TestSource");
        conference.setEventDates(eventDate);
        return conference;
    }
}
