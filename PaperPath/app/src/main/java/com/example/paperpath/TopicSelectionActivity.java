package com.example.paperpath;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("SpellCheckingInspection")
public class TopicSelectionActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "SmartCFP_Prefs";
    public static final String KEY_USER_TRACKS = "USER_TRACKS";

    private ChipGroup chipGroupDomains;
    private MaterialButton btnContinueToFeed;
    private String userEmail;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_topic_selection);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayoutTopicSelection), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve user data from intent
        Intent intent = getIntent();
        userEmail = intent.getStringExtra("USER_EMAIL");
        userName = intent.getStringExtra("USER_NAME");

        initViews();
        restorePreviousSelections();
        setupListeners();
    }

    private void initViews() {
        chipGroupDomains = findViewById(R.id.chipGroupDomains);
        btnContinueToFeed = findViewById(R.id.btnContinueToFeed);
    }

    private void restorePreviousSelections() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> savedTracks = prefs.getStringSet(KEY_USER_TRACKS, null);

        if (savedTracks != null && !savedTracks.isEmpty()) {
            for (int i = 0; i < chipGroupDomains.getChildCount(); i++) {
                if (chipGroupDomains.getChildAt(i) instanceof Chip) {
                    Chip chip = (Chip) chipGroupDomains.getChildAt(i);
                    if (savedTracks.contains(chip.getText().toString())) {
                        chip.setChecked(true);
                    }
                }
            }
        }
    }

    private void setupListeners() {
        btnContinueToFeed.setOnClickListener(v -> saveAndProceed());
    }

    private void saveAndProceed() {
        List<String> selectedDomains = new ArrayList<>();

        for (int i = 0; i < chipGroupDomains.getChildCount(); i++) {
            if (chipGroupDomains.getChildAt(i) instanceof Chip) {
                Chip chip = (Chip) chipGroupDomains.getChildAt(i);
                if (chip.isChecked()) {
                    selectedDomains.add(chip.getText().toString());
                }
            }
        }

        // Validate that user selected at least 1 domain
        if (selectedDomains.isEmpty()) {
            Toast.makeText(this, getString(R.string.err_select_at_least_one_domain), Toast.LENGTH_SHORT).show();
            return;
        }

        // Save selected domains to SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(KEY_USER_TRACKS, new HashSet<>(selectedDomains));
        editor.apply();

        Toast.makeText(this, selectedDomains.size() + " domains selected", Toast.LENGTH_SHORT).show();

        // Launch MainActivity / Smart Feed
        Intent intent = new Intent(TopicSelectionActivity.this, MainActivity.class);
        intent.putExtra("USER_EMAIL", userEmail);
        intent.putExtra("USER_NAME", userName);
        intent.putStringArrayListExtra("SELECTED_TRACKS", new ArrayList<>(selectedDomains));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
