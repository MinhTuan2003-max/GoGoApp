package com.example.gogo.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.gogo.R;
import com.example.gogo.database.DatabaseHelper;

public class HomeActivity extends AppCompatActivity {

    private ImageView avatarImageView;
    private TextView welcomeTextView;
    private DatabaseHelper databaseHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        avatarImageView = findViewById(R.id.avatarImageView);
        welcomeTextView = findViewById(R.id.welcomeTextView);
        databaseHelper = new DatabaseHelper(this);

        loadUserData();
    }

    private void loadUserData() {
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
}
