package com.example.paperpath;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class NotificationHistory {

    private static final String TAG = "NotificationHistory";
    private static final String PREFS_NAME = "conference_notifications";
    private static final String HISTORY_PREFIX = "history_";
    private static final Type ITEM_LIST_TYPE =
            new TypeToken<ArrayList<StoredNotification>>() { }.getType();
    private static final Gson GSON = new Gson();

    private NotificationHistory() {
    }

    public static synchronized void add(
            Context context,
            String email,
            Conference conference) {
        String key = historyKey(email);
        List<StoredNotification> history = read(context, key);
        String conferenceId = identity(conference);
        boolean alreadyRecorded = history.stream()
                .anyMatch(item -> item.conferenceId.equals(conferenceId));
        if (alreadyRecorded) {
            return;
        }

        history.add(0, new StoredNotification(
                conferenceId,
                System.currentTimeMillis(),
                conference
        ));
        if (history.size() > 100) {
            history = new ArrayList<>(history.subList(0, 100));
        }

        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(key, GSON.toJson(history))
                .apply();
    }

    public static synchronized List<NotificationItem> load(
            Context context,
            String email) {
        List<NotificationItem> notifications = new ArrayList<>();
        for (StoredNotification item : read(context, historyKey(email))) {
            if (item.conference == null) {
                continue;
            }
            String relativeTime = DateUtils.getRelativeTimeSpanString(
                    item.createdAt,
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS
            ).toString();
            notifications.add(new NotificationItem(
                    "New conference matching your interests",
                    item.conference.getFullName(),
                    relativeTime,
                    "Updates",
                    R.drawable.ic_speaker,
                    R.drawable.bg_circle_purple,
                    item.conference
            ));
        }
        return notifications;
    }

    private static List<StoredNotification> read(Context context, String key) {
        String json = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(key, "[]");
        try {
            List<StoredNotification> history = GSON.fromJson(json, ITEM_LIST_TYPE);
            return history == null ? new ArrayList<>() : history;
        } catch (RuntimeException exception) {
            Log.e(TAG, "Could not read saved notification history", exception);
            return new ArrayList<>();
        }
    }

    private static String historyKey(String email) {
        return HISTORY_PREFIX + email.trim().toLowerCase();
    }

    private static String identity(Conference conference) {
        String id = conference.getId();
        return id.isEmpty() ? conference.getOfficialUrl() : id;
    }

    private static final class StoredNotification {
        private String conferenceId;
        private long createdAt;
        private Conference conference;

        private StoredNotification() {
        }

        private StoredNotification(
                String conferenceId,
                long createdAt,
                Conference conference) {
            this.conferenceId = conferenceId;
            this.createdAt = createdAt;
            this.conference = conference;
        }
    }
}
