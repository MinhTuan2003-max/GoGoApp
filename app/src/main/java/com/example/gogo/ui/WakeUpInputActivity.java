package com.example.gogo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gogo.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class WakeUpInputActivity extends AppCompatActivity {
    private TimePicker timePicker;
    private ImageButton btnBack;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_up);

        timePicker = findViewById(R.id.time_picker);
        btnBack = findViewById(R.id.btn_back);
        btnSubmit = findViewById(R.id.btn_submit);

        btnBack.setOnClickListener(v -> finish());

        btnSubmit.setOnClickListener(v -> showSleepSuggestions());

        setupBottomNavigation();
    }

    private void showSleepSuggestions() {

        String wakeUpTime = String.format("%02d:%02d",
                timePicker.getHour(), timePicker.getMinute());

        Intent intent = new Intent(this, SleepSuggestionActivity.class);
        intent.putExtra("wake_up_time", wakeUpTime);
        startActivity(intent);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(WakeUpInputActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(WakeUpInputActivity.this, ViewProfileActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(WakeUpInputActivity.this, SettingActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }
}