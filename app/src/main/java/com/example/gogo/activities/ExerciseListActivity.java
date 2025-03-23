package com.example.gogo.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.adapters.ExerciseAdapter;
import com.example.gogo.models.Exercise;
import com.example.gogo.repository.ExercisePlanRepository;
import com.example.gogo.repository.ExerciseRepository;

import java.util.List;

public class ExerciseListActivity extends AppCompatActivity implements ExerciseAdapter.OnExerciseClickListener, ExerciseAdapter.OnAddToPlanClickListener {
    private RecyclerView recyclerView;
    private ExerciseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Danh sách bài tập");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recycler_view_exercises);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadExercises();
    }

    private void loadExercises() {
        ExerciseRepository repo = new ExerciseRepository(this);
        List<Exercise> exercises = repo.getAllExercises();
        adapter = new ExerciseAdapter(exercises, this, this);
        adapter.setPlanRepository(new ExercisePlanRepository(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onExerciseClick(Exercise exercise) {
        Intent intent = new Intent(this, ExerciseEditActivity.class);
        intent.putExtra("EXERCISE_ID", exercise.getExerciseID());
        startActivity(intent);
    }

    @Override
    public void onAddToPlanClick(Exercise exercise) {
        // Có thể hiển thị thông báo hoặc chuyển hướng đến ExercisePlanActivity
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExercises();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}