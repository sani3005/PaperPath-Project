package com.example.smartcfp.service;

import com.example.smartcfp.model.Conference;
import com.example.smartcfp.model.User;
import com.example.smartcfp.repository.ConferenceRepository;
import com.example.smartcfp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConferenceRecommendationTest {

    @Test
    void recommendsConferencesMatchingSavedTopicsAndKnownAliases() {
        User user = new User("Example User", "person@example.com", "encoded-password");
        user.setTopics(Set.of("Artificial Intelligence", "Machine Learning"));
        Conference aiConference = conference("AI and intelligent systems");
        Conference mlConference = conference("Advances in Machine Learning");
        Conference networkingConference = conference("Computer Networks");
        ConferenceRepository conferences = mock(ConferenceRepository.class);
        UserRepository users = mock(UserRepository.class);
        when(users.findByEmail("person@example.com")).thenReturn(Optional.of(user));
        when(conferences.findAllByOrderByIdDesc())
                .thenReturn(List.of(aiConference, mlConference, networkingConference));

        List<Conference> result =
                new ConferenceService(conferences, users)
                        .getRecommendedConferences(" Person@Example.com ");

        assertEquals(List.of(aiConference, mlConference), result);
        verify(users).findByEmail("person@example.com");
    }

    @Test
    void returnsNoRecommendationsWhenUserHasNoTopics() {
        User user = new User("Example User", "person@example.com", "encoded-password");
        UserRepository users = mock(UserRepository.class);
        when(users.findByEmail("person@example.com")).thenReturn(Optional.of(user));

        List<Conference> result = new ConferenceService(
                mock(ConferenceRepository.class), users)
                .getRecommendedConferences("person@example.com");

        assertEquals(List.of(), result);
    }

    @Test
    void rejectsUnknownUser() {
        UserRepository users = mock(UserRepository.class);
        when(users.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> new ConferenceService(mock(ConferenceRepository.class), users)
                        .getRecommendedConferences("missing@example.com")
        );

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
    }

    private Conference conference(String title) {
        return new Conference(
                null, title, title, "Online", "2027-01-15",
                "https://example.org/" + title.replace(' ', '-'), "TestSource");
    }
}
