package com.example.gogo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gogo.R;
import com.example.gogo.database.AccountDAO;
import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class SleepActivity extends AppCompatActivity {
    private AccountDAO accountDAO;
    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_main);

        // Khởi tạo AccountDAO
        accountDAO = new AccountDAO(new DatabaseHelper(this));

        // Lấy userId từ Google Sign-In
        loadUserData();

        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            // Chuyển hướng về trang đăng nhập nếu cần
            Intent intent = new Intent(this, MainActivity.class); // Hoặc trang đăng nhập của bạn
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        Button btnGetStarted = findViewById(R.id.getStartedButton);
        Button btnSleepInput = findViewById(R.id.btnSleepInput);

        btnGetStarted.setOnClickListener(v -> {
            Intent intent = new Intent(this, WakeUpInputActivity.class);
            intent.putExtra("USER_ID", userId); // Truyền userId
            startActivity(intent);
        });

        btnSleepInput.setOnClickListener(v -> {
            Intent intent = new Intent(this, SleepInputActivity.class);
            intent.putExtra("USER_ID", userId); // Truyền userId
            startActivity(intent);
        });
    }

    private void loadUserData() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            User user = accountDAO.getUserByGoogleId(account.getId());
            if (user != null) {
                userId = user.getUserId();
            } else {
                userId = accountDAO.getUserIdByGoogleId(account.getId());
            }
        }
    }
}