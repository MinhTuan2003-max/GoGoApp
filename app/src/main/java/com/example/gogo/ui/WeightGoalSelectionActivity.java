package com.example.gogo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gogo.R;

public class WeightGoalSelectionActivity extends AppCompatActivity {
    private Button btnWeightLoss, btnWeightGain;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_goal_selection);

        btnWeightLoss = findViewById(R.id.btnWeightLoss);
        btnWeightGain = findViewById(R.id.btnWeightGain);
        btnBack = findViewById(R.id.btnBack);

        btnWeightLoss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeightGoalSelectionActivity.this, WeightLossActivity.class);
                startActivity(intent);
            }
        });

        btnWeightGain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeightGoalSelectionActivity.this, WeightGainActivity.class);
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }
}
