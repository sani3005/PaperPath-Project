package com.example.paperpath;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ConferenceAdapter extends RecyclerView.Adapter<ConferenceAdapter.ConferenceViewHolder> {

    public interface OnConferenceClickListener {
        void onConferenceClick(Conference conference);
    }

    public interface OnBookmarkToggleListener {
        void onBookmarkToggle(Conference conference, boolean isBookmarked);
    }

    private List<Conference> conferenceList;
    private final OnConferenceClickListener clickListener;
    private OnBookmarkToggleListener bookmarkListener;

    public ConferenceAdapter(List<Conference> conferenceList, OnConferenceClickListener clickListener) {
        this.conferenceList = conferenceList;
        this.clickListener = clickListener;
    }

    public void setOnBookmarkToggleListener(OnBookmarkToggleListener listener) {
        this.bookmarkListener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setConferences(List<Conference> list) {
        this.conferenceList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ConferenceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conference_card, parent, false);
        return new ConferenceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConferenceViewHolder holder, int position) {
        Conference conf = conferenceList.get(position);
        holder.bind(conf, clickListener, bookmarkListener);
    }

    @Override
    public int getItemCount() {
        return conferenceList != null ? conferenceList.size() : 0;
    }

    public static class ConferenceViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivConfBadge;
        private final TextView tvConfTitle;
        private final ImageView ivBookmarkAction;
        private final TextView tvConfDate;
        private final TextView tvConfVenue;
        private final TextView tvTag1;
        private final TextView tvTag2;
        private final TextView tvTag3;

        public ConferenceViewHolder(@NonNull View itemView) {
            super(itemView);
            ivConfBadge = itemView.findViewById(R.id.ivConfBadge);
            tvConfTitle = itemView.findViewById(R.id.tvConfTitle);
            ivBookmarkAction = itemView.findViewById(R.id.ivBookmarkAction);
            tvConfDate = itemView.findViewById(R.id.tvConfDate);
            tvConfVenue = itemView.findViewById(R.id.tvConfVenue);
            tvTag1 = itemView.findViewById(R.id.tvTag1);
            tvTag2 = itemView.findViewById(R.id.tvTag2);
            tvTag3 = itemView.findViewById(R.id.tvTag3);
        }

        public void bind(Conference conf, OnConferenceClickListener clickListener, OnBookmarkToggleListener bookmarkListener) {
            tvConfTitle.setText(conf.getFullName());
            tvConfDate.setText(conf.getConfDates());
            tvConfVenue.setText(conf.getVenue());

            // Badge icon based on publisher or acronym
            if (conf.getAcronym().contains("ACM") || conf.getFullName().contains("ACM")) {
                ivConfBadge.setImageResource(R.drawable.ic_badge_acm);
            } else {
                ivConfBadge.setImageResource(R.drawable.ic_badge_ieee);
            }

            // Sync with BookmarkManager
            boolean isBookmarked = BookmarkManager.isBookmarked(itemView.getContext(), conf.getId());
            conf.setBookmarked(isBookmarked);
            updateBookmarkIcon(isBookmarked);

            ivBookmarkAction.setOnClickListener(v -> {
                boolean newState = !BookmarkManager.isBookmarked(v.getContext(), conf.getId());
                BookmarkManager.setBookmarked(v.getContext(), conf.getId(), newState);
                conf.setBookmarked(newState);
                updateBookmarkIcon(newState);

                if (newState) {
                    Toast.makeText(v.getContext(), "Saved to Watchlist", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), "Removed from Saved", Toast.LENGTH_SHORT).show();
                }

                if (bookmarkListener != null) {
                    bookmarkListener.onBookmarkToggle(conf, newState);
                }
            });

            // Tags
            String[] tags = conf.getTags();
            if (tags.length > 0) tvTag1.setText(tags[0]);
            if (tags.length > 1) {
                tvTag2.setText(tags[1]);
                tvTag2.setVisibility(View.VISIBLE);
            } else {
                tvTag2.setVisibility(View.GONE);
            }
            if (tags.length > 2) {
                tvTag3.setText(tags[2]);
                tvTag3.setVisibility(View.VISIBLE);
            } else {
                tvTag3.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onConferenceClick(conf);
                }
            });
        }

        private void updateBookmarkIcon(boolean isBookmarked) {
            ivBookmarkAction.setImageTintList(null);
            if (isBookmarked) {
                ivBookmarkAction.setImageResource(R.drawable.ic_bookmark_star_filled);
            } else {
                ivBookmarkAction.setImageResource(R.drawable.ic_bookmark_star);
            }
        }
    }
}
