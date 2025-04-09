package com.example.gogo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SleepInputActivity extends AppCompatActivity {
    private DatePicker datePicker;
    private TimePicker sleepTimePicker, wakeUpTimePicker;
    private SleepDAO sleepDao;
    private AccountDAO accountDAO;
    private ImageButton btnBack;
    private TextView tvTitle;
    private int userId = -1;
    private int sleepRecordId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_history);

        datePicker = findViewById(R.id.date_picker);
        sleepTimePicker = findViewById(R.id.sleep_time_picker);
        wakeUpTimePicker = findViewById(R.id.wake_up_time_picker);
        Button btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);
        tvTitle = findViewById(R.id.tv_title);

        sleepDao = new SleepDAO(this);
        accountDAO = new AccountDAO(new DatabaseHelper(this));

        userId = getIntent().getIntExtra("USER_ID", -1);
        sleepRecordId = getIntent().getIntExtra("SLEEP_RECORD_ID", -1);

        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (sleepRecordId != -1) {
            tvTitle.setText("Cập nhật giấc ngủ");
            loadExistingData();
            btnSave.setOnClickListener(v -> updateSleepRecord());
        } else {
            tvTitle.setText("Thêm lịch sử giấc ngủ");
            btnSave.setOnClickListener(v -> saveSleepRecord());
        }

        btnBack.setOnClickListener(v -> finish());
        setupBottomNavigation();
    }

    private void loadExistingData() {
        String date = getIntent().getStringExtra("DATE");
        String bedTime = getIntent().getStringExtra("BED_TIME");
        String wakeTime = getIntent().getStringExtra("WAKE_TIME");

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(date));
            datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String[] bedParts = bedTime.split(":");
            sleepTimePicker.setHour(Integer.parseInt(bedParts[0]));
            sleepTimePicker.setMinute(Integer.parseInt(bedParts[1]));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String[] wakeParts = wakeTime.split(":");
            wakeUpTimePicker.setHour(Integer.parseInt(wakeParts[0]));
            wakeUpTimePicker.setMinute(Integer.parseInt(wakeParts[1]));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Lỗi khi lưu thông tin giấc ngủ!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSleepRecord() {
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

        SleepRecord record = new SleepRecord(sleepRecordId, user, date, sleepTime, wakeUpTime, sleepHours);
        boolean updated = sleepDao.updateSleepRecord(record);

        if (updated) {
            String message = String.format("Đã cập nhật thông tin giấc ngủ! Bạn đã ngủ %.1f giờ.", sleepHours);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Lỗi khi cập nhật thông tin giấc ngủ!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(SleepInputActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(SleepInputActivity.this, ViewProfileActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(SleepInputActivity.this, SettingActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }
}