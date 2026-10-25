package com.example.paperpath;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Response;

public class ConferenceNotificationWorker extends Worker {

    private static final String CHANNEL_ID = "conference_updates";
    private static final String PREFS_NAME = "conference_notifications";
    private static final String KEY_PREFIX = "seen_v2_";
    private static final String INPUT_EMAIL = "user_email";

    public ConferenceNotificationWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String email = getInputData().getString(INPUT_EMAIL);
        if (email == null || email.trim().isEmpty()) {
            return Result.failure();
        }

        try {
            Response<List<Conference>> response = RetrofitClient.getApiService()
                    .getRecommendedConferences(email.trim())
                    .execute();
            if (!response.isSuccessful() || response.body() == null) {
                return response.code() >= 500 || response.code() == 429
                        ? Result.retry()
                        : Result.failure();
            }

            List<Conference> conferences = response.body();
            String preferenceKey = KEY_PREFIX + email.trim().toLowerCase();
            Set<String> seenIds = new HashSet<>(getApplicationContext()
                    .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .getStringSet(preferenceKey, Collections.emptySet()));
            boolean firstSuccessfulCheck = !getApplicationContext()
                    .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .contains(preferenceKey);
            Set<String> currentIds = new HashSet<>();
            boolean notificationsAllowed = canPostNotifications();

            for (Conference conference : conferences) {
                String id = conference.getId();
                if (id.isEmpty()) {
                    id = conference.getOfficialUrl();
                }
                if (id.isEmpty()) {
                    continue;
                }
                currentIds.add(id);
                NotificationHistory.add(getApplicationContext(), email, conference);
            }

            if (firstSuccessfulCheck && !conferences.isEmpty()) {
                Conference first = conferences.get(0);
                showNotification(
                        first,
                        "recommendations_" + email.trim().toLowerCase(),
                        "Your recommendations are ready",
                        conferences.size() + " conferences match your interests. Tap to explore."
                );
            } else if (!firstSuccessfulCheck) {
                for (Conference conference : conferences) {
                    String id = conference.getId().isEmpty()
                            ? conference.getOfficialUrl()
                            : conference.getId();
                    if (!id.isEmpty() && !seenIds.contains(id)) {
                        showNotification(
                                conference,
                                id,
                                "New conference matching your interests",
                                conference.getFullName()
                        );
                    }
                }
            }

            if (notificationsAllowed) {
                getApplicationContext()
                        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .putStringSet(preferenceKey, currentIds)
                        .apply();
            }
            return Result.success();
        } catch (IOException exception) {
            return Result.retry();
        }
    }

    private boolean canPostNotifications() {
        return AppNotificationHelper.canPost(getApplicationContext());
    }

    private void showNotification(
            Conference conference,
            String id,
            String title,
            String message) {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, ConferenceDetailActivity.class);
        intent.putExtra(ConferenceDetailActivity.EXTRA_CONFERENCE, conference);
        AppNotificationHelper.show(context, id.hashCode(), title, message, intent);
    }
}
