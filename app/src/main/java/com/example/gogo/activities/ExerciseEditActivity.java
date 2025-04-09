package com.example.gogo.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.gogo.R;
import com.example.gogo.models.Exercise;
import com.example.gogo.repository.ExercisePlanRepository;
import com.example.gogo.repository.ExerciseRepository;

public class ExerciseEditActivity extends AppCompatActivity {
    private EditText editName, editDescription, editEnergy, editDuration, editEquipment;
    private Spinner spinnerCategory, spinnerDifficulty;
    private Button btnUpdate, btnDelete;
    private Exercise exercise;
    private ExerciseRepository exerciseRepo;
    private ExercisePlanRepository planRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chỉnh sửa bài tập");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        exerciseRepo = new ExerciseRepository(this);
        planRepo = new ExercisePlanRepository(this);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.exercise_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        ArrayAdapter<CharSequence> difficultyAdapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_levels, android.R.layout.simple_spinner_item);
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(difficultyAdapter);

        int exerciseId = getIntent().getIntExtra("EXERCISE_ID", -1);
        exercise = exerciseRepo.getExerciseById(exerciseId);

        if (exercise != null) {
            editName.setText(exercise.getExerciseName());
            editDescription.setText(exercise.getDescription());
            editEnergy.setText(String.valueOf(exercise.getEnergyConsumed()));
            editDuration.setText(String.valueOf(exercise.getStandardDuration()));
            editEquipment.setText(exercise.getEquipmentRequired());
            spinnerCategory.setSelection(categoryAdapter.getPosition(exercise.getCategory()));
            spinnerDifficulty.setSelection(difficultyAdapter.getPosition(exercise.getDifficultyLevel()));
        } else {
            Toast.makeText(this, "Không tìm thấy bài tập", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnUpdate.setOnClickListener(v -> updateExercise());
        btnDelete.setOnClickListener(v -> confirmDeleteExercise());
    }

    private void updateExercise() {
        try {
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
            int rowsAffected = exerciseRepo.updateExercise(exercise);
            if (rowsAffected > 0) {
                Toast.makeText(this, "Cập nhật bài tập thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập số hợp lệ cho năng lượng và thời gian", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDeleteExercise() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa bài tập")
                .setMessage("Bạn có chắc chắn muốn xóa bài tập " + exercise.getExerciseName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteExercise())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteExercise() {
        if (planRepo.isExerciseInAnyPlan(exercise.getExerciseID())) {
            new AlertDialog.Builder(this)
                    .setTitle("Không thể xóa")
                    .setMessage("Bài tập " + exercise.getExerciseName() + " đang được sử dụng trong kế hoạch tập luyện.")
                    .setPositiveButton("OK", null)
                    .show();
        } else {
            if (exerciseRepo.deleteExercise(exercise.getExerciseID())) {
                Toast.makeText(this, "Đã xóa bài tập " + exercise.getExerciseName(), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Xóa bài tập thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}