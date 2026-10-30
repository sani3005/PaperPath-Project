package com.example.smartcfp.service;

import com.example.smartcfp.model.Conference;
import com.example.smartcfp.model.User;
import com.example.smartcfp.repository.ConferenceRepository;
import com.example.smartcfp.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ConferenceService {
    private static final Pattern ISO_DATE =
            Pattern.compile("\\b\\d{4}-\\d{1,2}-\\d{1,2}\\b");
    private static final Pattern NUMERIC_DATE =
            Pattern.compile("\\b(\\d{1,2})/(\\d{1,2})/(\\d{4})\\b");
    private static final Pattern MONTH_FIRST_DATE = Pattern.compile(
            "(?i)\\b(January|February|March|April|May|June|July|August|September|October|November|December"
                    + "|Jan|Feb|Mar|Apr|Jun|Jul|Aug|Sep|Sept|Oct|Nov|Dec)"
                    + "\\s+(\\d{1,2})(?:st|nd|rd|th)?(?:,?\\s+(\\d{4}))?\\b");
    private static final Pattern DAY_FIRST_DATE = Pattern.compile(
            "(?i)\\b(\\d{1,2})(?:st|nd|rd|th)?\\s+"
                    + "(January|February|March|April|May|June|July|August|September|October|November|December"
                    + "|Jan|Feb|Mar|Apr|Jun|Jul|Aug|Sep|Sept|Oct|Nov|Dec)"
                    + ",?\\s+(\\d{4})\\b");
    private static final Pattern YEAR = Pattern.compile("\\b(19|20)\\d{2}\\b");

    private final ConferenceRepository repository;
    private final UserRepository userRepository;

    public ConferenceService(
            ConferenceRepository repository,
            UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public List<Conference> getAllConferences() {
        return sortByDate(repository.findAllByOrderByIdDesc());
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

        List<Conference> recommended = repository.findAllByOrderByIdDesc().stream()
                .filter(conference -> matchesTopics(conference, user.getTopics()))
                .toList();
        return sortByDate(recommended);
    }

    public List<Conference> searchConferences(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllConferences();
        }
        return sortByDate(repository.searchConferences(keyword.trim()));
    }

    private List<Conference> sortByDate(List<Conference> conferences) {
        return conferences.stream()
                .sorted(Comparator.comparing(
                        this::dateForSort,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    private LocalDate dateForSort(Conference conference) {
        LocalDate eventDate = parseDate(conference.getEventDates());
        return eventDate != null ? eventDate : parseDate(conference.getDeadline());
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        Matcher matcher = ISO_DATE.matcher(value);
        if (matcher.find()) {
            try {
                return LocalDate.parse(matcher.group());
            } catch (DateTimeParseException ignored) {
                return null;
            }
        }

        matcher = NUMERIC_DATE.matcher(value);
        if (matcher.find()) {
            try {
                return LocalDate.of(
                        Integer.parseInt(matcher.group(3)),
                        Integer.parseInt(matcher.group(1)),
                        Integer.parseInt(matcher.group(2)));
            } catch (DateTimeException | NumberFormatException ignored) {
                return null;
            }
        }

        int year = findYear(value);
        if (year == 0) {
            return null;
        }

        matcher = MONTH_FIRST_DATE.matcher(value);
        if (matcher.find()) {
            return monthDate(
                    matcher.group(1),
                    matcher.group(2),
                    matcher.group(3),
                    year);
        }

        matcher = DAY_FIRST_DATE.matcher(value);
        if (matcher.find()) {
            return monthDate(
                    matcher.group(2),
                    matcher.group(1),
                    matcher.group(3),
                    year);
        }
        return null;
    }

    private LocalDate monthDate(
            String monthText, String dayText, String dateYear, int fallbackYear) {
        try {
            Month month = Month.valueOf(monthNumber(monthText));
            int year = dateYear == null ? fallbackYear : Integer.parseInt(dateYear);
            return LocalDate.of(year, month, Integer.parseInt(dayText));
        } catch (DateTimeException | NumberFormatException ignored) {
            return null;
        }
    }

    private String monthNumber(String monthText) {
        String normalized = monthText.toLowerCase(Locale.ROOT);
        return switch (normalized.substring(0, Math.min(3, normalized.length()))) {
            case "jan" -> "JANUARY";
            case "feb" -> "FEBRUARY";
            case "mar" -> "MARCH";
            case "apr" -> "APRIL";
            case "may" -> "MAY";
            case "jun" -> "JUNE";
            case "jul" -> "JULY";
            case "aug" -> "AUGUST";
            case "sep" -> "SEPTEMBER";
            case "oct" -> "OCTOBER";
            case "nov" -> "NOVEMBER";
            case "dec" -> "DECEMBER";
            default -> throw new IllegalArgumentException("Unknown month");
        };
    }

    private int findYear(String value) {
        Matcher matcher = YEAR.matcher(value);
        int year = 0;
        while (matcher.find()) {
            year = Integer.parseInt(matcher.group());
        }
        return year;
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
