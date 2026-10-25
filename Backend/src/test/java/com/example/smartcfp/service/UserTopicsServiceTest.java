package com.example.smartcfp.service;

import com.example.smartcfp.model.User;
import com.example.smartcfp.model.UserTopicsResponse;
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

class UserTopicsServiceTest {

    @Test
    void updatesAndReturnsTopicsForNormalizedEmail() {
        User user = new User("Example User", "person@example.com", "encoded-password");
        UserRepository repository = mock(UserRepository.class);
        when(repository.findByEmail("person@example.com")).thenReturn(Optional.of(user));
        when(repository.save(user)).thenReturn(user);

        UserTopicsResponse response = new UserTopicsService(repository)
                .updateTopics(" Person@Example.com ", List.of("Machine Learning", "AI"));

        assertEquals(List.of("AI", "Machine Learning"), response.getTopics());
        assertEquals(Set.of("AI", "Machine Learning"), user.getTopics());
        verify(repository).save(user);
    }

    @Test
    void rejectsEmptyTopicSelection() {
        UserTopicsService service = new UserTopicsService(mock(UserRepository.class));

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.updateTopics("person@example.com", List.of())
        );

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
    }
}
