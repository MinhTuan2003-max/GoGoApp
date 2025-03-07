package com.example.gogo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gogo.R;
import com.example.gogo.database.SleepDao;
import com.example.gogo.models.SleepRecord;
import com.example.gogo.utils.TimeUtils;

public class SleepInputActivity extends AppCompatActivity {
    private DatePicker datePicker;
    private TimePicker sleepTimePicker, wakeUpTimePicker;
    private SleepDao sleepDao;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_history);

        // Khởi tạo các thành phần giao diện
        datePicker = findViewById(R.id.date_picker);
        sleepTimePicker = findViewById(R.id.sleep_time_picker);
        wakeUpTimePicker = findViewById(R.id.wake_up_time_picker);
        Button btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);

        sleepDao = new SleepDao(this);

        // Xử lý sự kiện cho nút "Back"
        btnBack.setOnClickListener(v -> finish());

        // Xử lý sự kiện cho nút "Lưu thông tin"
        btnSave.setOnClickListener(v -> saveSleepRecord());
    }

    private void saveSleepRecord() {
        // Lấy dữ liệu từ các picker
        String date = String.format("%02d/%02d/%d",
                datePicker.getDayOfMonth(), datePicker.getMonth() + 1, datePicker.getYear());
        String sleepTime = String.format("%02d:%02d",
                sleepTimePicker.getHour(), sleepTimePicker.getMinute());
        String wakeUpTime = String.format("%02d:%02d",
                wakeUpTimePicker.getHour(), wakeUpTimePicker.getMinute());
        float sleepHours = TimeUtils.calculateSleepHours(sleepTime, wakeUpTime);

        // Tạo và lưu SleepRecord
        SleepRecord record = new SleepRecord(0, date, sleepTime, wakeUpTime, sleepHours);
        long result = sleepDao.insertSleepRecord(record); // Lấy kết quả từ insert

        // Kiểm tra xem dữ liệu có được lưu thành công không
        if (result != -1) {
            Toast.makeText(this, "Đã lưu thông tin giấc ngủ!", Toast.LENGTH_SHORT).show();
            // Chuyển sang SleepHistoryActivity
            Intent intent = new Intent(this, SleepHistoryActivity.class);
            startActivity(intent);
            finish(); // Kết thúc activity hiện tại sau khi chuyển
        } else {
            Toast.makeText(this, "Lỗi khi lưu thông tin giấc ngủ!", Toast.LENGTH_SHORT).show();
        }
    }
}