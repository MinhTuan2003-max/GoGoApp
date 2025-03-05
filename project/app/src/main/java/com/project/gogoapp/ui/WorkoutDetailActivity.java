package com.project.gogoapp.ui;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.project.gogoapp.R;
import com.project.gogoapp.database.DatabaseHelper;

import android.widget.ImageButton; // Thêm import này

public class WorkoutDetailActivity extends AppCompatActivity {

    private TextView tvExerciseName, tvDescription;
    private ImageButton btnBack; // Khai báo nút back
    private DatabaseHelper dbHelper;
    private int workoutId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_detail);
        getSupportActionBar().hide();

        tvExerciseName = findViewById(R.id.tvExerciseName);
        tvDescription = findViewById(R.id.tvDescription);
        btnBack = findViewById(R.id.btnBack); // Ánh xạ nút back

        dbHelper = new DatabaseHelper(this);
        workoutId = getIntent().getIntExtra("WORKOUT_ID", -1);

        if (workoutId != -1) {
            loadWorkoutDetails();
        } else {
            Toast.makeText(this, "Lỗi khi tải bài tập", Toast.LENGTH_SHORT).show();
        }

        // Xử lý khi bấm nút Back
        btnBack.setOnClickListener(view -> onBackPressed());
    }

    private void loadWorkoutDetails() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Exercise WHERE ExerciseID = ?",
                new String[]{String.valueOf(workoutId)});

        if (cursor.moveToFirst()) {
            String name = cursor.getString(1);
            String description = cursor.getString(2);
            tvExerciseName.setText(name);
            tvDescription.setText(description);
        } else {
            Toast.makeText(this, "Không tìm thấy bài tập", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        db.close();
    }
}
