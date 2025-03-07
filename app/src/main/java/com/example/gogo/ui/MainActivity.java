package com.example.gogo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gogo.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_main); // Sử dụng activity_main.xml thay vì activity_sleep_main

        // Khởi tạo các nút
        Button btnGetStarted = findViewById(R.id.getStartedButton);
        Button btnSleepInput = findViewById(R.id.btnSleepInput);

        // Xử lý sự kiện cho nút "Get Started" (chuyển sang WakeUpInputActivity)
        btnGetStarted.setOnClickListener(v -> {
            Intent intent = new Intent(this, WakeUpInputActivity.class);
            startActivity(intent);
        });

        // Xử lý sự kiện cho nút "Nhập lịch sử giấc ngủ" (chuyển sang SleepInputActivity)
        btnSleepInput.setOnClickListener(v -> {
            Intent intent = new Intent(this, SleepInputActivity.class);
            startActivity(intent);
        });
    }
}