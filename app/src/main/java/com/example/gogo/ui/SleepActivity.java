package com.example.gogo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
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
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_main);

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        accountDAO = new AccountDAO(new DatabaseHelper(this));

        loadUserData();

        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        Button btnGetStarted = findViewById(R.id.getStartedButton);
        Button btnSleepInput = findViewById(R.id.btnSleepInput);

        btnGetStarted.setOnClickListener(v -> {
            Intent intent = new Intent(this, WakeUpInputActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });

        btnSleepInput.setOnClickListener(v -> {
            Intent intent = new Intent(this, SleepHistoryActivity.class);
            intent.putExtra("USER_ID", userId);
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