package com.example.gogo.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.adapters.ViewProfileAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.appbar.MaterialToolbar;

public class ViewProfileActivity extends AppCompatActivity {

    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_user);

        // Initialize GoogleSignInClient
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestId()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(ViewProfileActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            String googleId = account.getId();
            setupRecyclerView(googleId);
        } else {
            finish();
        }
    }

    private void setupRecyclerView(String googleId) {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewProfile);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ViewProfileAdapter(this, googleId));
    }

    public void logout() {
        if (googleSignInClient != null) {
            googleSignInClient.signOut().addOnCompleteListener(this, task -> {
                Intent intent = new Intent(ViewProfileActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }
}