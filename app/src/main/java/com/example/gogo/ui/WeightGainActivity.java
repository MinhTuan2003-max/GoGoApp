package com.example.gogo.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gogo.R;
import com.example.gogo.database.AccountDAO;
import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.models.User;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class WeightGainActivity extends AppCompatActivity {

    private EditText targetWeightInput;
    private Button calculateButton;
    private TextView resultTextView;
    private LineChart lineChart;
    private ImageView btnBack;
    private TextView currentWeightTextView;
    private BottomNavigationView bottomNavigationView;
    private AccountDAO accountDAO;
    private DatabaseHelper databaseHelper;
    private TextView notesTextView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_gain);

        targetWeightInput = findViewById(R.id.targetWeightInput);
        calculateButton = findViewById(R.id.calculateButton);
        resultTextView = findViewById(R.id.resultTextView);
        lineChart = findViewById(R.id.barChart);
        btnBack = findViewById(R.id.btnBack);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        notesTextView = findViewById(R.id.notesTextView);
        currentWeightTextView = findViewById(R.id.currentWeightValue);

        databaseHelper = new DatabaseHelper(this);
        accountDAO = new AccountDAO(databaseHelper);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        User user = accountDAO.getUserByGoogleId(account.getId());
        currentWeightTextView.setText(user.getWeight() + " kg");

        btnBack.setOnClickListener(v -> finish());

        calculateButton.setOnClickListener(v -> calculateWeightGain());

        setupBottomNavigation();
    }

    private void calculateWeightGain() {
        String targetWeightStr = targetWeightInput.getText().toString().trim();
        if (targetWeightStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập cân nặng mong muốn!", Toast.LENGTH_SHORT).show();
            return;
        }

        double targetWeight = Double.parseDouble(targetWeightStr);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account == null || account.getId() == null) {
            Toast.makeText(this, "Không thể lấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = accountDAO.getUserByGoogleId(account.getId());
        if (user == null) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng trong database!", Toast.LENGTH_SHORT).show();
            return;
        }

        double currentWeight = user.getWeight();
        if (currentWeight >= targetWeight) {
            Toast.makeText(this, "Cân nặng mong muốn phải lớn hơn cân nặng hiện tại!", Toast.LENGTH_SHORT).show();
            return;
        }

        double weightToGain = targetWeight - currentWeight;
        double caloriesPerKg = 7700;
        double totalCaloriesToGain = weightToGain * caloriesPerKg;
        double dailyCalorieSurplus = 900;
        double daysToGain = totalCaloriesToGain / dailyCalorieSurplus;
        int weeksToGain = (int) Math.ceil(daysToGain / 7);
        int monthsToGain = (int) Math.ceil(daysToGain / 30);

        String timeUnit;
        int timeUnitsToGain;
        double stepSize;
        if (daysToGain < 7) {
            timeUnit = "ngày";
            timeUnitsToGain = (int) Math.ceil(daysToGain);
            stepSize = 1;
        } else if (weeksToGain > 6) {
            timeUnit = "tháng";
            timeUnitsToGain = monthsToGain;
            stepSize = daysToGain / monthsToGain;
        } else {
            timeUnit = "tuần";
            timeUnitsToGain = weeksToGain;
            stepSize = 7;
        }

        DecimalFormat df = new DecimalFormat("#.##");
        String result = "Cân nặng mong muốn: " + df.format(targetWeight) + " kg\n" +
                "Thời gian ước tính: " + timeUnitsToGain + " " + timeUnit;
        resultTextView.setText(result);
        resultTextView.setVisibility(View.VISIBLE);

        String notes = "Để tăng cân từ " + df.format(currentWeight) + " kg lên " + df.format(targetWeight) +
                " kg, bạn cần duy trì thặng dư " + dailyCalorieSurplus + " calo mỗi ngày.";
        notesTextView.setText(notes);

        drawAreaChart(timeUnitsToGain, timeUnit, currentWeight, targetWeight, stepSize);
    }

    private void drawAreaChart(int timeUnitsToGain, String timeUnit, double currentWeight, double targetWeight, double stepSize) {
        List<Entry> entries = new ArrayList<>();
        List<String> xAxisLabels = new ArrayList<>();
        double dailyWeightGain = (targetWeight - currentWeight) / (timeUnitsToGain * stepSize);
        double totalDays = timeUnitsToGain * stepSize;

        for (int unit = 0; unit <= timeUnitsToGain; unit++) {
            double daysPassed = unit * stepSize;
            double currentWeightProgress = currentWeight + (dailyWeightGain * daysPassed);
            if (currentWeightProgress > targetWeight) currentWeightProgress = targetWeight;
            entries.add(new Entry((float) unit, (float) currentWeightProgress));
            if (unit == 1) xAxisLabels.add(timeUnit.equals("ngày") ? "Ngày 1" : timeUnit.equals("tuần") ? "Tuần 1" : "Tháng 1");
            else if (unit == timeUnitsToGain / 2) xAxisLabels.add("Thời gian");
            else if (unit == timeUnitsToGain) xAxisLabels.add(timeUnit.equals("ngày") ? "Ngày " + timeUnitsToGain : timeUnit.equals("tuần") ? "Tuần " + timeUnitsToGain : "Tháng " + timeUnitsToGain);
            else xAxisLabels.add("");
        }

        LineDataSet dataSet = new LineDataSet(entries, "Cân nặng (kg)");
        dataSet.setColor(getResources().getColor(android.R.color.holo_green_dark));
        dataSet.setValueTextColor(getResources().getColor(android.R.color.black));
        dataSet.setValueTextSize(10f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(getResources().getColor(android.R.color.holo_green_light));

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(timeUnitsToGain + 1);
        xAxis.setTextSize(10f);

        lineChart.getAxisLeft().setAxisMinimum((float) currentWeight);
        lineChart.getAxisLeft().setAxisMaximum((float) targetWeight);
        lineChart.getAxisLeft().setLabelCount(5, true);
        lineChart.invalidate();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(WeightGainActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(WeightGainActivity.this, ViewProfileActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(WeightGainActivity.this, SettingActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}