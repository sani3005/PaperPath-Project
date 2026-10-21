package com.example.paperpath;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ConferenceAdapter.OnConferenceClickListener {

    private static final int HOME_CONFERENCE_LIMIT = 20;

    private NestedScrollView scrollViewHome;
    private View layoutGreeting;
    private View layoutSearchBar;
    private View scrollCategories;
    private View layoutSectionHeader;
    private TextView tvSectionTitle;
    private TextView tvSeeAll;
    private View layoutSavedHeader;
    private TextView tvSavedHeaderSubtitle;
    private RecyclerView rvConferences;
    private TextView tvNoConferences;
    private View layoutEmptySaved;
    private View btnExploreConferences;
    private ConferenceAdapter adapter;
    private List<Conference> allConferences;

    // Categories
    private LinearLayout btnCatAll;
    private LinearLayout btnCatCS;
    private LinearLayout btnCatAI;
    private LinearLayout btnCatEng;
    private String selectedCategory = "All";

    // Bottom Navigation
    private LinearLayout navTabHome;
    private LinearLayout navTabSaved;
    private LinearLayout navTabNotifications;
    private LinearLayout navTabProfile;
    private ImageView ivNavHome;
    private TextView tvNavHome;
    private ImageView ivNavSaved;
    private TextView tvNavSaved;

    private String currentBottomTab = "Home";
    private boolean isSavedOnlyFilter = false;
    private String userEmail = "saniya.g23@gmail.com";
    private String userName = "Saniya ✨";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent().hasExtra("USER_NAME")) {
            userName = getIntent().getStringExtra("USER_NAME");
        }
        if (getIntent().hasExtra("USER_EMAIL")) {
            userEmail = getIntent().getStringExtra("USER_EMAIL");
        }

        initViews();
        setupCategories();
        setupRecyclerView();
        setupListeners();

        if (getIntent().getBooleanExtra("SHOW_SAVED_TAB", false)) {
            selectBottomTab("Saved");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent != null && intent.getBooleanExtra("SHOW_SAVED_TAB", false)) {
            selectBottomTab("Saved");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncBookmarksAndFilter();
    }

    private void initViews() {
        scrollViewHome = findViewById(R.id.scrollViewHome);

        layoutGreeting = findViewById(R.id.layoutGreeting);
        layoutSearchBar = findViewById(R.id.layoutSearchBar);
        scrollCategories = findViewById(R.id.scrollCategories);
        layoutSectionHeader = findViewById(R.id.layoutSectionHeader);
        tvSectionTitle = findViewById(R.id.tvSectionTitle);
        tvSeeAll = findViewById(R.id.tvSeeAll);

        layoutSavedHeader = findViewById(R.id.layoutSavedHeader);
        tvSavedHeaderSubtitle = findViewById(R.id.tvSavedHeaderSubtitle);

        rvConferences = findViewById(R.id.rvConferences);
        tvNoConferences = findViewById(R.id.tvNoConferences);
        layoutEmptySaved = findViewById(R.id.layoutEmptySaved);
        btnExploreConferences = findViewById(R.id.btnExploreConferences);

        btnCatAll = findViewById(R.id.btnCatAll);
        btnCatCS = findViewById(R.id.btnCatCS);
        btnCatAI = findViewById(R.id.btnCatAI);
        btnCatEng = findViewById(R.id.btnCatEng);

        navTabHome = findViewById(R.id.navTabHome);
        navTabSaved = findViewById(R.id.navTabSaved);
        navTabNotifications = findViewById(R.id.navTabNotifications);
        navTabProfile = findViewById(R.id.navTabProfile);

        ivNavHome = findViewById(R.id.ivNavHome);
        tvNavHome = findViewById(R.id.tvNavHome);
        ivNavSaved = findViewById(R.id.ivNavSaved);
        tvNavSaved = findViewById(R.id.tvNavSaved);
    }

    private void setupRecyclerView() {
        rvConferences.setLayoutManager(new LinearLayoutManager(this));
        allConferences = new ArrayList<>();
        adapter = new ConferenceAdapter(allConferences, this);
        adapter.setOnBookmarkToggleListener((conference, isBookmarked) -> {
            if (isSavedOnlyFilter) {
                filterConferences();
            }
        });
        rvConferences.setAdapter(adapter);
        loadConferences();
    }


    private void loadConferences() {
        RetrofitClient.getApiService().getConferences().enqueue(new Callback<List<Conference>>() {
            @Override
            public void onResponse(Call<List<Conference>> call, Response<List<Conference>> response) {
                if (isFinishing() || isDestroyed()) return;
                if (!response.isSuccessful() || response.body() == null) {
                    android.widget.Toast.makeText(
                            MainActivity.this,
                            "Could not load conferences (HTTP " + response.code() + ")",
                            android.widget.Toast.LENGTH_LONG
                    ).show();
                    return;
                }
                allConferences = response.body();
                filterConferences();
            }

            @Override
            public void onFailure(Call<List<Conference>> call, Throwable error) {
                if (isFinishing() || isDestroyed()) return;
                android.widget.Toast.makeText(
                        MainActivity.this,
                        "Cannot reach the conference server. Check that the backend is running.",
                        android.widget.Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void setupCategories() {
        btnCatAll.setOnClickListener(v -> selectCategory("All"));
        btnCatCS.setOnClickListener(v -> selectCategory("Computer Science"));
        btnCatAI.setOnClickListener(v -> selectCategory("AI & ML"));
        btnCatEng.setOnClickListener(v -> selectCategory("Engineering"));
    }

    private void selectCategory(String category) {
        selectedCategory = category;
        resetCategoryButton(btnCatAll, false);
        resetCategoryButton(btnCatCS, false);
        resetCategoryButton(btnCatAI, false);
        resetCategoryButton(btnCatEng, false);

        if ("All".equals(category)) {
            resetCategoryButton(btnCatAll, true);
        } else if ("Computer Science".equals(category)) {
            resetCategoryButton(btnCatCS, true);
        } else if ("AI & ML".equals(category)) {
            resetCategoryButton(btnCatAI, true);
        } else if ("Engineering".equals(category)) {
            resetCategoryButton(btnCatEng, true);
        }

        filterConferences();
    }

    private void resetCategoryButton(LinearLayout btn, boolean isActive) {
        if (btn == null)
            return;
        btn.setBackgroundResource(isActive ? R.drawable.bg_category_active : R.drawable.bg_category_inactive);
    }

    private void selectBottomTab(String tab) {
        currentBottomTab = tab;
        int activeColor = ContextCompat.getColor(this, R.color.gradient_purple_start);
        int inactiveColor = ContextCompat.getColor(this, R.color.text_muted);

        if ("Saved".equals(tab)) {
            isSavedOnlyFilter = true;

            // Highlight Saved Tab with filled star
            if (ivNavSaved != null) {
                ivNavSaved.setImageResource(R.drawable.ic_bookmark_star_filled);
                ivNavSaved.setImageTintList(ColorStateList.valueOf(activeColor));
            }
            if (tvNavSaved != null)
                tvNavSaved.setTextColor(activeColor);

            // Unhighlight Home Tab
            if (ivNavHome != null) {
                ivNavHome.setImageResource(R.drawable.ic_home);
                ivNavHome.setImageTintList(ColorStateList.valueOf(inactiveColor));
            }
            if (tvNavHome != null)
                tvNavHome.setTextColor(inactiveColor);

            // Hide Home-only sections
            if (layoutGreeting != null)
                layoutGreeting.setVisibility(View.GONE);
            if (layoutSearchBar != null)
                layoutSearchBar.setVisibility(View.GONE);
            if (scrollCategories != null)
                scrollCategories.setVisibility(View.GONE);
            if (layoutSectionHeader != null)
                layoutSectionHeader.setVisibility(View.GONE);

            // Show dedicated Saved Header
            if (layoutSavedHeader != null)
                layoutSavedHeader.setVisibility(View.VISIBLE);

        } else {
            isSavedOnlyFilter = false;

            // Highlight Home Tab
            if (ivNavHome != null) {
                ivNavHome.setImageResource(R.drawable.ic_home);
                ivNavHome.setImageTintList(ColorStateList.valueOf(activeColor));
            }
            if (tvNavHome != null)
                tvNavHome.setTextColor(activeColor);

            // Unhighlight Saved Tab with outline star
            if (ivNavSaved != null) {
                ivNavSaved.setImageResource(R.drawable.ic_bookmark_star);
                ivNavSaved.setImageTintList(ColorStateList.valueOf(inactiveColor));
            }
            if (tvNavSaved != null)
                tvNavSaved.setTextColor(inactiveColor);

            // Restore Home sections
            if (layoutGreeting != null)
                layoutGreeting.setVisibility(View.VISIBLE);
            if (layoutSearchBar != null)
                layoutSearchBar.setVisibility(View.VISIBLE);
            if (scrollCategories != null)
                scrollCategories.setVisibility(View.VISIBLE);
            if (layoutSectionHeader != null)
                layoutSectionHeader.setVisibility(View.VISIBLE);

            // Hide Saved Header
            if (layoutSavedHeader != null)
                layoutSavedHeader.setVisibility(View.GONE);
        }

        filterConferences();

        if (scrollViewHome != null) {
            scrollViewHome.smoothScrollTo(0, 0);
        }
    }

    private void syncBookmarksAndFilter() {
        if (allConferences != null) {
            for (Conference conf : allConferences) {
                conf.setBookmarked(BookmarkManager.isBookmarked(this, conf.getId()));
            }
        }
        filterConferences();
    }

    private void filterConferences() {
        if (allConferences == null)
            return;

        List<Conference> filtered = new ArrayList<>();
        int totalSaved = 0;

        for (Conference conf : allConferences) {
            boolean isBookmarked = BookmarkManager.isBookmarked(this, conf.getId());
            conf.setBookmarked(isBookmarked);
            if (isBookmarked) {
                totalSaved++;
            }

            if (isSavedOnlyFilter) {
                if (isBookmarked) {
                    filtered.add(conf);
                }
            } else {
                if ("All".equals(selectedCategory)) {
                    filtered.add(conf);
                } else if ("AI & ML".equals(selectedCategory)) {
                    if (conf.getDomainTag().contains("AI") || conf.getDomainTag().contains("Machine Learning")
                            || conf.getFullName().contains("AI") || conf.getTopics().contains("AI")) {
                        filtered.add(conf);
                    }
                } else if ("Computer Science".equals(selectedCategory)) {
                    if (conf.getDomainTag().contains("Computer") || conf.getDomainTag().contains("Systems")
                            || conf.getFullName().contains("Computer") || conf.getFullName().contains("SIGCOMM")
                            || conf.getTopics().contains("Computer")) {
                        filtered.add(conf);
                    }
                } else if ("Engineering".equals(selectedCategory)) {
                    if (conf.getDomainTag().contains("Systems") || conf.getDomainTag().contains("Engineering")
                            || conf.getDomainTag().contains("Networks") || conf.getTopics().contains("Robotics")) {
                        filtered.add(conf);
                    }
                }
            }
        }

        if (isSavedOnlyFilter) {
            if (tvNoConferences != null) tvNoConferences.setVisibility(View.GONE);
            if (tvSavedHeaderSubtitle != null) {
                if (filtered.isEmpty()) {
                    tvSavedHeaderSubtitle.setText("No conferences bookmarked yet");
                } else {
                    tvSavedHeaderSubtitle.setText(filtered.size()
                            + (filtered.size() == 1 ? " saved conference • Track your submission milestones"
                                    : " saved conferences • Track your submission milestones"));
                }
            }

            if (filtered.isEmpty()) {
                if (rvConferences != null)
                    rvConferences.setVisibility(View.GONE);
                if (layoutEmptySaved != null)
                    layoutEmptySaved.setVisibility(View.VISIBLE);
            } else {
                if (rvConferences != null)
                    rvConferences.setVisibility(View.VISIBLE);
                if (layoutEmptySaved != null)
                    layoutEmptySaved.setVisibility(View.GONE);
            }
        } else {
            if (rvConferences != null) rvConferences.setVisibility(View.VISIBLE);
            if (layoutEmptySaved != null) layoutEmptySaved.setVisibility(View.GONE);
            if (tvNoConferences != null) {
                tvNoConferences.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
                tvNoConferences.setText(allConferences.isEmpty()
                        ? "No conferences found. Import scraped conferences from the backend and try again."
                        : "No conferences match this category.");
            }
        }

        if (adapter != null) {
            List<Conference> displayed = filtered;
            if (!isSavedOnlyFilter && filtered.size() > HOME_CONFERENCE_LIMIT) {
                displayed = filtered.subList(0, HOME_CONFERENCE_LIMIT);
            }
            adapter.setConferences(displayed);
        }
    }

    private void setupListeners() {
        // Notification bell click
        FrameLayout btnTopNotifications = findViewById(R.id.btnTopNotifications);
        if (btnTopNotifications != null) {
            btnTopNotifications.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, NotificationsActivity.class);
                startActivity(intent);
            });
        }

        // Search Bar click
        if (layoutSearchBar != null) {
            layoutSearchBar.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            });
        }

        // Filter button
        View btnFilterSearch = findViewById(R.id.btnFilterSearch);
        if (btnFilterSearch != null) {
            btnFilterSearch.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            });
        }

        // See All click
        if (tvSeeAll != null) {
            tvSeeAll.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            });
        }

        // Empty state explore button
        if (btnExploreConferences != null) {
            btnExploreConferences.setOnClickListener(v -> selectBottomTab("Home"));
        }

        // Bottom Navigation Tabs
        navTabHome.setOnClickListener(v -> selectBottomTab("Home"));

        navTabSaved.setOnClickListener(v -> selectBottomTab("Saved"));

        navTabNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        navTabProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("USER_NAME", userName);
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
        });
    }

    @Override
    public void onConferenceClick(Conference conference) {
        Intent intent = new Intent(MainActivity.this, ConferenceDetailActivity.class);
        intent.putExtra(ConferenceDetailActivity.EXTRA_CONFERENCE, conference);
        startActivity(intent);
    }
}