package com.example.paperpath;

import android.content.Intent;
import android.os.Bundle;
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

public class SearchActivity extends AppCompatActivity implements SearchConferenceAdapter.OnItemClickListener {

    private ImageView btnSearchBack;
    private EditText etSearchQuery;
    private ImageView btnClearSearch;
    private TextView tabAll;
    private TextView tabUpcoming;
    private TextView tabOngoing;
    private TextView tabPast;
    private RecyclerView rvSearchResults;
    private SearchConferenceAdapter adapter;
    private List<Conference> searchList;
    private String selectedTab = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        setupList();
        setupTabs();
        setupListeners();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        filterResults();
    }

    private void setupList() {
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        searchList = ConferenceRepository.getAllConferences();
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterResults();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        findViewById(R.id.btnFloatingFilter).setOnClickListener(v -> {
            Toast.makeText(this, "Filters: Showing " + adapter.getItemCount() + " results", Toast.LENGTH_SHORT).show();
        });
    }

    private void filterResults() {
        String query = etSearchQuery.getText().toString().trim().toLowerCase();
        List<Conference> filtered = new ArrayList<>();

        for (Conference conf : searchList) {
            boolean matchesTab = "All".equals(selectedTab) || conf.getStatus().equalsIgnoreCase(selectedTab);
            boolean matchesQuery = query.isEmpty() ||
                    conf.getAcronym().toLowerCase().contains(query) ||
                    conf.getFullName().toLowerCase().contains(query) ||
                    conf.getTopics().toLowerCase().contains(query) ||
                    conf.getVenue().toLowerCase().contains(query);

            if (matchesTab && matchesQuery) {
                filtered.add(conf);
            }
        }
        adapter.updateList(filtered);
    }

    @Override
    public void onItemClick(Conference conference) {
        Intent intent = new Intent(SearchActivity.this, ConferenceDetailActivity.class);
        intent.putExtra(ConferenceDetailActivity.EXTRA_CONFERENCE, conference);
        startActivity(intent);
    }
}
