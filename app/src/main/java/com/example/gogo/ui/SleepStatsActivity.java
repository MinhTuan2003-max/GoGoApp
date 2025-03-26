package com.example.gogo.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gogo.R;
import com.example.gogo.database.SleepDAO;
import com.example.gogo.models.SleepRecord;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class SleepStatsActivity extends AppCompatActivity {
    private BarChart barChart;
    private TextView tvEmptyState;
    private SleepDAO sleepDAO;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_stats);

        barChart = findViewById(R.id.bar_chart);
        tvEmptyState = findViewById(R.id.tv_empty_state);
        btnBack = findViewById(R.id.btn_back);
        sleepDAO = new SleepDAO(this);

        btnBack.setOnClickListener(v -> finish());

        loadStats();
    }

    private void loadStats() {
        List<SleepRecord> records = sleepDAO.getAllSleepRecords();
        if (records.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            barChart.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            barChart.setVisibility(View.VISIBLE);
            setupBarChart(records);
        }
    }

    private void setupBarChart(List<SleepRecord> records) {
        // Tạo danh sách các giá trị cho biểu đồ
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < records.size(); i++) {
            SleepRecord record = records.get(i);
            entries.add(new BarEntry(i, record.getSleepHours()));
            labels.add(record.getDate());
        }

        // Tạo dataset cho biểu đồ
        BarDataSet dataSet = new BarDataSet(entries, "Số giờ ngủ");
        dataSet.setColor(getResources().getColor(R.color.yellow)); // Màu vàng #FFEB3B
        dataSet.setValueTextColor(getResources().getColor(R.color.white)); // Màu chữ trắng
        dataSet.setValueTextSize(12f);

        // Tạo BarData từ dataset
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f); // Độ rộng của cột

        // Cấu hình biểu đồ
        barChart.setData(barData);
        barChart.setFitBars(true); // Điều chỉnh kích thước cột để vừa với biểu đồ
        barChart.getDescription().setEnabled(false); // Tắt mô tả
        barChart.getLegend().setTextColor(getResources().getColor(R.color.white)); // Màu chữ của legend

        // Cấu hình trục X (ngày)
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setPosition(com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setTextColor(getResources().getColor(R.color.white));
        barChart.getXAxis().setGranularity(1f); // Khoảng cách giữa các nhãn
        barChart.getXAxis().setLabelRotationAngle(45f); // Xoay nhãn 45 độ để dễ đọc

        // Cấu hình trục Y (số giờ)
        barChart.getAxisLeft().setTextColor(getResources().getColor(R.color.white));
        barChart.getAxisRight().setEnabled(false); // Tắt trục Y bên phải
        barChart.getAxisLeft().setAxisMinimum(0f); // Giá trị tối thiểu của trục Y là 0

        // Cấu hình thêm
        barChart.setDrawGridBackground(false);
        barChart.setDrawBorders(false);
        barChart.animateY(1000); // Hiệu ứng xuất hiện

        barChart.invalidate(); // Cập nhật biểu đồ
    }
}