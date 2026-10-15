package com.example.paperpath;

import java.io.Serializable;

public class Conference implements Serializable {
    private String id;
    private String acronym;
    private String fullName;
    private String domainTag;
    private String coreRank;
    private String venue;
    private String confDates;
    private String submissionDeadline;
    private int daysLeft;
    private String notificationDate;
    private String cameraReadyDate;
    private String aboutDescription;
    private String topics;
    private String officialUrl;
    private String status; // "Upcoming", "Ongoing", "Past"
    private String[] tags;
    private boolean isBookmarked;

    public Conference(String id, String acronym, String fullName, String domainTag,
                      String coreRank, String venue, String confDates,
                      String submissionDeadline, int daysLeft, String notificationDate,
                      String topics, String officialUrl) {
        this(id, acronym, fullName, domainTag, coreRank, venue, confDates,
             submissionDeadline, daysLeft, notificationDate, "Nov 5, 2025",
             "", topics, officialUrl, "Upcoming", new String[]{"AI & ML", "Computer Science", "IEEE"});
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

    public String getId() { return id; }
    public String getAcronym() { return acronym; }
    public String getFullName() { return fullName; }
    public String getDomainTag() { return domainTag; }
    public String getCoreRank() { return coreRank; }
    public String getVenue() { return venue; }
    public String getConfDates() { return confDates; }
    public String getSubmissionDeadline() { return submissionDeadline; }
    public int getDaysLeft() { return daysLeft; }
    public String getNotificationDate() { return notificationDate; }
    public String getCameraReadyDate() {
        return (cameraReadyDate != null && !cameraReadyDate.isEmpty()) ? cameraReadyDate : "Nov 5, 2025";
    }
    public String getAboutDescription() {
        if (aboutDescription != null && !aboutDescription.isEmpty()) {
            return aboutDescription;
        }
        return "The " + fullName + " brings together leading academic researchers, industry scientists, and engineers to exchange research advancements in " + topics + ".";
    }
    public String getTopics() { return topics; }
    public String getOfficialUrl() { return officialUrl; }
    public String getStatus() { return (status != null) ? status : "Upcoming"; }
    public String[] getTags() {
        if (tags != null && tags.length > 0) return tags;
        return new String[]{"AI & ML", "Computer Science", acronym};
    }
    public boolean isBookmarked() { return isBookmarked; }
    public void setBookmarked(boolean bookmarked) { isBookmarked = bookmarked; }
}
