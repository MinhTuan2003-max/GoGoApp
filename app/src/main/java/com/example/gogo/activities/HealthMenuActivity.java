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

import java.util.ArrayList;
import java.util.List;

public class HealthMenuActivity extends AppCompatActivity implements MainMenuAdapter.OnMenuItemClickListener {
    private RecyclerView recyclerView;

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