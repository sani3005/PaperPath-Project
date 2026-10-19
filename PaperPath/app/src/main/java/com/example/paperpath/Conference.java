package com.example.paperpath;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Conference implements Serializable {
    @SerializedName("id")
    private String id;
    @SerializedName("shortTitle")
    private String acronym;
    @SerializedName("longTitle")
    private String fullName;
    @SerializedName("sourceName")
    private String sourceName;
    private String domainTag;
    private String coreRank;
    @SerializedName("location")
    private String venue;
    private String confDates;
    @SerializedName("deadline")
    private String submissionDeadline;
    private int daysLeft;
    private String notificationDate;
    private String cameraReadyDate;
    private String aboutDescription;
    private String topics;
    @SerializedName("website")
    private String officialUrl;
    private String status;
    private String[] tags;
    private boolean isBookmarked;

    public Conference() {
    }

    public Conference(String id, String acronym, String fullName, String domainTag,
                      String coreRank, String venue, String confDates,
                      String submissionDeadline, int daysLeft, String notificationDate,
                      String topics, String officialUrl) {
        this(id, acronym, fullName, domainTag, coreRank, venue, confDates,
                submissionDeadline, daysLeft, notificationDate, "Not provided",
                "", topics, officialUrl, "Upcoming", new String[]{domainTag});
    }

    public Conference(String id, String acronym, String fullName, String domainTag,
                      String coreRank, String venue, String confDates,
                      String submissionDeadline, int daysLeft, String notificationDate,
                      String cameraReadyDate, String aboutDescription,
                      String topics, String officialUrl, String status, String[] tags) {
        this.id = id;
        this.acronym = acronym;
        this.fullName = fullName;
        this.domainTag = domainTag;
        this.coreRank = coreRank;
        this.venue = venue;
        this.confDates = confDates;
        this.submissionDeadline = submissionDeadline;
        this.daysLeft = daysLeft;
        this.notificationDate = notificationDate;
        this.cameraReadyDate = cameraReadyDate;
        this.aboutDescription = aboutDescription;
        this.topics = topics;
        this.officialUrl = officialUrl;
        this.status = status;
        this.tags = tags;
        this.isBookmarked = false;
    }

    public String getId() { return valueOr(id, ""); }
    public String getAcronym() { return valueOr(acronym, getFullName()); }
    public String getFullName() { return valueOr(fullName, valueOr(acronym, "Untitled conference")); }
    public String getDomainTag() { return valueOr(domainTag, valueOr(sourceName, "Conference")); }
    public String getCoreRank() { return valueOr(coreRank, "Rank not listed"); }
    public String getVenue() { return valueOr(venue, "Location not listed"); }
    public String getConfDates() {
        if (hasText(confDates)) return confDates;
        if (!hasText(submissionDeadline)) return "Date not listed";
        String label = "IEEE".equalsIgnoreCase(sourceName) ? "Conference dates: " : "Submission deadline: ";
        return label + submissionDeadline;
    }
    public String getSubmissionDeadline() { return valueOr(submissionDeadline, "Not provided"); }
    public int getDaysLeft() { return daysLeft; }
    public String getNotificationDate() { return valueOr(notificationDate, "Not provided"); }
    public String getCameraReadyDate() { return valueOr(cameraReadyDate, "Not provided"); }
    public String getAboutDescription() {
        return valueOr(aboutDescription, getFullName() + " - " + getTopics());
    }
    public String getTopics() { return valueOr(topics, valueOr(sourceName, "Research and academic conference")); }
    public String getOfficialUrl() { return valueOr(officialUrl, ""); }
    public String getStatus() { return valueOr(status, "Upcoming"); }
    public String[] getTags() {
        if (tags != null && tags.length > 0) return tags;
        if (hasText(domainTag) && hasText(sourceName)) return new String[]{domainTag, sourceName};
        if (hasText(sourceName)) return new String[]{sourceName};
        if (hasText(domainTag)) return new String[]{domainTag};
        return new String[]{"Conference"};
    }
    public boolean isBookmarked() { return isBookmarked; }
    public void setBookmarked(boolean bookmarked) { isBookmarked = bookmarked; }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static String valueOr(String value, String fallback) {
        return hasText(value) ? value : fallback;
    }
}
