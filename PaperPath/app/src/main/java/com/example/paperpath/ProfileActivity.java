package com.example.paperpath;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvProfileName;
    private TextView tvProfileEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setupMenuActions();
    }

    private void initViews() {
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);

        if (getIntent().hasExtra("USER_NAME")) {
            String name = getIntent().getStringExtra("USER_NAME");
            if (name != null && !name.isEmpty()) {
                tvProfileName.setText(name.replace("✨", "").trim());
            }
        }

        if (getIntent().hasExtra("USER_EMAIL")) {
            String email = getIntent().getStringExtra("USER_EMAIL");
            if (email != null && !email.isEmpty()) {
                tvProfileEmail.setText(email);
            }
        }

        findViewById(R.id.btnEditProfile).setOnClickListener(v -> {
            Toast.makeText(this, "Edit Profile feature opening...", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupMenuActions() {
        // Home
        findViewById(R.id.menuItemHome).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Saved Conferences
        findViewById(R.id.menuItemSaved).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.putExtra("SHOW_SAVED_TAB", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // My Applications
        findViewById(R.id.menuItemApplications).setOnClickListener(v -> {
            Toast.makeText(this, "My Applications: 2 Submitted Papers in Review", Toast.LENGTH_SHORT).show();
        });

        // Notifications
        findViewById(R.id.menuItemNotifications).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, NotificationsActivity.class);
            startActivity(intent);
            finish();
        });

        // Settings
        findViewById(R.id.menuItemSettings).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // Help & Support
        findViewById(R.id.menuItemHelp).setOnClickListener(v -> {
            Toast.makeText(this, "Help & Support: Contact support@smartcfp.org", Toast.LENGTH_LONG).show();
        });

        // About SmartCFP
        findViewById(R.id.menuItemAbout).setOnClickListener(v -> {
            Toast.makeText(this, "SmartCFP v1.0 • Autonomous CFP Tracker", Toast.LENGTH_SHORT).show();
        });

        // Logout
        findViewById(R.id.menuItemLogout).setOnClickListener(v -> {
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
