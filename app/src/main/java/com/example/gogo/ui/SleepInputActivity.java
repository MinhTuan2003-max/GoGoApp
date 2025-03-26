package com.example.gogo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gogo.R;
import com.example.gogo.database.AccountDAO;
import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.database.SleepDAO;
import com.example.gogo.models.SleepRecord;
import com.example.gogo.models.User;
import com.example.gogo.utils.TimeUtils;

public class SleepInputActivity extends AppCompatActivity {
    private DatePicker datePicker;
    private TimePicker sleepTimePicker, wakeUpTimePicker;
    private SleepDAO sleepDao;
    private AccountDAO accountDAO;
    private ImageButton btnBack;
    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_history);

        datePicker = findViewById(R.id.date_picker);
        sleepTimePicker = findViewById(R.id.sleep_time_picker);
        wakeUpTimePicker = findViewById(R.id.wake_up_time_picker);
        Button btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);

        sleepDao = new SleepDAO(this);
        accountDAO = new AccountDAO(new DatabaseHelper(this));

        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveSleepRecord());
    }

    private void saveSleepRecord() {
        User user = accountDAO.getUserById(userId);
        if (user == null) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = String.format("%02d/%02d/%d",
                datePicker.getDayOfMonth(), datePicker.getMonth() + 1, datePicker.getYear());
        String sleepTime = String.format("%02d:%02d",
                sleepTimePicker.getHour(), sleepTimePicker.getMinute());
        String wakeUpTime = String.format("%02d:%02d",
                wakeUpTimePicker.getHour(), wakeUpTimePicker.getMinute());
        float sleepHours = TimeUtils.calculateSleepHours(sleepTime, wakeUpTime);

        SleepRecord record = new SleepRecord(0, user, date, sleepTime, wakeUpTime, sleepHours);
        long result = sleepDao.insertSleepRecord(record);

        if (result != -1) {
            String message = String.format("Đã lưu thông tin giấc ngủ! Bạn đã ngủ %.1f giờ.", sleepHours);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, SleepHistoryActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Lỗi khi lưu thông tin giấc ngủ!", Toast.LENGTH_SHORT).show();
        }
    }
}