package com.example.paperpath;

import android.content.Intent;
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
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("SpellCheckingInspection")
public class TopicSelectionActivity extends AppCompatActivity {

    private final List<ChipGroup> topicGroups = new ArrayList<>();
    private ChipGroup chipGroupCustom;
    private TextInputEditText etCustomSubject;
    private MaterialButton btnContinueToFeed;
    private String userEmail;
    private String userName;
    private boolean isEditingTopics;
    private boolean topicsLoaded;

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

        Intent intent = getIntent();
        userEmail = intent.getStringExtra("USER_EMAIL");
        userName = intent.getStringExtra("USER_NAME");
        isEditingTopics = intent.getBooleanExtra("IS_EDITING_TOPICS", false);

        initViews();
        setupListeners();
        loadUserTopics();
    }

    private void initViews() {
        topicGroups.addAll(Arrays.asList(
                findViewById(R.id.chipGroupDomains),
                findViewById(R.id.chipGroupGeography),
                findViewById(R.id.chipGroupChemistry),
                findViewById(R.id.chipGroupZoology),
                findViewById(R.id.chipGroupPhysics),
                findViewById(R.id.chipGroupMaths),
                findViewById(R.id.chipGroupHumanities),
                findViewById(R.id.chipGroupLifeSci)
        ));
        chipGroupCustom = findViewById(R.id.chipGroupCustom);
        topicGroups.add(chipGroupCustom);
        etCustomSubject = findViewById(R.id.etCustomSubject);
        btnContinueToFeed = findViewById(R.id.btnContinueToFeed);
        btnContinueToFeed.setEnabled(false);
    }

    private void setupListeners() {
        btnContinueToFeed.setOnClickListener(v -> {
            if (topicsLoaded) {
                saveTopics();
            } else {
                loadUserTopics();
            }
        });

        findViewById(R.id.btnAddSubject).setOnClickListener(v -> addCustomTopic());
        etCustomSubject.setOnEditorActionListener((textView, actionId, event) -> {
            addCustomTopic();
            return true;
        });
    }

    private void addCustomTopic() {
        String topic = etCustomSubject.getText() == null
                ? ""
                : etCustomSubject.getText().toString().trim();
        if (topic.isEmpty()) {
            etCustomSubject.setError("Enter a topic");
            return;
        }

        for (ChipGroup group : topicGroups) {
            for (int i = 0; i < group.getChildCount(); i++) {
                if (group.getChildAt(i) instanceof Chip
                        && topic.equalsIgnoreCase(((Chip) group.getChildAt(i)).getText().toString())) {
                    ((Chip) group.getChildAt(i)).setChecked(true);
                    etCustomSubject.setText("");
                    return;
                }
            }
        }

        addCustomChip(topic, true);
        etCustomSubject.setText("");
    }

    private void addCustomChip(String topic, boolean checked) {
        Chip chip = new Chip(this);
        chip.setText(topic);
        chip.setCheckable(true);
        chip.setChecked(checked);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> chipGroupCustom.removeView(chip));
        chipGroupCustom.addView(chip);
    }

    private void loadUserTopics() {
        if (userEmail == null || userEmail.trim().isEmpty()) {
            Toast.makeText(this, "Your account email is missing. Sign in again.", Toast.LENGTH_LONG).show();
            return;
        }

        btnContinueToFeed.setEnabled(false);
        RetrofitClient.getApiService().getUserTopics(userEmail).enqueue(new Callback<UserTopicsResponse>() {
            @Override
            public void onResponse(Call<UserTopicsResponse> call, Response<UserTopicsResponse> response) {
                if (isFinishing() || isDestroyed()) return;
                if (!response.isSuccessful() || response.body() == null) {
                    btnContinueToFeed.setEnabled(true);
                    Toast.makeText(
                            TopicSelectionActivity.this,
                            "Could not load your topics (HTTP " + response.code() + "). Tap Continue to retry.",
                            Toast.LENGTH_LONG
                    ).show();
                    return;
                }

                userName = response.body().getName();
                restoreSelections(response.body().getTopics());
                topicsLoaded = true;
                btnContinueToFeed.setEnabled(true);
            }

            @Override
            public void onFailure(Call<UserTopicsResponse> call, Throwable error) {
                if (isFinishing() || isDestroyed()) return;
                btnContinueToFeed.setEnabled(true);
                Toast.makeText(
                        TopicSelectionActivity.this,
                        "Cannot load topics. Check that the backend is running, then tap Continue to retry.",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void restoreSelections(List<String> savedTopics) {
        if (savedTopics == null || savedTopics.isEmpty()) return;
        Set<String> restored = new LinkedHashSet<>(savedTopics);

        for (ChipGroup group : topicGroups) {
            for (int i = 0; i < group.getChildCount(); i++) {
                if (group.getChildAt(i) instanceof Chip) {
                    Chip chip = (Chip) group.getChildAt(i);
                    if (restored.remove(chip.getText().toString())) {
                        chip.setChecked(true);
                    }
                }
            }
        }

        for (String topic : restored) {
            addCustomChip(topic, true);
        }
    }

    private void saveTopics() {
        Set<String> selected = new LinkedHashSet<>();
        for (ChipGroup group : topicGroups) {
            for (int i = 0; i < group.getChildCount(); i++) {
                if (group.getChildAt(i) instanceof Chip) {
                    Chip chip = (Chip) group.getChildAt(i);
                    if (chip.isChecked()) {
                        selected.add(chip.getText().toString());
                    }
                }
            }
        }

        if (selected.isEmpty()) {
            Toast.makeText(this, getString(R.string.err_select_at_least_one_domain), Toast.LENGTH_SHORT).show();
            return;
        }

        btnContinueToFeed.setEnabled(false);
        RetrofitClient.getApiService()
                .updateUserTopics(userEmail, new TopicsRequest(new ArrayList<>(selected)))
                .enqueue(new Callback<UserTopicsResponse>() {
                    @Override
                    public void onResponse(
                            Call<UserTopicsResponse> call,
                            Response<UserTopicsResponse> response
                    ) {
                        if (isFinishing() || isDestroyed()) return;
                        btnContinueToFeed.setEnabled(true);
                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(
                                    TopicSelectionActivity.this,
                                    "Could not save topics (HTTP " + response.code() + ")",
                                    Toast.LENGTH_LONG
                            ).show();
                            return;
                        }

                        if (isEditingTopics) {
                            Toast.makeText(TopicSelectionActivity.this, "Topics updated", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }

                        Intent intent = new Intent(TopicSelectionActivity.this, MainActivity.class);
                        intent.putExtra("USER_EMAIL", userEmail);
                        intent.putExtra("USER_NAME", userName);
                        intent.putStringArrayListExtra("SELECTED_TRACKS", new ArrayList<>(selected));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<UserTopicsResponse> call, Throwable error) {
                        if (isFinishing() || isDestroyed()) return;
                        btnContinueToFeed.setEnabled(true);
                        Toast.makeText(
                                TopicSelectionActivity.this,
                                "Cannot save topics. Check that the backend is running and try again.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}
