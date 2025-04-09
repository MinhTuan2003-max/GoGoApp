package com.example.gogo.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.gogo.R;
import com.example.gogo.models.Exercise;
import com.example.gogo.repository.ExerciseRepository;

public class ExerciseAddActivity extends AppCompatActivity {
    private EditText editName, editDescription, editEnergy, editDuration, editEquipment;
    private Spinner spinnerCategory, spinnerDifficulty;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_add);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Thêm bài tập");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        editName = findViewById(R.id.edit_exercise_name);
        editDescription = findViewById(R.id.edit_exercise_description);
        editEnergy = findViewById(R.id.edit_energy_consumed);
        editDuration = findViewById(R.id.edit_standard_duration);
        editEquipment = findViewById(R.id.edit_equipment);
        spinnerCategory = findViewById(R.id.spinner_category);
        spinnerDifficulty = findViewById(R.id.spinner_difficulty);
        btnSave = findViewById(R.id.btn_save);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.exercise_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        ArrayAdapter<CharSequence> difficultyAdapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_levels, android.R.layout.simple_spinner_item);
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(difficultyAdapter);

        btnSave.setOnClickListener(v -> saveExercise());
    }

    private void saveExercise() {
        String name = editName.getText().toString();
        String category = spinnerCategory.getSelectedItem().toString();
        String description = editDescription.getText().toString();
        double energy = Double.parseDouble(editEnergy.getText().toString());
        int duration = Integer.parseInt(editDuration.getText().toString());
        String difficulty = spinnerDifficulty.getSelectedItem().toString();
        String equipment = editEquipment.getText().toString();

        Exercise exercise = new Exercise(0, name, category, description, energy, "kcal", duration, difficulty, equipment, null);
        ExerciseRepository repo = new ExerciseRepository(this);
        repo.addExercise(exercise);
        finish();
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}