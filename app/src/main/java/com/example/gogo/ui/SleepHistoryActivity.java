package com.example.gogo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gogo.R;
import com.example.gogo.adapters.SleepHistoryAdapter;
import com.example.gogo.database.SleepDAO;
import com.google.android.material.button.MaterialButton; // Thêm import cho MaterialButton

public class SleepHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView tvEmptyState;
    private SleepDAO sleepDAO;
    private MaterialButton btnStats; // Thay ImageButton thành MaterialButton

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);

        recyclerView = findViewById(R.id.recycler_view);
        tvEmptyState = findViewById(R.id.tv_empty_state);
        btnStats = findViewById(R.id.btn_stats); // Ánh xạ MaterialButton
        sleepDAO = new SleepDAO(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Xử lý sự kiện nhấn nút thống kê
        btnStats.setOnClickListener(v -> {
            Intent intent = new Intent(SleepHistoryActivity.this, SleepStatsActivity.class);
            startActivity(intent);
        });

        loadSleepRecords();
    }

    private void loadSleepRecords() {
        var records = sleepDAO.getAllSleepRecords();
        if (records.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(new SleepHistoryAdapter(records));
        }
    }
}