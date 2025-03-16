package com.example.gogo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gogo.R;
import com.example.gogo.adapters.SettingOptionAdapter;
import com.example.gogo.database.AccountDAO;
import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.models.SettingOption;
import com.example.gogo.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    private RecyclerView optionsRecyclerView;
    private SettingOptionAdapter adapter;
    private Button signOutButton;
    private GoogleSignInClient googleSignInClient;
    private ImageView profileImage;
    private TextView nameTextView;
    private AccountDAO accountDAO;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Ánh xạ các thành phần giao diện
        optionsRecyclerView = findViewById(R.id.optionsRecyclerView);
        signOutButton = findViewById(R.id.buttonLogout);
        profileImage = findViewById(R.id.profileImage);
        nameTextView = findViewById(R.id.nameTextView); // Sửa lỗi typo findViewId thành findViewById

        // Khởi tạo DatabaseHelper
        databaseHelper = new DatabaseHelper(this);
        // Khởi tạo AccountDAO với DatabaseHelper
        accountDAO = new AccountDAO(databaseHelper);

        // Khởi tạo GoogleSignInClient
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestId()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Lấy thông tin người dùng từ database
        loadUserData();

        // Thiết lập RecyclerView
        setupRecyclerView();

        // Thiết lập sự kiện cho nút Đăng xuất
        signOutButton.setOnClickListener(v -> {
            Toast.makeText(this, "Signing out...", Toast.LENGTH_SHORT).show();
            logout();
            finish();
        });

        // Thiết lập BottomNavigationView
        setupBottomNavigation();
    }

    private void loadUserData() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            String googleId = account.getId();
            if (googleId != null) {
                // Lấy thông tin từ database
                User user = accountDAO.getUserByGoogleId(googleId);
                if (user != null) {
                    // Hiển thị tên người dùng
                    String fullName = user.getFullName();
                    if (fullName != null && !fullName.isEmpty()) {
                        nameTextView.setText(fullName);
                    } else {
                        nameTextView.setText("User Name");
                    }

                    // Hiển thị avatar
                    String profileImageUrl = user.getProfileImageUrl();
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Glide.with(this)
                                .load(profileImageUrl)
                                .placeholder(R.drawable.default_avatar)
                                .error(R.drawable.default_avatar)
                                .into(profileImage);
                    } else {
                        profileImage.setImageResource(R.drawable.default_avatar);
                    }
                } else {
                    // Nếu không tìm thấy trong database, sử dụng thông tin mặc định
                    nameTextView.setText("User Name");
                    profileImage.setImageResource(R.drawable.default_avatar);
                    Toast.makeText(this, "User data not found in database", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            // Nếu không có tài khoản Google, quay lại MainActivity
            Intent intent = new Intent(SettingActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void setupRecyclerView() {
        List<SettingOption> options = new ArrayList<>();
        options.add(new SettingOption(1, "So sánh cân nặng", R.drawable.ic_compare));
        options.add(new SettingOption(2, "Thông báo", R.drawable.ic_notification));
        options.add(new SettingOption(3, "Điều khoản và chính sách", R.drawable.ic_terms));
        options.add(new SettingOption(4, "Về chúng tôi", R.drawable.ic_about_us));

        // Setup adapter
        adapter = new SettingOptionAdapter(options, option -> {
            switch (option.getId()) {
                case 1: // Compare
                    Intent compareIntent = new Intent(SettingActivity.this, WeightLossActivity.class);
                    startActivity(compareIntent);
                    break;
                case 2: // Notifications
                    // Navigate to Notifications feature
                    break;
                case 3: // Terms
                    Intent termsIntent = new Intent(SettingActivity.this, TermsActivity.class);
                    startActivity(termsIntent);
                    break;
                case 4: // About Us
                    Intent intent = new Intent(SettingActivity.this, AboutUsActivity.class);
                    startActivity(intent);
                    break;
            }
        });

        // Setup recycler view
        optionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        optionsRecyclerView.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(SettingActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(SettingActivity.this, ViewProfileActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_settings) {
                // Đã ở SettingActivity, không làm gì
                return true;
            }
            return false;
        });

        // Đánh dấu mục hiện tại
        bottomNavigationView.setSelectedItemId(R.id.nav_settings);
    }

    public void logout() {
        if (googleSignInClient != null) {
            googleSignInClient.signOut().addOnCompleteListener(this, task -> {
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Chỉ gọi close() trên databaseHelper vì AccountDAO không có phương thức close()
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}