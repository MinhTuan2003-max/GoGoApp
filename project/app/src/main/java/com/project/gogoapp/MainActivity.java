package com.project.gogoapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.project.gogoapp.ui.WorkoutListActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        startActivity(new Intent(this, WorkoutListActivity.class));
        finish();
    }
}
