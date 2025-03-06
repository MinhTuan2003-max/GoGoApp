package com.example.gogo.ui;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.gogo.R;
import com.example.gogo.database.DatabaseHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class HomeActivity extends AppCompatActivity {

    private ImageView avatarImageView;
    private TextView welcomeTextView;
    private Button logoutButton;
    private DatabaseHelper databaseHelper;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        avatarImageView = findViewById(R.id.avatarImageView);
        welcomeTextView = findViewById(R.id.welcomeTextView);
        logoutButton = findViewById(R.id.logoutButton);
        databaseHelper = new DatabaseHelper(this);

        // Cấu hình Google Sign-In Client
        googleSignInClient = GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build());

        loadUserData();

        // Bắt sự kiện khi nhấn nút Logout
        logoutButton.setOnClickListener(view -> logout());
    }

    private void loadUserData() {
        // Lấy dữ liệu người dùng từ SQLite
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT FullName, ProfileImageUrl FROM Users ORDER BY CreatedAt DESC LIMIT 1", null);

        if (cursor.moveToFirst()) {
            String fullName = cursor.getString(0);
            String profileImageUrl = cursor.getString(1);

            welcomeTextView.setText("Welcome, " + fullName);
            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                Glide.with(this).load(profileImageUrl).into(avatarImageView);
            }
        }

        cursor.close();
        db.close();
    }

    private void logout() {
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            // Chuyển về màn hình đăng nhập
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
