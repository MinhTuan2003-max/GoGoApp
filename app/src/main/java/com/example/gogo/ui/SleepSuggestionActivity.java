package com.example.gogo.ui;

import android.os.Bundle;
import android.widget.ImageButton;  // Thêm import này
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gogo.R;
import com.example.gogo.utils.TimeUtils;

public class SleepSuggestionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest_sleep);

        ImageButton btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());

        String wakeUpTime = getIntent().getStringExtra("wake_up_time");
        if (wakeUpTime == null) wakeUpTime = "06:00";

        TextView tvWakeUpTime = findViewById(R.id.tv_wake_up_time);
        TextView tvSuggestion1 = findViewById(R.id.tv_suggestion_1);
        TextView tvSuggestion2 = findViewById(R.id.tv_suggestion_2);
        TextView tvSuggestion3 = findViewById(R.id.tv_suggestion_3);

        tvWakeUpTime.setText("Giờ thức dậy: " + wakeUpTime);
        tvSuggestion1.setText("Nếu bạn muốn ngủ 7.5 giờ, hãy đi ngủ lúc: " +
                TimeUtils.calculateSleepTime(wakeUpTime, 7.5f));
        tvSuggestion2.setText("Nếu bạn muốn ngủ 9 giờ, hãy đi ngủ lúc: " +
                TimeUtils.calculateSleepTime(wakeUpTime, 9f));
        tvSuggestion3.setText("Nếu bạn muốn ngủ 10.5 giờ, hãy đi ngủ lúc: " +
                TimeUtils.calculateSleepTime(wakeUpTime, 10.5f));
    }
}