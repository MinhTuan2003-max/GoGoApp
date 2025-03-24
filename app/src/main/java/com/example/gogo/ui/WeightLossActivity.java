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

public class WeightLossActivity extends AppCompatActivity {

    private EditText targetWeightInput;
    private Button calculateButton;
    private TextView resultTextView;
    private LineChart barChart;
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
        setContentView(R.layout.activity_weight_loss);

        targetWeightInput = findViewById(R.id.targetWeightInput);
        calculateButton = findViewById(R.id.calculateButton);
        resultTextView = findViewById(R.id.resultTextView);
        barChart = findViewById(R.id.barChart);
        btnBack = findViewById(R.id.btnBack);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        notesTextView = findViewById(R.id.notesTextView);
        currentWeightTextView = findViewById(R.id.currentWeightValue);

        databaseHelper = new DatabaseHelper(this);
        accountDAO = new AccountDAO(databaseHelper);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        User user = accountDAO.getUserByGoogleId(account.getId());
        currentWeightTextView.setText(user.getWeight() + " " + "kg");

        btnBack.setOnClickListener(v -> finish());

        calculateButton.setOnClickListener(v -> calculateWeightLoss());

        setupBottomNavigation();
    }

    private void calculateWeightLoss() {
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
        if (currentWeight <= targetWeight) {
            Toast.makeText(this, "Cân nặng mong muốn phải nhỏ hơn cân nặng hiện tại!", Toast.LENGTH_SHORT).show();
            return;
        }

        double weightToLose = currentWeight - targetWeight;
        double caloriesPerKg = 7700;
        double totalCaloriesToLose = weightToLose * caloriesPerKg;
        double dailyCalorieDeficit = 1500;
        double daysToLose = totalCaloriesToLose / dailyCalorieDeficit;
        int weeksToLose = (int) Math.ceil(daysToLose / 7);
        int monthsToLose = (int) Math.ceil(daysToLose / 30);

        String timeUnit;
        int timeUnitsToLose;
        double stepSize;
        if (daysToLose < 7) {
            timeUnit = "ngày";
            timeUnitsToLose = (int) Math.ceil(daysToLose);
            stepSize = 1;
        } else if (weeksToLose > 6) {
            timeUnit = "tháng";
            timeUnitsToLose = monthsToLose;
            stepSize = daysToLose / monthsToLose;
        } else {
            timeUnit = "tuần";
            timeUnitsToLose = weeksToLose;
            stepSize = 7;
        }

        // Hiển thị kết quả
        DecimalFormat df = new DecimalFormat("#.##");
        String result = "Cân nặng mong muốn: " + df.format(targetWeight) + " kg\n" +
                "Thời gian ước tính: " + timeUnitsToLose + " " + timeUnit;
        resultTextView.setText(result);
        resultTextView.setVisibility(View.VISIBLE);

        String notes = getString(R.string.weight_loss_notes).replace("[Sẽ cập nhật khi tính toán]", df.format(currentWeight) + " kg");
        notesTextView.setText(notes);

        drawAreaChart(timeUnitsToLose, timeUnit, currentWeight, targetWeight, stepSize);
    }

    private void drawAreaChart(int timeUnitsToLose, String timeUnit, double currentWeight, double targetWeight, double stepSize) {
        List<Entry> entries = new ArrayList<>();
        List<String> xAxisLabels = new ArrayList<>();
        double dailyWeightLoss = (currentWeight - targetWeight) / (timeUnitsToLose * stepSize);
        double totalDays = timeUnitsToLose * stepSize;

        for (int unit = 0; unit <= timeUnitsToLose; unit++) {
            double daysPassed = unit * stepSize;
            double remainingWeight = currentWeight - (dailyWeightLoss * daysPassed);
            if (remainingWeight < targetWeight) remainingWeight = targetWeight;
            entries.add(new Entry((float) unit, (float) remainingWeight));
            if (unit == 1) xAxisLabels.add(timeUnit.equals("ngày") ? "Ngày 1" : timeUnit.equals("tuần") ? "Tuần 1" : "Tháng 1");
            else if (unit == timeUnitsToLose / 2) xAxisLabels.add("Thời gian");
            else if (unit == timeUnitsToLose) xAxisLabels.add(timeUnit.equals("ngày") ? "Ngày " + timeUnitsToLose : timeUnit.equals("tuần") ? "Tuần " + timeUnitsToLose : "Tháng " + timeUnitsToLose);
            else xAxisLabels.add("");
        }

        LineDataSet dataSet = new LineDataSet(entries, "Cân nặng còn lại (kg)");
        dataSet.setColor(getResources().getColor(android.R.color.holo_blue_dark));
        dataSet.setValueTextColor(getResources().getColor(android.R.color.black));
        dataSet.setValueTextSize(10f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(getResources().getColor(android.R.color.holo_blue_light));

        LineData lineData = new LineData(dataSet);
        barChart.setData(lineData);
        barChart.getDescription().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(timeUnitsToLose + 1);
        xAxis.setTextSize(10f);

        barChart.getAxisLeft().setAxisMinimum((float) targetWeight);
        barChart.getAxisLeft().setAxisMaximum((float) currentWeight);
        barChart.getAxisLeft().setLabelCount(5, true);
        barChart.invalidate();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(WeightLossActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(WeightLossActivity.this, ViewProfileActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(WeightLossActivity.this, SettingActivity.class));
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