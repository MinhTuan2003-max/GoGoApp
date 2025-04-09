package com.example.gogo.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.gogo.R;
import com.example.gogo.repository.ExerciseCompletionRepository;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExerciseStatsActivity extends AppCompatActivity {
    private TextView totalCaloriesText, totalExercisesText, totalDurationText, totalDaysText;
    private PieChart pieChart;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_stats);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Thống kê tập luyện");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        totalCaloriesText = findViewById(R.id.total_calories);
        totalExercisesText = findViewById(R.id.total_exercises);
        totalDurationText = findViewById(R.id.total_duration);
        totalDaysText = findViewById(R.id.total_days);
        pieChart = findViewById(R.id.pie_chart);

        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) {
            finish();
            return;
        }

        loadStats();
        setupPieChart();
    }

    private void loadStats() {
        ExerciseCompletionRepository repo = new ExerciseCompletionRepository(this);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        String endDate = sdf.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        String startDate = sdf.format(calendar.getTime());

        int totalCalories = repo.getTotalCaloriesBurnedInWeek(userId, startDate, endDate);
        int totalExercises = repo.getTotalExercisesCompletedInWeek(userId, startDate, endDate);
        int totalDuration = repo.getTotalDurationInWeek(userId, startDate, endDate);
        int totalDays = repo.getTrainingDaysInWeek(userId, startDate, endDate);
        Map<String, Integer> caloriesByCategory = repo.getCaloriesByCategoryInWeek(userId, startDate, endDate);

        totalCaloriesText.setText("Tổng calories đốt cháy: " + totalCalories + " kcal");
        totalExercisesText.setText("Số bài tập hoàn thành: " + totalExercises);
        totalDurationText.setText("Tổng thời gian tập: " + totalDuration + " phút");
        totalDaysText.setText("Số ngày tập luyện: " + totalDays);

        updatePieChart(caloriesByCategory, totalCalories);
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setHoleRadius(58f);
        pieChart.setDrawCenterText(true);
        pieChart.setCenterText("Phân bố calories");
        pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setCenterTextSize(14f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.getLegend().setEnabled(true);
    }

    private void updatePieChart(Map<String, Integer> caloriesByCategory, int totalCalories) {
        List<PieEntry> entries = new ArrayList<>();
        if (totalCalories == 0) {
            entries.add(new PieEntry(100f, "Không có dữ liệu"));
        } else {
            for (Map.Entry<String, Integer> entry : caloriesByCategory.entrySet()) {
                float percentage = (entry.getValue() * 100f) / totalCalories;
                entries.add(new PieEntry(percentage, entry.getKey()));
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setData(data);
        pieChart.invalidate();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}