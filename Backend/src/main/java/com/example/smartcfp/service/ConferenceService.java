package com.example.smartcfp.service;

import com.example.smartcfp.model.Conference;
import com.example.smartcfp.model.User;
import com.example.smartcfp.repository.ConferenceRepository;
import com.example.smartcfp.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class ConferenceService {
    private final ConferenceRepository repository;
    private final UserRepository userRepository;

    public ConferenceService(
            ConferenceRepository repository,
            UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public List<Conference> getAllConferences() {
        return repository.findAllByOrderByIdDesc();
    }

    @Transactional(readOnly = true)
    public List<Conference> getRecommendedConferences(String email) {
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Email is required");
        }

        User user = userRepository.findByEmail(
                        email.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        if (user.getTopics().isEmpty()) {
            return List.of();
        }

        return repository.findAllByOrderByIdDesc().stream()
                .filter(conference -> matchesTopics(conference, user.getTopics()))
                .toList();
    }

    public List<Conference> searchConferences(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllConferences();
        }
        return repository.searchConferences(keyword.trim());
    }

    private boolean matchesTopics(Conference conference, Set<String> topics) {
        String conferenceText = normalize(
                nullToEmpty(conference.getShortTitle()) + " "
                        + nullToEmpty(conference.getLongTitle()) + " "
                        + nullToEmpty(conference.getTopics()));
        if (conferenceText.isEmpty()) {
            return false;
        }

        for (String topic : topics) {
            for (String topicPart : topic.split("[&,/]")) {
                String normalizedTopic = normalize(topicPart);
                if (normalizedTopic.isEmpty()) {
                    continue;
                }

                if (containsPhrase(conferenceText, normalizedTopic)) {
                    return true;
                }

                for (String alias : aliasesFor(normalizedTopic)) {
                    if (containsPhrase(conferenceText, alias)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Set<String> aliasesFor(String topic) {
        return switch (topic) {
            case "ai" -> Set.of("artificial intelligence");
            case "artificial intelligence" -> Set.of("ai");
            case "ml" -> Set.of("machine learning");
            case "machine learning" -> Set.of("ml");
            case "nlp" -> Set.of("natural language processing");
            case "natural language processing" -> Set.of("nlp");
            default -> Set.of();
        };
    }

    private boolean containsPhrase(String text, String phrase) {
        return (" " + text + " ").contains(" " + phrase + " ");
    }

    private String normalize(String value) {
        return value.toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{L}\\p{N}]+", " ")
                .trim()
                .replaceAll("\\s+", " ");
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
