package com.example.paperpath;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity
        implements NotificationAdapter.OnNotificationClickListener {

    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private List<NotificationItem> allNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        initViews();
        setupList();
        setupBottomNav();
    }

    private void initViews() {
        rvNotifications = findViewById(R.id.rvNotifications);
    }

    private void setupList() {
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        allNotifications = getSampleNotifications();
        adapter = new NotificationAdapter(allNotifications, this);
        rvNotifications.setAdapter(adapter);
    }

    private void setupBottomNav() {
        LinearLayout navTabHome = findViewById(R.id.navTabHome);
        if (navTabHome != null) {
            navTabHome.setOnClickListener(v -> {
                Intent intent = new Intent(NotificationsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        LinearLayout navTabSaved = findViewById(R.id.navTabSaved);
        if (navTabSaved != null) {
            navTabSaved.setOnClickListener(v -> {
                Intent intent = new Intent(NotificationsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        LinearLayout navTabProfile = findViewById(R.id.navTabProfile);
        if (navTabProfile != null) {
            navTabProfile.setOnClickListener(v -> {
                Intent intent = new Intent(NotificationsActivity.this, ProfileActivity.class);
                startActivity(intent);
            });
        }
    }

    @Override
    public void onNotificationClick(NotificationItem item) {
        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
    }

    private List<NotificationItem> getSampleNotifications() {
        List<NotificationItem> list = new ArrayList<>();

        // 1. Purple megaphone: New Conference Posted
        list.add(new NotificationItem(
                "New Conference Posted",
                "ICLR 2026 is now available on SmartCFP.",
                "2 hours ago",
                "Updates",
                R.drawable.ic_speaker,
                R.drawable.bg_circle_purple));

        // 2. Green calendar: Application Deadline Reminder
        list.add(new NotificationItem(
                "Application Deadline Reminder",
                "NeurIPS 2025 deadline is in 5 days.",
                "5 hours ago",
                "Reminders",
                R.drawable.ic_calendar,
                R.drawable.bg_circle_green));

        // 3. Blue info: Conference Update
        list.add(new NotificationItem(
                "Conference Update",
                "IEEE AI 2026 has updated its venue to Vancouver, Canada.",
                "1 day ago",
                "Updates",
                R.drawable.ic_info,
                R.drawable.bg_circle_blue));

        // 4. Violet bookmark: Saved Conference
        list.add(new NotificationItem(
                "Saved Conference",
                "You saved ACM SIGCOMM 2026 for later.",
                "1 day ago",
                "System",
                R.drawable.ic_bookmark_ribbon_filled,
                R.drawable.bg_circle_violet));

        // 5. Golden star: New Opportunity
        list.add(new NotificationItem(
                "New Opportunity",
                "Check out 3 new AI conferences matching your interests.",
                "2 days ago",
                "Reminders",
                R.drawable.ic_bookmark_star_filled,
                R.drawable.bg_circle_gold));

        return list;
    }
}
