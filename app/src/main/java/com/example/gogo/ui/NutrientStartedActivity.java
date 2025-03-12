package com.example.gogo.ui;

import static android.app.ProgressDialog.show;

import android.graphics.BlurMaskFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class NutrientStartedActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_nutrients_start);
//            // Tham chiếu đến BottomNavigationView
//            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
//
//            // Xử lý sự kiện khi chọn một mục
//            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//                @Override
//                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                    switch (item.getItemId()) {
//                        case R.id.nav_home:
//                            // Xử lý khi chọn Home
//                            Toast.makeText( MainActivity.this, "Home Selected", Toast.LENGTH_SHORT).show();
//                            return true;
//                        case R.id.nav_profile:
//                            // Xử lý khi chọn Profile0
//                            Toast.makeText(MainActivity.this, "Profile Selected", Toast.LENGTH_SHORT).show();
//                            return true;
//                        case R.id.nav_settings:
//                            // Xử lý khi chọn Settings
//                            Toast.makeText(MainActivity.this, "Settings Selected", Toast.LENGTH_SHORT).show();
//                            return true;
//                    }
//                    return false;
//                }
//            });

        }
    }
