package com.example.gogo.activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.adapters.PlanDetailAdapter;
import com.example.gogo.models.ExercisePlan;
import com.example.gogo.repository.ExerciseCompletionRepository;
import com.example.gogo.repository.ExercisePlanRepository;

import java.util.List;

public class ExercisePlanDetailActivity extends AppCompatActivity implements PlanDetailAdapter.OnExerciseDeletedListener {
    private RecyclerView recyclerView;
    private TextView totalCaloriesText;
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
        if (getSupportActionBar() != null) {
            planName = getIntent().getStringExtra("PLAN_NAME");
            getSupportActionBar().setTitle(planName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        totalCaloriesText = findViewById(R.id.total_calories);
        recyclerView = findViewById(R.id.recycler_view_plan_details);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userId = getIntent().getIntExtra("USER_ID", -1);
        planRepo = new ExercisePlanRepository(this);
        completionRepo = new ExerciseCompletionRepository(this);
        loadPlanDetails();
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

    @Override
    public void onExerciseDeleted() {
        updateTotalCalories();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}