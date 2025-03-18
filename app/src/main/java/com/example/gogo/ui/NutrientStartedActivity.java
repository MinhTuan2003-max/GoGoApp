package com.example.gogo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gogo.R;

public class NutrientStartedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrients_start);

        // Tham chiếu đến nút "Get Started"
        Button getStartedButton = findViewById(R.id.getStartedButton);

        // Xử lý sự kiện khi nút "Get Started" được nhấn
        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển hướng đến NutrientSelectActivity
                Intent intent = new Intent(NutrientStartedActivity.this, NutrientSelectActivity.class);
                startActivity(intent);

                // Áp dụng animation
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }
}