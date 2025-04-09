package com.example.gogo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gogo.R;
import com.example.gogo.database.AccountDAO;
import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.database.SleepDAO;
import com.example.gogo.models.SleepRecord;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class SleepStatsActivity extends AppCompatActivity {
    private BarChart barChart;
    private TextView tvEmptyState;
    private ImageButton btnBack;
    private SleepDAO sleepDAO;
    private DatabaseHelper dbHelper;
    private AccountDAO accountDAO;
    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_stats);

        barChart = findViewById(R.id.bar_chart);
        tvEmptyState = findViewById(R.id.tv_empty_state);
        btnBack = findViewById(R.id.btn_back);

        dbHelper = new DatabaseHelper(this);
        accountDAO = new AccountDAO(dbHelper);
        sleepDAO = new SleepDAO(this);

        // Lấy userId từ Intent hoặc Google Sign-In
        Intent intent = getIntent();
        userId = intent.getIntExtra("USER_ID", -1);
        if (userId == -1) {
            loadUserData();
        }

        if (userId == -1) {
            Intent loginIntent = new Intent(this, MainActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
            return;
        }

        btnBack.setOnClickListener(v -> finish());

        loadStats();

        setupBottomNavigation();
    }

    private void loadUserData() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            com.example.gogo.models.User user = accountDAO.getUserByGoogleId(account.getId());
            if (user != null) {
                userId = user.getUserId();
            } else {
                userId = accountDAO.getUserIdByGoogleId(account.getId());
            }
        }
    }

    private void loadStats() {
        List<SleepRecord> records = sleepDAO.getSleepRecordsByUserId(userId);
        if (records.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            barChart.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            barChart.setVisibility(View.VISIBLE);
            setupBarChart(records);
        }
    }

    private void setupBarChart(List<SleepRecord> records) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < records.size(); i++) {
            SleepRecord record = records.get(i);
            entries.add(new BarEntry(i, record.getSleepHours()));
            labels.add(record.getDate());
        }

        BarDataSet dataSet = new BarDataSet(entries, "Số giờ ngủ");
        dataSet.setColor(getResources().getColor(R.color.tdee_low));
        dataSet.setValueTextColor(getResources().getColor(R.color.white));
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);

        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setTextColor(getResources().getColor(R.color.white));

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setPosition(com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setTextColor(getResources().getColor(R.color.white));
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setLabelRotationAngle(45f);

        barChart.getAxisLeft().setTextColor(getResources().getColor(R.color.white));
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setAxisMinimum(0f);

        barChart.setDrawGridBackground(false);
        barChart.setDrawBorders(false);
        barChart.animateY(1000);

        barChart.invalidate();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(SleepStatsActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(SleepStatsActivity.this, ViewProfileActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(SleepStatsActivity.this, SettingActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }
}