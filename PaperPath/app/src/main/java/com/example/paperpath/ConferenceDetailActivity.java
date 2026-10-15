package com.example.paperpath;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class ConferenceDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CONFERENCE = "EXTRA_CONFERENCE";

    private ImageView btnDetailBack;
    private ImageView btnDetailShare;
    private ImageView btnDetailBookmark;
    private ImageView ivDetailBadge;
    private TextView tvDetailTitle;
    private TextView tvDetailDates;
    private TextView tvDetailVenue;
    private TextView tvDetailUrl;
    private TextView tvDetailTag1;
    private TextView tvDetailTag2;
    private TextView tvDetailTag3;
    private TextView tvDetailAboutText;
    private TextView tvSubmissionDeadlineDate;
    private TextView tvAcceptanceDate;
    private TextView tvCameraReadyDate;
    private MaterialButton btnSaveForLater;

    private Conference conference;
    private boolean isBookmarked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conference_detail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            conference = getIntent().getSerializableExtra(EXTRA_CONFERENCE, Conference.class);
        } else {
            conference = (Conference) getIntent().getSerializableExtra(EXTRA_CONFERENCE);
        }

        initViews();
        populateData();
        setupListeners();
    }

    private void initViews() {
        btnDetailBack = findViewById(R.id.btnDetailBack);
        btnDetailShare = findViewById(R.id.btnDetailShare);
        btnDetailBookmark = findViewById(R.id.btnDetailBookmark);
        ivDetailBadge = findViewById(R.id.ivDetailBadge);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailDates = findViewById(R.id.tvDetailDates);
        tvDetailVenue = findViewById(R.id.tvDetailVenue);
        tvDetailUrl = findViewById(R.id.tvDetailUrl);
        tvDetailTag1 = findViewById(R.id.tvDetailTag1);
        tvDetailTag2 = findViewById(R.id.tvDetailTag2);
        tvDetailTag3 = findViewById(R.id.tvDetailTag3);
        tvDetailAboutText = findViewById(R.id.tvDetailAboutText);
        tvSubmissionDeadlineDate = findViewById(R.id.tvSubmissionDeadlineDate);
        tvAcceptanceDate = findViewById(R.id.tvAcceptanceDate);
        tvCameraReadyDate = findViewById(R.id.tvCameraReadyDate);
        btnSaveForLater = findViewById(R.id.btnSaveForLater);
    }

    private void populateData() {
        if (conference == null) {
            // Provide fallback default matching reference screenshot
            conference = new Conference(
                    "ieee_ai_2026",
                    "IEEE ICAI",
                    "IEEE International Conference on AI",
                    "AI & ML",
                    "CORE A*",
                    "Vancouver, Canada",
                    "Jan 15 - 18, 2026",
                    "Sep 10, 2025",
                    25,
                    "Oct 15, 2025",
                    "Nov 5, 2025",
                    "The IEEE International Conference on Artificial Intelligence (ICAI) brings together researchers, practitioners and industry experts to discuss the latest advances in AI, machine learning and intelligent systems.",
                    "AI, Machine Learning, Deep Learning",
                    "https://www.ieee.org/conferences/ai",
                    "Upcoming",
                    new String[]{"AI & ML", "Computer Science", "IEEE"}
            );
        }

        tvDetailTitle.setText(conference.getFullName());
        tvDetailDates.setText(conference.getConfDates());
        tvDetailVenue.setText(conference.getVenue());
        tvDetailUrl.setText(conference.getOfficialUrl());
        tvDetailAboutText.setText(conference.getAboutDescription());

        // Key Dates
        tvSubmissionDeadlineDate.setText(conference.getSubmissionDeadline());
        tvAcceptanceDate.setText(conference.getNotificationDate());
        tvCameraReadyDate.setText(conference.getCameraReadyDate());

        // Badge
        if (conference.getAcronym().contains("ACM") || conference.getFullName().contains("ACM")) {
            ivDetailBadge.setImageResource(R.drawable.ic_badge_acm);
        } else {
            ivDetailBadge.setImageResource(R.drawable.ic_badge_ieee);
        }

        // Tags
        String[] tags = conference.getTags();
        if (tags.length > 0) tvDetailTag1.setText(tags[0]);
        if (tags.length > 1) {
            tvDetailTag2.setText(tags[1]);
            tvDetailTag2.setVisibility(View.VISIBLE);
        } else {
            tvDetailTag2.setVisibility(View.GONE);
        }
        if (tags.length > 2) {
            tvDetailTag3.setText(tags[2]);
            tvDetailTag3.setVisibility(View.VISIBLE);
        } else {
            tvDetailTag3.setVisibility(View.GONE);
        }

        isBookmarked = BookmarkManager.isBookmarked(this, conference.getId());
        conference.setBookmarked(isBookmarked);
        updateBookmarkUI();
    }

    private void setupListeners() {
        btnDetailBack.setOnClickListener(v -> finish());

        // Share Action
        btnDetailShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareBody = conference.getFullName() + "\nDates: " + conference.getConfDates() + "\nVenue: " + conference.getVenue() + "\nSubmission Deadline: " + conference.getSubmissionDeadline() + "\nLink: " + conference.getOfficialUrl();
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, conference.getFullName());
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(shareIntent, "Share Conference"));
        });

        // Top Bookmark Star Toggle
        btnDetailBookmark.setOnClickListener(v -> {
            toggleBookmark();
        });

        // Bottom CTA Save for Later Button Toggle
        btnSaveForLater.setOnClickListener(v -> {
            toggleBookmark();
        });

        // Click official URL to open in browser
        tvDetailUrl.setOnClickListener(v -> {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(conference.getOfficialUrl()));
                startActivity(browserIntent);
            } catch (Exception e) {
                Toast.makeText(this, "Could not open browser", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleBookmark() {
        isBookmarked = !isBookmarked;
        conference.setBookmarked(isBookmarked);
        BookmarkManager.setBookmarked(this, conference.getId(), isBookmarked);
        updateBookmarkUI();

        if (isBookmarked) {
            Toast.makeText(this, "Saved for Later!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Removed from Saved", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBookmarkUI() {
        btnDetailBookmark.setImageTintList(null);
        if (isBookmarked) {
            btnDetailBookmark.setImageResource(R.drawable.ic_bookmark_star_filled);
            btnSaveForLater.setText(R.string.saved_for_later);
            btnSaveForLater.setIconResource(R.drawable.ic_bookmark_ribbon_filled);
        } else {
            btnDetailBookmark.setImageResource(R.drawable.ic_bookmark_star);
            btnSaveForLater.setText(R.string.save_for_later);
            btnSaveForLater.setIconResource(R.drawable.ic_bookmark_ribbon);
        }
    }
}
