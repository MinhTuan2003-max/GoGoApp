package com.example.gogo.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.adapters.WorkoutAdapter;
import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.models.Workout;

import java.util.ArrayList;
import java.util.List;

public class WorkoutListActivity extends AppCompatActivity {

    private RecyclerView rvWorkoutList;
    private WorkoutAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<Workout> workoutList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_list);
        getSupportActionBar().hide();
        rvWorkoutList = findViewById(R.id.rvWorkoutList);
        rvWorkoutList.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterWorkouts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadWorkouts();
    }

    private void loadWorkouts() {
        workoutList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Exercise", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String description = cursor.getString(2);
                workoutList.add(new Workout(id, name, description));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        adapter = new WorkoutAdapter(workoutList, this::onWorkoutClick);
        rvWorkoutList.setAdapter(adapter);
    }

    private void filterWorkouts(String query) {
        List<Workout> filteredList = new ArrayList<>();
        for (Workout workout : workoutList) {
            if (workout.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(workout);
            }
        }
        adapter.updateList(filteredList);
    }


    private void onWorkoutClick(Workout workout) {
        Intent intent = new Intent(this, WorkoutDetailActivity.class);
        intent.putExtra("WORKOUT_ID", workout.getId());
        startActivity(intent);
    }
}
