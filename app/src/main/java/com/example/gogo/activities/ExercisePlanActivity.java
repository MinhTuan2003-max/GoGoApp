package com.example.gogo.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;

import com.example.gogo.R;
import com.example.gogo.adapters.SectionedPlanAdapter;
import com.example.gogo.models.ExercisePlan;
import com.example.gogo.repository.ExercisePlanRepository;

import java.util.ArrayList;
import java.util.List;

public class ExercisePlanActivity extends AppCompatActivity implements SectionedPlanAdapter.OnPlanGroupClickListener {
    private RecyclerView recyclerView;
    private int userId;
    private Button btnStats, btnSuggestPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_plan);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Kế hoạch tập luyện");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnStats = findViewById(R.id.btn_stats);
        btnSuggestPlan = findViewById(R.id.btn_suggest_plan);
        recyclerView = findViewById(R.id.recycler_view_plans);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userId = getIntent().getIntExtra("USER_ID", -1);

        btnStats.setOnClickListener(v -> {
            Intent intent = new Intent(this, ExerciseStatsActivity.class);
            startActivity(intent);
        });

        btnSuggestPlan.setOnClickListener(v -> {
            Intent intent = new Intent(this, SuggestPlanActivity.class);
            startActivity(intent);
        });

        loadPlans();
    }

    private void loadPlans() {
        ExercisePlanRepository repo = new ExercisePlanRepository(this);
        List<String> planNames = repo.getPlanNamesByUserId(userId);
        List<SectionedPlanAdapter.Section> sections = new ArrayList<>();

        for (String planName : planNames) {
            List<ExercisePlan> plans = repo.getPlansByPlanName(planName, userId);
            sections.add(new SectionedPlanAdapter.Section(planName, plans));
        }

        SectionedPlanAdapter adapter = new SectionedPlanAdapter(sections, this, repo);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onPlanGroupClick(String planName) {
        Intent intent = new Intent(this, ExercisePlanDetailActivity.class);
        intent.putExtra("PLAN_NAME", planName);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPlans();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}