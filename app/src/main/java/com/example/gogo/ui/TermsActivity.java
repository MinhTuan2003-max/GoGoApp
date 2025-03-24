package com.example.gogo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.adapters.TermAdapter;
import com.example.gogo.models.TermItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class TermsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TermAdapter adapter;
    private ImageView btnBack;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        // Ánh xạ các thành phần giao diện
        recyclerView = findViewById(R.id.recyclerView);
        btnBack = findViewById(R.id.btnBack);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Thiết lập sự kiện cho nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Thiết lập RecyclerView
        setupRecyclerView();

        // Thiết lập BottomNavigationView
        setupBottomNavigation();
    }

    private void setupRecyclerView() {
        // Tạo danh sách các điều khoản bằng tiếng Việt
        List<TermItem> items = new ArrayList<>();
        items.add(new TermItem("1. Chấp nhận điều khoản",
                "Bằng việc sử dụng ứng dụng GoGo, bạn đồng ý tuân thủ các điều khoản và điều kiện được liệt kê dưới đây."));
        items.add(new TermItem("2. Quyền và nghĩa vụ của người dùng",
                "Người dùng có trách nhiệm cung cấp thông tin chính xác và không sử dụng ứng dụng cho các mục đích bất hợp pháp."));
        items.add(new TermItem("3. Chính sách bảo mật",
                "Chúng tôi cam kết bảo vệ thông tin cá nhân của bạn theo chính sách bảo mật của ứng dụng."));
        items.add(new TermItem("4. Thay đổi điều khoản",
                "GoGo có quyền thay đổi các điều khoản này bất kỳ lúc nào và sẽ thông báo cho người dùng qua email hoặc thông báo trong ứng dụng."));
        items.add(new TermItem("5. Liên hệ hỗ trợ",
                "Nếu bạn có thắc mắc về điều khoản, vui lòng liên hệ với chúng tôi qua email: support@gogo.com."));

        // Thiết lập adapter
        adapter = new TermAdapter(items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(TermsActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(TermsActivity.this, ViewProfileActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(TermsActivity.this, SettingActivity.class));
                finish();
                return true;
            }

            return false;
        });

    }
}