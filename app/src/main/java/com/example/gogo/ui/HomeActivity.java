package com.example.gogo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gogo.R;
import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.bumptech.glide.Glide;

public class HomeActivity extends AppCompatActivity {
    private ImageView avatarImageView;
    private TextView welcomeTextView;
    private TextView ageTextView;
    private TextView genderTextView;
    private TextView heightTextView;
    private TextView weightTextView;
    private Button continueButton;
    private Button logoutButton;
    private DatabaseHelper databaseHelper;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        avatarImageView = findViewById(R.id.avatarImageView);
        welcomeTextView = findViewById(R.id.welcomeTextView);
        ageTextView = findViewById(R.id.ageTextView);
        genderTextView = findViewById(R.id.genderTextView);
        heightTextView = findViewById(R.id.heightTextView);
        weightTextView = findViewById(R.id.weightTextView);
        continueButton = findViewById(R.id.continueButton);
        logoutButton = findViewById(R.id.logoutButton);
        databaseHelper = new DatabaseHelper(this);

        googleSignInClient = GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build());

        loadUserData();

        continueButton.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        });

        logoutButton.setOnClickListener(view -> logout());
    }

    private void loadUserData() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            User user = databaseHelper.getUserByGoogleId(account.getId());
            if (user != null) {
                welcomeTextView.setText("Welcome, " + user.getFullName());
                ageTextView.setText("Age: " + (user.getAge() > 0 ? user.getAge() : "N/A"));
                genderTextView.setText("Gender: " + (user.getGender() != null ? user.getGender() : "N/A"));
                heightTextView.setText("Height: " + (user.getHeight() > 0 ? String.format("%.1f cm", user.getHeight()) : "N/A"));
                weightTextView.setText("Weight: " + (user.getWeight() > 0 ? String.format("%.1f kg", user.getWeight()) : "N/A"));

                if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                    Glide.with(this)
                            .load(user.getProfileImageUrl())
                            .placeholder(R.drawable.default_avatar)
                            .error(R.drawable.default_avatar)
                            .into(avatarImageView);
                }
            } else {
                welcomeTextView.setText("Welcome, " + account.getDisplayName());
                if (account.getPhotoUrl() != null) {
                    Glide.with(this)
                            .load(account.getPhotoUrl())
                            .placeholder(R.drawable.default_avatar)
                            .error(R.drawable.default_avatar)
                            .into(avatarImageView);
                }
                ageTextView.setText("Age: N/A");
                genderTextView.setText("Gender: N/A");
                heightTextView.setText("Height: N/A");
                weightTextView.setText("Weight: N/A");
            }
        } else {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void logout() {
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}