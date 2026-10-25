package com.example.paperpath;

public class NotificationItem {
    private String title;
    private String message;
    private String time;
    private String category;
    private int iconRes;
    private int bgRes;
    private Conference conference;

    public NotificationItem() {
    }

    public NotificationItem(String title, String message, String time, String category, int iconRes, int bgRes) {
        this(title, message, time, category, iconRes, bgRes, null);
    }

    public NotificationItem(String title, String message, String time, String category,
                            int iconRes, int bgRes, Conference conference) {
        this.title = title;
        this.message = message;
        this.time = time;
        this.category = category;
        this.iconRes = iconRes;
        this.bgRes = bgRes;
        this.conference = conference;
    }

    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getTime() { return time; }
    public String getCategory() { return category; }
    public int getIconRes() { return iconRes; }
    public int getBgRes() { return bgRes; }
    public Conference getConference() { return conference; }
}
