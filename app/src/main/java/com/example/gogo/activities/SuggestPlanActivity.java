package com.example.gogo.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.adapters.SuggestedExerciseAdapter;
import com.example.gogo.models.Exercise;
import com.example.gogo.models.ExercisePlan;
import com.example.gogo.repository.ExercisePlanRepository;
import com.example.gogo.repository.ExerciseRepository;

import java.util.ArrayList;
import java.util.List;

public class SuggestPlanActivity extends AppCompatActivity implements SuggestedExerciseAdapter.OnAddToPlanClickListener {
    private EditText targetCaloriesInput;
    private Button btnSuggestPlan;
    private RecyclerView recyclerView;
    private SuggestedExerciseAdapter adapter;
    private int userId = 1; // Giả định userId
    private ExercisePlanRepository planRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest_plan);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Gợi ý kế hoạch");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        planRepo = new ExercisePlanRepository(this);
        targetCaloriesInput = findViewById(R.id.target_calories);
        btnSuggestPlan = findViewById(R.id.btn_suggest_plan);
        recyclerView = findViewById(R.id.recycler_view_suggested_exercises);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnSuggestPlan.setOnClickListener(v -> suggestPlan());
    }

    private void suggestPlan() {
        String targetCaloriesStr = targetCaloriesInput.getText().toString();
        if (targetCaloriesStr.isEmpty()) {
            targetCaloriesInput.setError("Vui lòng nhập mục tiêu calories");
            return;
        }

        int targetCalories;
        try {
            targetCalories = Integer.parseInt(targetCaloriesStr);
        } catch (NumberFormatException e) {
            targetCaloriesInput.setError("Vui lòng nhập số hợp lệ");
            return;
        }

        ExerciseRepository repo = new ExerciseRepository(this);
        List<Exercise> allExercises = repo.getAllExercises();
        List<SuggestedExercise> suggestedExercises = new ArrayList<>();

        int remainingCalories = targetCalories;
        for (Exercise exercise : allExercises) {
            if (remainingCalories <= 0) {
                break;
            }

            double caloriesPerMinute = exercise.getEnergyConsumed();
            int maxCaloriesForThisExercise = (int) (targetCalories * 0.5);
            int caloriesToBurn = Math.min(remainingCalories, maxCaloriesForThisExercise);
            int minutes = (int) Math.ceil(caloriesToBurn / caloriesPerMinute);

            if (minutes > 0) {
                suggestedExercises.add(new SuggestedExercise(exercise, minutes, (int) (caloriesPerMinute * minutes)));
                remainingCalories -= (int) (caloriesPerMinute * minutes);
            }
        }

        if (remainingCalories > 0 && !suggestedExercises.isEmpty()) {
            SuggestedExercise lastExercise = suggestedExercises.get(suggestedExercises.size() - 1);
            double caloriesPerMinute = lastExercise.exercise.getEnergyConsumed();
            int additionalMinutes = (int) Math.ceil(remainingCalories / caloriesPerMinute);
            lastExercise.minutes += additionalMinutes;
            lastExercise.caloriesBurned += (int) (caloriesPerMinute * additionalMinutes);
        }

        adapter = new SuggestedExerciseAdapter(suggestedExercises, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onAddToPlanClick(SuggestedExercise suggestedExercise) {
        // Lấy danh sách PlanName hiện có
        List<String> existingPlanNames = planRepo.getPlanNamesByUserId(userId);
        List<String> planOptions = new ArrayList<>();
        planOptions.add("Tạo kế hoạch mới...");
        planOptions.addAll(existingPlanNames);

        // Tạo layout cho hộp thoại
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_select_plan, null);
        Spinner planSpinner = dialogView.findViewById(R.id.plan_spinner);
        EditText planNameInput = dialogView.findViewById(R.id.plan_name_input);
        TextView durationText = dialogView.findViewById(R.id.duration_text);

        // Thiết lập Spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, planOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        planSpinner.setAdapter(spinnerAdapter);

        // Hiển thị số phút đã tính toán
        durationText.setText("Thời gian: " + suggestedExercise.minutes + " phút");

        // Ẩn EditText nếu chọn kế hoạch hiện có
        planSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { // "Tạo kế hoạch mới..."
                    planNameInput.setVisibility(View.VISIBLE);
                } else {
                    planNameInput.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                planNameInput.setVisibility(View.VISIBLE);
            }
        });

        new AlertDialog.Builder(this)
                .setTitle("Thêm vào kế hoạch")
                .setView(dialogView)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String selectedPlan = (String) planSpinner.getSelectedItem();
                    String planName;

                    if (selectedPlan.equals("Tạo kế hoạch mới...")) {
                        planName = planNameInput.getText().toString();
                        if (planName.isEmpty()) {
                            planNameInput.setError("Vui lòng nhập tên kế hoạch");
                            return;
                        }
                    } else {
                        planName = selectedPlan;
                    }

                    // Tạo ExercisePlan và thêm vào cơ sở dữ liệu
                    ExercisePlan plan = new ExercisePlan(
                            0, // PlanID (tự động tăng)
                            userId,
                            suggestedExercise.exercise.getExerciseID(),
                            suggestedExercise.minutes,
                            suggestedExercise.caloriesBurned,
                            planName,
                            null // CreatedAt (tự động lấy thời gian hiện tại)
                    );
                    planRepo.addPlan(plan);

                    new AlertDialog.Builder(this)
                            .setTitle("Thành công")
                            .setMessage("Đã thêm " + suggestedExercise.exercise.getExerciseName() + " vào kế hoạch " + planName)
                            .setPositiveButton("OK", null)
                            .show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // Lớp để lưu trữ bài tập gợi ý cùng số phút và calories
    public static class SuggestedExercise {
        public Exercise exercise;
        public int minutes;
        public int caloriesBurned;

        SuggestedExercise(Exercise exercise, int minutes, int caloriesBurned) {
            this.exercise = exercise;
            this.minutes = minutes;
            this.caloriesBurned = caloriesBurned;
        }
    }
}