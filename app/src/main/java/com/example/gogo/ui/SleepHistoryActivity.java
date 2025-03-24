package com.example.gogo.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gogo.R;
import com.example.gogo.adapters.SleepHistoryAdapter;
import com.example.gogo.database.SleepDao;

public class SleepHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView tvEmptyState;
    private SleepDao sleepDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);

        recyclerView = findViewById(R.id.recycler_view);
        tvEmptyState = findViewById(R.id.tv_empty_state);
        sleepDao = new SleepDao(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadSleepRecords();
    }

    private void loadSleepRecords() {
        var records = sleepDao.getAllSleepRecords();
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