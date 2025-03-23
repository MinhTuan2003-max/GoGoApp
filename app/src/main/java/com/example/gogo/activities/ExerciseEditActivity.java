package com.example.gogo.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.gogo.R;
import com.example.gogo.models.Exercise;
import com.example.gogo.repository.ExerciseRepository;

public class ExerciseEditActivity extends AppCompatActivity {
    private EditText editName, editDescription, editEnergy, editDuration, editEquipment;
    private Spinner spinnerCategory, spinnerDifficulty;
    private Button btnUpdate, btnDelete;
    private Exercise exercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_edit);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chỉnh sửa bài tập");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút "Quay lại"
        }

        editName = findViewById(R.id.edit_exercise_name);
        editDescription = findViewById(R.id.edit_exercise_description);
        editEnergy = findViewById(R.id.edit_energy_consumed);
        editDuration = findViewById(R.id.edit_standard_duration);
        editEquipment = findViewById(R.id.edit_equipment);
        spinnerCategory = findViewById(R.id.spinner_category);
        spinnerDifficulty = findViewById(R.id.spinner_difficulty);
        btnUpdate = findViewById(R.id.btn_update);
        btnDelete = findViewById(R.id.btn_delete);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.exercise_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        ArrayAdapter<CharSequence> difficultyAdapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_levels, android.R.layout.simple_spinner_item);
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(difficultyAdapter);

        int exerciseId = getIntent().getIntExtra("EXERCISE_ID", -1);
        ExerciseRepository repo = new ExerciseRepository(this);
        exercise = repo.getExerciseById(exerciseId);

        if (exercise != null) {
            editName.setText(exercise.getExerciseName());
            editDescription.setText(exercise.getDescription());
            editEnergy.setText(String.valueOf(exercise.getEnergyConsumed()));
            editDuration.setText(String.valueOf(exercise.getStandardDuration()));
            editEquipment.setText(exercise.getEquipmentRequired());
            spinnerCategory.setSelection(categoryAdapter.getPosition(exercise.getCategory()));
            spinnerDifficulty.setSelection(difficultyAdapter.getPosition(exercise.getDifficultyLevel()));
        }

        btnUpdate.setOnClickListener(v -> updateExercise());
        btnDelete.setOnClickListener(v -> deleteExercise());
    }

    private void updateExercise() {
        exercise = new Exercise(
                exercise.getExerciseID(),
                editName.getText().toString(),
                spinnerCategory.getSelectedItem().toString(),
                editDescription.getText().toString(),
                Double.parseDouble(editEnergy.getText().toString()),
                "kcal",
                Integer.parseInt(editDuration.getText().toString()),
                spinnerDifficulty.getSelectedItem().toString(),
                editEquipment.getText().toString(),
                exercise.getCreatedAt()
        );
        ExerciseRepository repo = new ExerciseRepository(this);
        repo.updateExercise(exercise);
        finish();
    }

    private void deleteExercise() {
        ExerciseRepository repo = new ExerciseRepository(this);
        repo.deleteExercise(exercise.getExerciseID());
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Quay lại màn hình trước đó (ExerciseListActivity)
        return true;
    }
}