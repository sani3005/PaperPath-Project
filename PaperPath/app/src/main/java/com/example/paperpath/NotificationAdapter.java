package com.example.paperpath;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotifViewHolder> {

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationItem item);
    }

    private List<NotificationItem> list;
    private final OnNotificationClickListener listener;

    public NotificationAdapter(List<NotificationItem> list, OnNotificationClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<NotificationItem> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotifViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifViewHolder holder, int position) {
        NotificationItem item = list.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class NotifViewHolder extends RecyclerView.ViewHolder {
        private final FrameLayout layoutNotifIconBg;
        private final ImageView ivNotifIcon;
        private final TextView tvNotifTitle;
        private final TextView tvNotifMessage;
        private final TextView tvNotifTime;

        public NotifViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutNotifIconBg = itemView.findViewById(R.id.layoutNotifIconBg);
            ivNotifIcon = itemView.findViewById(R.id.ivNotifIcon);
            tvNotifTitle = itemView.findViewById(R.id.tvNotifTitle);
            tvNotifMessage = itemView.findViewById(R.id.tvNotifMessage);
            tvNotifTime = itemView.findViewById(R.id.tvNotifTime);
        }

        public void bind(NotificationItem item, OnNotificationClickListener listener) {
            tvNotifTitle.setText(item.getTitle());
            tvNotifMessage.setText(item.getMessage());
            tvNotifTime.setText(item.getTime());

            layoutNotifIconBg.setBackgroundResource(item.getBgRes());
            ivNotifIcon.setImageResource(item.getIconRes());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNotificationClick(item);
                }
            });
        }
    }
}
