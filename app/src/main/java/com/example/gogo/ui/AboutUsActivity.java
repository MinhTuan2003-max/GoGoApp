package com.example.gogo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.adapters.AboutUsAdapter;
import com.example.gogo.models.AboutUsItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class AboutUsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AboutUsAdapter adapter;
    private List<AboutUsItem> itemList;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        backButton = findViewById(R.id.btnBack);

        itemList = new ArrayList<>();
        itemList.add(new AboutUsItem(R.drawable.our_story_icon, "Câu chuyện của chúng tôi", "Hành trình tạo ra ứng dụng giúp người dùng cải thiện sức khỏe mỗi ngày."));
        itemList.add(new AboutUsItem(R.drawable.our_mission_icon, "Sứ mệnh", "Mang đến giải pháp toàn diện để giúp mọi người theo dõi và nâng cao sức khỏe."));
        itemList.add(new AboutUsItem(R.drawable.our_vision_icon, "Tầm nhìn", "Trở thành ứng dụng sức khỏe hàng đầu, đồng hành cùng người dùng trên hành trình sống khỏe."));
        itemList.add(new AboutUsItem(R.drawable.core_values_icon, "Giá trị cốt lõi", "Lấy người dùng làm trung tâm, ứng dụng công nghệ để cá nhân hóa trải nghiệm."));
        itemList.add(new AboutUsItem(R.drawable.our_team_icon, "Đội ngũ của chúng tôi", "Nhóm phát triển tận tâm, luôn tìm cách cải tiến sản phẩm để mang lại trải nghiệm tốt nhất."));

        adapter = new AboutUsAdapter(this, itemList);
        recyclerView.setAdapter(adapter);

        backButton.setOnClickListener(v -> finish());

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(AboutUsActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(AboutUsActivity.this, ViewProfileActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(AboutUsActivity.this, SettingActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }
}

