package com.example.gogo.adapters;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.models.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<Notification> items;

    public NotificationAdapter(List<Notification> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout container = new LinearLayout(parent.getContext());
        container.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(dpToPx(parent.getResources(), 8), dpToPx(parent.getResources(), 8),
                dpToPx(parent.getResources(), 8), dpToPx(parent.getResources(), 8));
        return new ViewHolder(container);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification item = items.get(position);

        holder.container.removeAllViews();

        // Nội dung thông báo
        TextView messageText = new TextView(holder.container.getContext());
        messageText.setText(item.getMessage());
        messageText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        messageText.setTextColor(ContextCompat.getColor(holder.container.getContext(), R.color.black));
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        messageText.setLayoutParams(textParams);
        messageText.setMaxLines(Integer.MAX_VALUE);
        messageText.setSingleLine(false);
        messageText.setEllipsize(null);
        messageText.setHorizontallyScrolling(false);
        Resources resources = holder.container.getContext().getResources();
        if (item.getIsRead() == 0) {
            messageText.setTypeface(null, Typeface.BOLD);
            messageText.setTextColor(resources.getColor(R.color.black));
        } else {
            messageText.setTextColor(resources.getColor(R.color.gray_color));
        }
        holder.container.addView(messageText);

        // Thời gian
        if (!item.getNotifyTime().isEmpty()) {
            TextView timeText = new TextView(holder.container.getContext());
            timeText.setText(item.getNotifyTime());
            timeText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            timeText.setTextColor(resources.getColor(R.color.gray_color));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.topMargin = dpToPx(resources, 4);
            holder.container.addView(timeText, params);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container;

        public ViewHolder(LinearLayout container) {
            super(container);
            this.container = container;
        }
    }

    private int dpToPx(Resources resources, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }
}