package com.example.gogo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.adapters.ViewProfileAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ViewProfileActivity extends AppCompatActivity {

    private GoogleSignInClient googleSignInClient;
    private ViewProfileAdapter adapter;
    private RecyclerView recyclerView;
    private String googleId;
    private BottomNavigationView bottomNavigationView;
    public static final int REQUEST_UPDATE_PROFILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_user);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestId()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        recyclerView = findViewById(R.id.recyclerViewProfile);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            googleId = account.getId();
            setupRecyclerView();
        } else {
            finish();
        }

        setupBottomNavigation();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ViewProfileAdapter(this, googleId);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ViewProfile", "onActivityResult called with requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (requestCode == REQUEST_UPDATE_PROFILE && resultCode == RESULT_OK) {
            Log.d("ViewProfile", "Update successful, refreshing data");
            if (adapter != null) {
                adapter.refreshData();
            } else {
                Log.w("ViewProfile", "Adapter is null, reinitializing");
                setupRecyclerView();
            }
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(ViewProfileActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(ViewProfileActivity.this, SettingActivity.class));
                finish();
                return true;
            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
    }
}