package com.example.gogo.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;

import com.example.gogo.R;
import com.example.gogo.repository.ExerciseCompletionRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ExerciseStatsActivity extends AppCompatActivity {
    private TextView totalCaloriesText, totalExercisesText, totalDurationText;
    private int userId = 1; // Giả định userId, bạn có thể thay đổi theo logic đăng nhập

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

        loadStats();
    }

    private void loadStats() {
        ExerciseCompletionRepository repo = new ExerciseCompletionRepository(this);

        // Tính khoảng thời gian 7 ngày gần nhất
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        String endDate = sdf.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        String startDate = sdf.format(calendar.getTime());

        int totalCalories = repo.getTotalCaloriesBurnedInWeek(userId, startDate, endDate);
        int totalExercises = repo.getTotalExercisesCompletedInWeek(userId, startDate, endDate);
        int totalDuration = repo.getTotalDurationInWeek(userId, startDate, endDate);

        totalCaloriesText.setText("Tổng calories đốt cháy: " + totalCalories + " kcal");
        totalExercisesText.setText("Số bài tập hoàn thành: " + totalExercises);
        totalDurationText.setText("Tổng thời gian tập: " + totalDuration + " phút");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}