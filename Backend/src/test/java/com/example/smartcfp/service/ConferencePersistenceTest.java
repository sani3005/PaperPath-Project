package com.example.smartcfp.service;

import com.example.smartcfp.model.Conference;
import com.example.smartcfp.repository.ConferenceRepository;
import com.example.smartcfp.source.ConferenceSource;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

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
        ConferenceService conferenceService = new ConferenceService(repository);
        assertEquals(List.of(conference), conferenceService.getAllConferences());
        verify(repository).findAllByOrderByIdDesc();
    }
}
