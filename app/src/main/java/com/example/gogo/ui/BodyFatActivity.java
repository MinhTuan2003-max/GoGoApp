package com.example.gogo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.gogo.R;
import com.example.gogo.database.AccountDAO;
import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.models.User;
import com.example.gogo.utils.BodyFatUtils;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BodyFatActivity extends AppCompatActivity {

    private TextView resultTextView;
    private PieChart pieChart;
    private ImageView btnBack;
    private BottomNavigationView bottomNavigationView;
    private AccountDAO accountDAO;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_fat);

        resultTextView = findViewById(R.id.resultTextView);
        pieChart = findViewById(R.id.pieChart);
        btnBack = findViewById(R.id.btnBack);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        databaseHelper = new DatabaseHelper(this);
        accountDAO = new AccountDAO(databaseHelper);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null && account.getId() != null) {
            User user = accountDAO.getUserByGoogleId(account.getId());
            if (user != null) {
                calculateAndDisplayBFP(user);
            } else {
                Toast.makeText(this, "Không tìm thấy người dùng!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Không thể lấy thông tin đăng nhập!", Toast.LENGTH_SHORT).show();
        }

        btnBack.setOnClickListener(v -> finish());

        setupBottomNavigation();
    }

    private void calculateAndDisplayBFP(User user) {
        double bfp = BodyFatUtils.calculateBFP(user);

        // Hiển thị BFP
        DecimalFormat df = new DecimalFormat("#.#");
        resultTextView.setText("Tỷ lệ mỡ cơ thể (BFP): " + df.format(bfp) + "%");

        Map<String, Object> evaluation = BodyFatUtils.evaluateHealthRisk(bfp, user);
        String status = (String) evaluation.get("status");
        String message = (String) evaluation.get("message");

        resultTextView.append("\n\n" + message);

        if (status.equals("normal")) {
            pieChart.setVisibility(View.GONE);
        } else {
            pieChart.setVisibility(View.VISIBLE);
            @SuppressWarnings("unchecked")
            Map<String, Float> risks = (Map<String, Float>) evaluation.get("risks");
            drawRiskPieChart(status.equals("high"), risks);
        }
    }

    private void drawRiskPieChart(boolean isHighBFP, Map<String, Float> risks) {
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        for (Map.Entry<String, Float> risk : risks.entrySet()) {
            entries.add(new PieEntry(risk.getValue(), risk.getKey()));
        }

        if (isHighBFP) {
            colors.add(ContextCompat.getColor(this, android.R.color.holo_red_dark));
            colors.add(ContextCompat.getColor(this, android.R.color.holo_orange_dark));
            colors.add(ContextCompat.getColor(this, android.R.color.holo_purple));
            colors.add(ContextCompat.getColor(this, android.R.color.holo_blue_light));
        } else {
            colors.add(ContextCompat.getColor(this, android.R.color.holo_red_light));
            colors.add(ContextCompat.getColor(this, android.R.color.holo_orange_light));
            colors.add(ContextCompat.getColor(this, android.R.color.holo_green_light));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Nguy cơ bệnh lý");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(ContextCompat.getColor(this, android.R.color.white));

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Nguy cơ (%)");
        pieChart.setCenterTextSize(14f);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.invalidate();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(BodyFatActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(BodyFatActivity.this, ViewProfileActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(BodyFatActivity.this, SettingActivity.class));
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