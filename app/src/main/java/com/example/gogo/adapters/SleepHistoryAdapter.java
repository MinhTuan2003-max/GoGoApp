package com.example.gogo.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.database.SleepDAO;
import com.example.gogo.models.SleepRecord;
import com.example.gogo.ui.SleepInputActivity;

import java.util.List;

public class SleepHistoryAdapter extends RecyclerView.Adapter<SleepHistoryAdapter.ViewHolder> {
    private List<SleepRecord> sleepRecords;
    private SleepDAO sleepDAO;
    private Context context;

    public SleepHistoryAdapter(Context context, List<SleepRecord> sleepRecords) {
        this.context = context;
        this.sleepRecords = sleepRecords;
        this.sleepDAO = new SleepDAO(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sleep_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SleepRecord record = sleepRecords.get(position);
        holder.tvDate.setText(record.getDate());
        holder.tvSleepTime.setText("Đi ngủ: " + record.getSleepTime());
        holder.tvWakeUpTime.setText("Thức dậy: " + record.getWakeUpTime());
        holder.tvSleepHours.setText(String.format("%.1f giờ", record.getSleepHours()));

        holder.btnDelete.setOnClickListener(v -> {
            boolean deleted = sleepDAO.deleteSleepRecord(record.getId());
            if (deleted) {
                sleepRecords.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, sleepRecords.size());
                Toast.makeText(context, "Đã xóa bản ghi giấc ngủ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Xóa bản ghi thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SleepInputActivity.class);
            intent.putExtra("USER_ID", record.getUser().getUserId());
            intent.putExtra("SLEEP_RECORD_ID", record.getId());
            intent.putExtra("DATE", record.getDate());
            intent.putExtra("BED_TIME", record.getSleepTime());
            intent.putExtra("WAKE_TIME", record.getWakeUpTime());
            intent.putExtra("SLEEP_HOURS", record.getSleepHours());
            ((AppCompatActivity) context).startActivityForResult(intent, 1);
        });
    }

    @Override
    public int getItemCount() {
        return sleepRecords.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvSleepTime, tvWakeUpTime, tvSleepHours;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvSleepTime = itemView.findViewById(R.id.tv_sleep_time);
            tvWakeUpTime = itemView.findViewById(R.id.tv_wake_up_time);
            tvSleepHours = itemView.findViewById(R.id.tv_sleep_hours);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}