package com.example.gogo.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.adapters.MainMenuAdapter;
import com.example.gogo.models.MainMenuItem;
import com.example.gogo.ui.HomeActivity;
import com.example.gogo.ui.SettingActivity;
import com.example.gogo.ui.ViewProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HealthMenuActivity extends AppCompatActivity implements MainMenuAdapter.OnMenuItemClickListener {
    private RecyclerView recyclerView;
    private BottomNavigationView bottomNavigationView;
    private final int userId = 1;
    private boolean isInitialSelection = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_menu);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Duyệt");
        }

        recyclerView = findViewById(R.id.recycler_view_menu);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<MainMenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MainMenuItem("Danh sách bài tập", R.drawable.ic_activity, ExerciseListActivity.class));
        menuItems.add(new MainMenuItem("Thêm bài tập", R.drawable.ic_add, ExerciseAddActivity.class));
        menuItems.add(new MainMenuItem("Kế hoạch tập luyện", R.drawable.ic_plan, ExercisePlanActivity.class));

        MainMenuAdapter adapter = new MainMenuAdapter(menuItems, this);
        recyclerView.setAdapter(adapter);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (isInitialSelection) {
                isInitialSelection = false;
                return true;
            }

            int itemId = item.getItemId();
            if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(HealthMenuActivity.this, ViewProfileActivity.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_home) {
                Intent intent = new Intent(HealthMenuActivity.this, HomeActivity.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_settings) {
                Intent intent = new Intent(HealthMenuActivity.this, SettingActivity.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
                return true;
            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    @Override
    public void onMenuItemClick(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        if (activityClass == ExercisePlanActivity.class) {
            intent.putExtra("USER_ID", 1);
        }
        startActivity(intent);
    }
}