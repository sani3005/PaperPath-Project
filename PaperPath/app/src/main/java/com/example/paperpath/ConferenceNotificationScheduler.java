package com.example.paperpath;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public final class ConferenceNotificationScheduler {

    private ConferenceNotificationScheduler() {
    }

    public static void schedule(Context context, String email) {
        String userEmail = email == null ? "" : email.trim();
        if (userEmail.isEmpty()) {
            return;
        }

        Data input = new Data.Builder()
                .putString("user_email", userEmail)
                .build();
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        String uniqueName = "conference_notifications_" + userEmail.toLowerCase();

        OneTimeWorkRequest initialCheck = new OneTimeWorkRequest.Builder(
                ConferenceNotificationWorker.class)
                .setInputData(input)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(context).enqueueUniqueWork(
                uniqueName + "_initial",
                ExistingWorkPolicy.KEEP,
                initialCheck
        );

        PeriodicWorkRequest periodicCheck = new PeriodicWorkRequest.Builder(
                ConferenceNotificationWorker.class,
                1,
                TimeUnit.HOURS
        )
                .setInputData(input)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                uniqueName,
                ExistingPeriodicWorkPolicy.UPDATE,
                periodicCheck
        );
    }
}
