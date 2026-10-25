package com.example.paperpath;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressWarnings("SpellCheckingInspection")
public class SettingsActivity extends AppCompatActivity {

    private ImageButton btnBackFromSettings;
    private TextView tvSettingsWelcomeTitle;
    private TextView tvSettingsUserEmail;
    private ChipGroup chipGroupSettingsTracks;
    private MaterialButton btnEditTracksSettings;
    private MaterialButton btnSignOutSettings;
    private String userEmail;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayoutSettings), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        displayUserData();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh tracks if returning from TopicSelectionActivity
        loadAndDisplayTracks();
    }

    private void initViews() {
        btnBackFromSettings = findViewById(R.id.btnBackFromSettings);
        tvSettingsWelcomeTitle = findViewById(R.id.tvSettingsWelcomeTitle);
        tvSettingsUserEmail = findViewById(R.id.tvSettingsUserEmail);
        chipGroupSettingsTracks = findViewById(R.id.chipGroupSettingsTracks);
        btnEditTracksSettings = findViewById(R.id.btnEditTracksSettings);
        btnSignOutSettings = findViewById(R.id.btnSignOutSettings);
    }

    @SuppressLint("SetTextI18n")
    private void displayUserData() {
        Intent intent = getIntent();
        userEmail = intent.getStringExtra("USER_EMAIL");
        userName = intent.getStringExtra("USER_NAME");

        if (userName != null && !userName.isEmpty()) {
            tvSettingsWelcomeTitle.setText("Welcome to SmartCFP, " + userName);
        } else {
            tvSettingsWelcomeTitle.setText(getString(R.string.welcome_to_smartcfp));
        }

        if (userEmail != null && !userEmail.isEmpty()) {
            tvSettingsUserEmail.setText(userEmail);
        } else {
            tvSettingsUserEmail.setText("Account email unavailable");
        }
    }

    private void loadAndDisplayTracks() {
        if (userEmail == null || userEmail.trim().isEmpty()) {
            Toast.makeText(this, "Your account email is missing. Sign in again.", Toast.LENGTH_LONG).show();
            return;
        }

        RetrofitClient.getApiService().getUserTopics(userEmail).enqueue(new Callback<UserTopicsResponse>() {
            @Override
            public void onResponse(
                    Call<UserTopicsResponse> call,
                    Response<UserTopicsResponse> response
            ) {
                if (isFinishing() || isDestroyed()) return;
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(
                            SettingsActivity.this,
                            "Could not load your topics (HTTP " + response.code() + ")",
                            Toast.LENGTH_LONG
                    ).show();
                    return;
                }
                displayTracks(response.body().getTopics());
            }

            @Override
            public void onFailure(Call<UserTopicsResponse> call, Throwable error) {
                if (isFinishing() || isDestroyed()) return;
                Toast.makeText(
                        SettingsActivity.this,
                        "Cannot load topics. Check that the backend is running.",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void displayTracks(List<String> tracks) {
        chipGroupSettingsTracks.removeAllViews();

        if (tracks != null && !tracks.isEmpty()) {
            for (String track : tracks) {
                Chip chip = new Chip(this);
                chip.setText(track);
                chip.setClickable(false);
                chip.setCheckable(false);
                chip.setChipBackgroundColorResource(R.color.white);
                chip.setTextColor(ContextCompat.getColor(this, R.color.dark_blue_primary));
                chip.setChipStrokeColorResource(R.color.dark_blue_primary);
                chip.setChipStrokeWidth(3.0f);
                chipGroupSettingsTracks.addView(chip);
            }
            return;
        }

        Chip emptyChip = new Chip(this);
        emptyChip.setText("No topics selected (Tap Edit to choose)");
        emptyChip.setClickable(false);
        chipGroupSettingsTracks.addView(emptyChip);
    }

    private void setupListeners() {
        btnBackFromSettings.setOnClickListener(v -> finish());

        btnEditTracksSettings.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, TopicSelectionActivity.class);
            intent.putExtra("USER_EMAIL", userEmail);
            intent.putExtra("USER_NAME", userName);
            intent.putExtra("IS_EDITING_TOPICS", true);
            startActivity(intent);
        });

        btnSignOutSettings.setOnClickListener(v -> handleSignOut());
    }

    private void handleSignOut() {
        Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SettingsActivity.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
