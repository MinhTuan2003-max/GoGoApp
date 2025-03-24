package com.example.gogo.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.adapters.UpdateProfileInfoAdapter;
import com.example.gogo.respository.HealthRepository;

public class UpdateProfileInfoActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageView btnBack;
    private HealthRepository healthRepository;
    private UpdateProfileInfoAdapter adapter;
    private String googleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_info);

        recyclerView = findViewById(R.id.recyclerView);
        btnBack = findViewById(R.id.btnBack);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        healthRepository = new HealthRepository(this);
        googleId = getIntent().getStringExtra("GOOGLE_ID");

        if (googleId == null) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UpdateProfileInfoAdapter(this, googleId, healthRepository);
        recyclerView.setAdapter(adapter);

        btnBack.setOnClickListener(v -> {
            setResult(RESULT_CANCELED); // Đặt RESULT_CANCELED khi bấm nút Back
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (healthRepository != null) {
            healthRepository.close();
        }
    }
}