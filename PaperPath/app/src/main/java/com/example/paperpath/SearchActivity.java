package com.example.paperpath;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements SearchConferenceAdapter.OnItemClickListener {

    private ImageView btnSearchBack;
    private EditText etSearchQuery;
    private ImageView btnClearSearch;
    private TextView tabAll;
    private TextView tabUpcoming;
    private TextView tabOngoing;
    private TextView tabPast;
    private RecyclerView rvSearchResults;
    private TextView tvSearchEmpty;
    private SearchConferenceAdapter adapter;
    private List<Conference> searchList = new ArrayList<>();
    private String selectedTab = "All";
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable pendingSearch;
    private int searchRequestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        setupList();
        setupTabs();
        setupListeners();
        loadConferences("");
    }

    private void initViews() {
        btnSearchBack = findViewById(R.id.btnSearchBack);
        etSearchQuery = findViewById(R.id.etSearchQuery);
        btnClearSearch = findViewById(R.id.btnClearSearch);
        tabAll = findViewById(R.id.tabAll);
        tabUpcoming = findViewById(R.id.tabUpcoming);
        tabOngoing = findViewById(R.id.tabOngoing);
        tabPast = findViewById(R.id.tabPast);
        rvSearchResults = findViewById(R.id.rvSearchResults);
        tvSearchEmpty = findViewById(R.id.tvSearchEmpty);
    }

    private void setupList() {
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchConferenceAdapter(searchList, this);
        rvSearchResults.setAdapter(adapter);
    }

    private void setupTabs() {
        tabAll.setOnClickListener(v -> selectTab("All"));
        tabUpcoming.setOnClickListener(v -> selectTab("Upcoming"));
        tabOngoing.setOnClickListener(v -> selectTab("Ongoing"));
        tabPast.setOnClickListener(v -> selectTab("Past"));
    }

    private void selectTab(String tab) {
        selectedTab = tab;
        updateTabButton(tabAll, "All".equals(tab));
        updateTabButton(tabUpcoming, "Upcoming".equals(tab));
        updateTabButton(tabOngoing, "Ongoing".equals(tab));
        updateTabButton(tabPast, "Past".equals(tab));
        filterResults();
    }

    private void updateTabButton(TextView tabView, boolean isActive) {
        if (tabView == null) return;
        tabView.setBackgroundResource(isActive ? R.drawable.bg_tab_pill_active : R.drawable.bg_tab_pill_inactive);
        tabView.setTextColor(getResources().getColor(isActive ? R.color.text_white : R.color.text_secondary, null));
    }

    private void setupListeners() {
        btnSearchBack.setOnClickListener(v -> finish());
        btnClearSearch.setOnClickListener(v -> etSearchQuery.setText(""));

        etSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                scheduleSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        findViewById(R.id.btnFloatingFilter).setOnClickListener(v ->
                Toast.makeText(this, "Showing " + adapter.getItemCount() + " conferences", Toast.LENGTH_SHORT).show());
    }

    private void scheduleSearch(String query) {
        if (pendingSearch != null) searchHandler.removeCallbacks(pendingSearch);
        pendingSearch = () -> loadConferences(query.trim());
        searchHandler.postDelayed(pendingSearch, 300);
    }

    private void loadConferences(String query) {
        int requestId = ++searchRequestId;
        Call<List<Conference>> request = query.isEmpty()
                ? RetrofitClient.getApiService().getConferences()
                : RetrofitClient.getApiService().searchConferences(query);
        request.enqueue(new Callback<List<Conference>>() {
            @Override
            public void onResponse(Call<List<Conference>> call, Response<List<Conference>> response) {
                if (requestId != searchRequestId || isFinishing() || isDestroyed()) return;
                if (!response.isSuccessful() || response.body() == null) {
                    searchList = new ArrayList<>();
                    filterResults();
                    Toast.makeText(
                            SearchActivity.this,
                            "Search failed (HTTP " + response.code() + ")",
                            Toast.LENGTH_LONG
                    ).show();
                    return;
                }
                searchList = response.body();
                filterResults();
            }

            @Override
            public void onFailure(Call<List<Conference>> call, Throwable error) {
                if (requestId != searchRequestId || isFinishing() || isDestroyed()) return;
                searchList = new ArrayList<>();
                filterResults();
                Toast.makeText(
                        SearchActivity.this,
                        "Cannot reach the conference server. Check that the backend is running.",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void filterResults() {
        List<Conference> filtered = new ArrayList<>();
        for (Conference conference : searchList) {
            if ("All".equals(selectedTab) || conference.getStatus().equalsIgnoreCase(selectedTab)) {
                filtered.add(conference);
            }
        }
        adapter.updateList(filtered);
        tvSearchEmpty.setVisibility(filtered.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    @Override
    public void onItemClick(Conference conference) {
        Intent intent = new Intent(SearchActivity.this, ConferenceDetailActivity.class);
        intent.putExtra(ConferenceDetailActivity.EXTRA_CONFERENCE, conference);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        searchHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
