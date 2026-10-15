package com.example.paperpath;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchConferenceAdapter extends RecyclerView.Adapter<SearchConferenceAdapter.SearchViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Conference conference);
    }

    private List<Conference> items;
    private final OnItemClickListener listener;

    public SearchConferenceAdapter(List<Conference> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<Conference> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_conference, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Conference conf = items.get(position);
        holder.bind(conf, listener);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvLogoAcronym;
        private final TextView tvSearchItemTitle;
        private final ImageView ivSearchBookmark;
        private final TextView tvSearchDates;
        private final TextView tvSearchVenue;
        private final TextView tvSearchTag1;
        private final TextView tvSearchTag2;
        private final TextView tvSearchTag3;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLogoAcronym = itemView.findViewById(R.id.tvLogoAcronym);
            tvSearchItemTitle = itemView.findViewById(R.id.tvSearchItemTitle);
            ivSearchBookmark = itemView.findViewById(R.id.ivSearchBookmark);
            tvSearchDates = itemView.findViewById(R.id.tvSearchDates);
            tvSearchVenue = itemView.findViewById(R.id.tvSearchVenue);
            tvSearchTag1 = itemView.findViewById(R.id.tvSearchTag1);
            tvSearchTag2 = itemView.findViewById(R.id.tvSearchTag2);
            tvSearchTag3 = itemView.findViewById(R.id.tvSearchTag3);
        }

        public void bind(Conference conf, OnItemClickListener listener) {
            tvLogoAcronym.setText(conf.getAcronym());
            tvSearchItemTitle.setText(conf.getAcronym() + " 2026");
            tvSearchDates.setText(conf.getConfDates());
            tvSearchVenue.setText(conf.getVenue());

            boolean isBookmarked = BookmarkManager.isBookmarked(itemView.getContext(), conf.getId());
            conf.setBookmarked(isBookmarked);
            updateBookmark(isBookmarked);
            ivSearchBookmark.setOnClickListener(v -> {
                boolean newState = !BookmarkManager.isBookmarked(v.getContext(), conf.getId());
                BookmarkManager.setBookmarked(v.getContext(), conf.getId(), newState);
                conf.setBookmarked(newState);
                updateBookmark(newState);
                if (newState) {
                    android.widget.Toast.makeText(v.getContext(), "Saved to Watchlist", android.widget.Toast.LENGTH_SHORT).show();
                } else {
                    android.widget.Toast.makeText(v.getContext(), "Removed from Saved", android.widget.Toast.LENGTH_SHORT).show();
                }
            });

            String[] tags = conf.getTags();
            if (tags.length > 0) tvSearchTag1.setText(tags[0]);
            if (tags.length > 1) {
                tvSearchTag2.setText(tags[1]);
                tvSearchTag2.setVisibility(View.VISIBLE);
            } else {
                tvSearchTag2.setVisibility(View.GONE);
            }
            if (tags.length > 2) {
                tvSearchTag3.setText(tags[2]);
                tvSearchTag3.setVisibility(View.VISIBLE);
            } else {
                tvSearchTag3.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(conf);
                }
            });
        }

        private void updateBookmark(boolean isBookmarked) {
            if (isBookmarked) {
                ivSearchBookmark.setImageResource(R.drawable.ic_bookmark_ribbon_filled);
                ivSearchBookmark.setColorFilter(null);
            } else {
                ivSearchBookmark.setImageResource(R.drawable.ic_bookmark_ribbon);
            }
        }
    }
}
