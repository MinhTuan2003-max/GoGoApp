package com.example.gogo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.models.SleepRecord;

import java.util.List;

public class SleepHistoryAdapter extends RecyclerView.Adapter<SleepHistoryAdapter.ViewHolder> {
    private List<SleepRecord> sleepRecords;

    public SleepHistoryAdapter(List<SleepRecord> sleepRecords) {
        this.sleepRecords = sleepRecords;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sleep_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SleepRecord record = sleepRecords.get(position);
        holder.tvDate.setText(record.getDate());
        holder.tvSleepTime.setText("Đi ngủ: " + record.getSleepTime());
        holder.tvWakeUpTime.setText("Thức dậy: " + record.getWakeUpTime());
        holder.tvSleepHours.setText(String.format("%.1f giờ", record.getSleepHours()));
    }

    @Override
    public int getItemCount() {
        return sleepRecords.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvSleepTime, tvWakeUpTime, tvSleepHours;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvSleepTime = itemView.findViewById(R.id.tv_sleep_time);
            tvWakeUpTime = itemView.findViewById(R.id.tv_wake_up_time);
            tvSleepHours = itemView.findViewById(R.id.tv_sleep_hours);
        }
    }
}