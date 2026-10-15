package com.example.paperpath;

public class NotificationItem {
    private final String title;
    private final String message;
    private final String time;
    private final String category; // "Reminders", "Updates", "System"
    private final int iconRes;
    private final int bgRes;

    public NotificationItem(String title, String message, String time, String category, int iconRes, int bgRes) {
        this.title = title;
        this.message = message;
        this.time = time;
        this.category = category;
        this.iconRes = iconRes;
        this.bgRes = bgRes;
    }

    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getTime() { return time; }
    public String getCategory() { return category; }
    public int getIconRes() { return iconRes; }
    public int getBgRes() { return bgRes; }
}
