package com.example.paperpath;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotificationsActivity extends AppCompatActivity
        implements NotificationAdapter.OnNotificationClickListener {

    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private TextView tvNotificationsEmpty;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        initViews();
        setupList();
        setupBottomNav();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications();
    }

    private void initViews() {
        rvNotifications = findViewById(R.id.rvNotifications);
        tvNotificationsEmpty = findViewById(R.id.tvNotificationsEmpty);
    }

    private void setupList() {
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(List.of(), this);
        rvNotifications.setAdapter(adapter);
    }

    private void loadNotifications() {
        userEmail = getIntent().getStringExtra("USER_EMAIL");
        if (userEmail == null || userEmail.trim().isEmpty()) {
            showEmptyState("Sign in again to view your notification history.");
            return;
        }

        List<NotificationItem> notifications =
                NotificationHistory.load(this, userEmail);
        adapter.updateList(notifications);
        if (notifications.isEmpty()) {
            showEmptyState("No new matching conferences yet. We’ll notify you when one is found.");
        } else {
            tvNotificationsEmpty.setVisibility(View.GONE);
            rvNotifications.setVisibility(View.VISIBLE);
        }
    }

    private void showEmptyState(String message) {
        tvNotificationsEmpty.setText(message);
        tvNotificationsEmpty.setVisibility(View.VISIBLE);
        rvNotifications.setVisibility(View.GONE);
    }

    private void setupBottomNav() {
        LinearLayout navTabHome = findViewById(R.id.navTabHome);
        if (navTabHome != null) {
            navTabHome.setOnClickListener(v -> {
                Intent intent = new Intent(NotificationsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("USER_EMAIL", userEmail);
                startActivity(intent);
                finish();
            });
        }

        LinearLayout navTabSaved = findViewById(R.id.navTabSaved);
        if (navTabSaved != null) {
            navTabSaved.setOnClickListener(v -> {
                Intent intent = new Intent(NotificationsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("USER_EMAIL", userEmail);
                startActivity(intent);
                finish();
            });
        }

        LinearLayout navTabProfile = findViewById(R.id.navTabProfile);
        if (navTabProfile != null) {
            navTabProfile.setOnClickListener(v -> {
                Intent intent = new Intent(NotificationsActivity.this, ProfileActivity.class);
                intent.putExtra("USER_EMAIL", userEmail);
                startActivity(intent);
            });
        }
    }

    @Override
    public void onNotificationClick(NotificationItem item) {
        Conference conference = item.getConference();
        if (conference == null) {
            Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, ConferenceDetailActivity.class);
        intent.putExtra(ConferenceDetailActivity.EXTRA_CONFERENCE, conference);
        startActivity(intent);
    }
}
