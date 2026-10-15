//package com.example.smartcfp.service;
//
//import com.example.smartcfp.model.Conference;
//import com.example.smartcfp.repository.ConferenceRepository;
//import com.example.smartcfp.source.ConferenceSource;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//class ConferenceDatabaseServiceTest {
//
//    private ConferenceRepository repository;
//    private ConferenceImportService importService;
//    private ConferenceService conferenceService;
//    private ConferenceSource mockSource;
//
//    @BeforeEach
//    void setUp() {
//        repository = Mockito.mock(ConferenceRepository.class);
//        mockSource = Mockito.mock(ConferenceSource.class);
//        when(mockSource.getSourceName()).thenReturn("MockSource");
//
//        importService = new ConferenceImportService(List.of(mockSource), repository);
//        conferenceService = new ConferenceService(repository);
//    }
//
//    @Test
//    void testImportAll_InsertsNewConferencesAndSkipsDuplicates() throws IOException {
//        Conference conf1 = new Conference(null, "CONF1", "Conference One", "Online", "2026-12-01", "https://conf1.org", "MockSource");
//        Conference conf2 = new Conference(null, "CONF2", "Conference Two", "Paris", "2026-11-01", "https://conf2.org", "MockSource");
//        Conference conf3 = new Conference(null, "CONF3", "Conference Three", "Berlin", "2026-10-01", "", "MockSource");
//
//        when(mockSource.scrape()).thenReturn(List.of(conf1, conf2, conf3));
//        when(repository.findByWebsite("https://conf1.org")).thenReturn(Optional.empty());
//        when(repository.findByWebsite("https://conf2.org")).thenReturn(Optional.of(conf2));
//
//        Map<String, Object> result = importService.importAll();
//
//        verify(repository, times(1)).save(conf1);
//        verify(repository, never()).save(conf2);
//        verify(repository, never()).save(conf3);
//
//        assertEquals(1, result.get("totalInserted"));
//        assertEquals(1, result.get("totalDuplicates"));
//    }
//
//    @Test
//    void testConferenceService_GetAllConferences() {
//        Conference conf1 = new Conference(1L, "C1", "Conf 1", "Rome", "2026-05-01", "https://c1.org", "MockSource");
//        when(repository.findAll()).thenReturn(List.of(conf1));
//
//        List<Conference> conferences = conferenceService.getAllConferences();
//
//        assertEquals(1, conferences.size());
//        assertEquals("C1", conferences.get(0).getShortTitle());
//        verify(repository, times(1)).findAll();
//    }
//
//    @Test
//    void testConferenceService_SearchConferences() {
//        Conference conf1 = new Conference(1L, "AI 2026", "Artificial Intelligence", "Tokyo", "2026-05-01", "https://ai.org", "MockSource");
//        Conference conf2 = new Conference(2L, "SEC 2026", "Security Summit", "London", "2026-06-01", "https://sec.org", "MockSource");
//        when(repository.findAll()).thenReturn(List.of(conf1, conf2));
//
//        List<Conference> searchResults = conferenceService.searchConference("intelligence");
//
//        assertEquals(1, searchResults.size());
//        assertEquals("AI 2026", searchResults.get(0).getShortTitle());
//    }
//
//    @Test
//    void testConferenceEntity_ConstructorsAndProperties() {
//        Conference conf = new Conference();
//        conf.setId(10L);
//        conf.setShortTitle("Test");
//        conf.setLongTitle("Full Test Title");
//        conf.setLocation("NYC");
//        conf.setDeadline("2026-09-01");
//        conf.setWebsite("https://test.org");
//        conf.setSourceName("TestSrc");
//
//        assertEquals(10L, conf.getId());
//        assertEquals("Test", conf.getShortTitle());
//        assertEquals("Full Test Title", conf.getLongTitle());
//        assertEquals("NYC", conf.getLocation());
//        assertEquals("2026-09-01", conf.getDeadline());
//        assertEquals("https://test.org", conf.getWebsite());
//        assertEquals("TestSrc", conf.getSourceName());
//    }
//}
