package com.example.gogo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gogo.R;

public class SleepActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_main);

        Button btnGetStarted = findViewById(R.id.getStartedButton);
        Button btnSleepInput = findViewById(R.id.btnSleepInput);

        btnGetStarted.setOnClickListener(v -> {
            Intent intent = new Intent(this, WakeUpInputActivity.class);
            startActivity(intent);
        });

        btnSleepInput.setOnClickListener(v -> {
            Intent intent = new Intent(this, SleepInputActivity.class);
            startActivity(intent);
        });
    }
}
