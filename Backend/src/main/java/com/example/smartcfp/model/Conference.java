package com.example.smartcfp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "conference")
public class Conference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String shortTitle;

    @Column(length = 1500)
    private String longTitle;

    @Column(length = 1500)
    private String location;

    @Column(name = "deadline")
    private String deadline;

    @Column(length = 500)
    private String eventDates;

    @Column(length = 5000)
    private String description;

    @Column(name = "website")
    private String website;

    @Column(name = "sourceName")
    private String sourceName;

    @Column(length = 1500)
    private String topics;

    @Column(length = 1500)
    private String registrationUrl;


    protected Conference() {
    }

    // Constructor for creating conference objects manually
    public Conference(Long id, String shortTitle, String longTitle,
                      String location, String deadline,
                      String website, String sourceName) {
        this.id = id;
        this.shortTitle = shortTitle;
        this.longTitle = longTitle;
        this.location = location;
        this.deadline = deadline;
        this.website = website;
        this.sourceName = sourceName;
    }

    public Conference(Long id, String shortTitle, String longTitle,
                      String location, String deadline, String website,
                      String sourceName, String topics) {
        this(id, shortTitle, longTitle, location, deadline, website, sourceName);
        this.topics = topics;
    }

    public Conference(Long id, String shortTitle, String longTitle,
                      String location, String deadline, String website,
                      String sourceName, String topics, String registrationUrl) {
        this(id, shortTitle, longTitle, location, deadline, website, sourceName, topics);
        this.registrationUrl = registrationUrl;
    }

    public Conference(Long id, String shortTitle, String longTitle,
                      String location, String deadline, String website,
                      String sourceName, String topics, String registrationUrl,
                      String eventDates, String description) {
        this(id, shortTitle, longTitle, location, deadline, website,
                sourceName, topics, registrationUrl);
        this.eventDates = eventDates;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getLongTitle() {
        return longTitle;
    }

    public void setLongTitle(String longTitle) {
        this.longTitle = longTitle;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getEventDates() {
        return eventDates;
    }

    public void setEventDates(String eventDates) {
        this.eventDates = eventDates;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public String getRegistrationUrl() {
        return registrationUrl;
    }

    public void setRegistrationUrl(String registrationUrl) {
        this.registrationUrl = registrationUrl;
    }
}
