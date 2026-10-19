package com.example.paperpath;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import org.junit.Test;

public class ConferenceApiMappingTest {

    @Test
    public void mapsConferenceApiFieldsToCardData() {
        String json = "{\"id\":17,\"shortTitle\":\"ICAI\","
                + "\"longTitle\":\"International Conference on AI\","
                + "\"location\":\"Vancouver, Canada\","
                + "\"deadline\":\"2027-01-10\","
                + "\"website\":\"https://example.org\","
                + "\"sourceName\":\"WikiCFP\"}";

        Conference conference = new Gson().fromJson(json, Conference.class);

        assertEquals("17", conference.getId());
        assertEquals("ICAI", conference.getAcronym());
        assertEquals("International Conference on AI", conference.getFullName());
        assertEquals("Vancouver, Canada", conference.getVenue());
        assertEquals("2027-01-10", conference.getSubmissionDeadline());
        assertEquals("https://example.org", conference.getOfficialUrl());
        assertEquals("Submission deadline: 2027-01-10", conference.getConfDates());
        assertEquals("WikiCFP", conference.getTags()[0]);
    }
}
