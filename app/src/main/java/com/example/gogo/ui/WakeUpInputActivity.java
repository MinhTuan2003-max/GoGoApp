package com.example.gogo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gogo.R;

public class WakeUpInputActivity extends AppCompatActivity {
    private TimePicker timePicker;
    private ImageButton btnBack;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_up); // Sử dụng layout activity_wake_up.xml

        // Khởi tạo các thành phần giao diện
        timePicker = findViewById(R.id.time_picker);
        btnBack = findViewById(R.id.btn_back);
        btnSubmit = findViewById(R.id.btn_submit);

        // Xử lý sự kiện cho nút "Quay lại"
        btnBack.setOnClickListener(v -> finish());

        // Xử lý sự kiện cho nút "Xác nhận"
        btnSubmit.setOnClickListener(v -> showSleepSuggestions());
    }

    private void showSleepSuggestions() {
        // Lấy giờ thức dậy từ TimePicker
        String wakeUpTime = String.format("%02d:%02d",
                timePicker.getHour(), timePicker.getMinute());

        // Chuyển sang SleepSuggestionActivity và truyền giờ thức dậy
        Intent intent = new Intent(this, SleepSuggestionActivity.class);
        intent.putExtra("wake_up_time", wakeUpTime);
        startActivity(intent);
    }
}