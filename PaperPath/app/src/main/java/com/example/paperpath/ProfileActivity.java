package com.example.paperpath;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
            if (email != null && !email.isEmpty() && tvProfileEmail != null) {
                tvProfileEmail.setText(email);
            }
        }

        setClickListener(R.id.btnEditProfile, v -> {
            Toast.makeText(this, "Edit Profile feature opening...", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupMenuActions() {
        // Home
        setClickListener(R.id.menuItemHome, v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Saved Conferences
        setClickListener(R.id.menuItemSaved, v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.putExtra("SHOW_SAVED_TAB", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // My Applications
        setClickListener(R.id.menuItemApplications, v -> {
            Toast.makeText(this, "My Applications: 2 Submitted Papers in Review", Toast.LENGTH_SHORT).show();
        });

        // Notifications
        setClickListener(R.id.menuItemNotifications, v -> {
            Intent intent = new Intent(ProfileActivity.this, NotificationsActivity.class);
            intent.putExtra("USER_EMAIL", getIntent().getStringExtra("USER_EMAIL"));
            startActivity(intent);
            finish();
        });

        // Settings
        setClickListener(R.id.menuItemSettings, v -> {
            Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
            intent.putExtra("USER_NAME", getIntent().getStringExtra("USER_NAME"));
            intent.putExtra("USER_EMAIL", getIntent().getStringExtra("USER_EMAIL"));
            startActivity(intent);
        });

        // Help & Support
        //findViewById(R.id.menuItemHelp).setOnClickListener(v -> {
         //   Toast.makeText(this, "Help & Support: Contact support@smartcfp.org", Toast.LENGTH_LONG).show();
       // });

        // About SmartCFP
//        findViewById(R.id.menuItemAbout).setOnClickListener(v -> {
//            Toast.makeText(this, "SmartCFP v1.0 • Autonomous CFP Tracker", Toast.LENGTH_SHORT).show();
//        });

        // Logout
        setClickListener(R.id.menuItemLogout, v -> {
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void setClickListener(int viewId, View.OnClickListener listener) {
        View view = findViewById(viewId);
        if (view != null) {
            view.setOnClickListener(listener);
        }
    }
}
