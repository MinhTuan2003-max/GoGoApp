package com.example.gogo.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.adapters.PlanDetailAdapter;
import com.example.gogo.models.ExerciseCompletion;
import com.example.gogo.models.ExercisePlan;
import com.example.gogo.repository.ExerciseCompletionRepository;
import com.example.gogo.repository.ExercisePlanRepository;

import java.util.List;

public class ExercisePlanDetailActivity extends AppCompatActivity implements PlanDetailAdapter.OnExerciseDeletedListener {
    private RecyclerView recyclerView;
    private TextView totalCaloriesText;
    private Button btnCompleteAll, btnUndoAll;
    private PlanDetailAdapter adapter;
    private ExercisePlanRepository planRepo;
    private ExerciseCompletionRepository completionRepo;
    private String planName;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_plan_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        planName = getIntent().getStringExtra("PLAN_NAME");
        userId = getIntent().getIntExtra("USER_ID", -1);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(planName != null ? planName : "Chi tiết kế hoạch");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (userId == -1 || planName == null) {
            finish();
            return;
        }

        totalCaloriesText = findViewById(R.id.total_calories);
        recyclerView = findViewById(R.id.recycler_view_plan_details);
        btnCompleteAll = findViewById(R.id.btn_complete_all);
        btnUndoAll = findViewById(R.id.btn_undo_all);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        planRepo = new ExercisePlanRepository(this);
        completionRepo = new ExerciseCompletionRepository(this);

        loadPlanDetails();

        btnCompleteAll.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc chắn muốn đánh dấu toàn bộ bài tập là hoàn thành không?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        markAllAsCompleted();
                        loadPlanDetails();
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });

        btnUndoAll.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc chắn muốn đánh dấu toàn bộ bài tập là chưa hoàn thành không?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        markAllAsUncompleted();
                        loadPlanDetails();
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });
    }

    private void loadPlanDetails() {
        List<ExercisePlan> plans = planRepo.getPlansByPlanName(planName, userId);
        adapter = new PlanDetailAdapter(plans, planRepo, completionRepo, this);
        recyclerView.setAdapter(adapter);
        updateTotalCalories();
    }

    private void updateTotalCalories() {
        int totalCalories = planRepo.getTotalCaloriesByPlanName(planName, userId);
        totalCaloriesText.setText("Tổng calories: " + totalCalories + " kcal");
    }

    private void markAllAsCompleted() {
        List<ExercisePlan> plans = planRepo.getPlansByPlanName(planName, userId);
        for (ExercisePlan plan : plans) {
            if (!completionRepo.isExerciseCompleted(plan.getPlanID())) {
                ExerciseCompletion completion = new ExerciseCompletion(
                        0, plan.getPlanID(), plan.getUserID(), plan.getExerciseID(),
                        plan.getCaloriesBurned(), null, plan.getDuration()
                );
                completionRepo.addCompletion(completion);
            }
        }
    }

    private void markAllAsUncompleted() {
        List<ExercisePlan> plans = planRepo.getPlansByPlanName(planName, userId);
        for (ExercisePlan plan : plans) {
            if (completionRepo.isExerciseCompleted(plan.getPlanID())) {
                completionRepo.deleteCompletion(plan.getPlanID());
            }
        }
    }

    @Override
    public void onExerciseDeleted() {
        loadPlanDetails();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPlanDetails();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}