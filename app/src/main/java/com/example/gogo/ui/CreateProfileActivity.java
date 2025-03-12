package com.example.gogo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogo.R;
import com.example.gogo.adapters.UserInfoAdapter;
import com.example.gogo.database.AccountDAO;
import com.example.gogo.database.DatabaseHelper;
import com.example.gogo.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateProfileActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "ProfileActivity";
    private GoogleSignInClient mGoogleSignInClient;
    private DatabaseHelper dbHelper;
    private AccountDAO accountDAO;
    private RecyclerView recyclerView;
    private UserInfoAdapter adapter;
    private String googleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        // Initialize DatabaseHelper and AccountDAO
        dbHelper = new DatabaseHelper(this);
        accountDAO = new AccountDAO(dbHelper); // Fix: Initialize accountDAO here

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            googleId = account.getId();
            User user = accountDAO.getUserByGoogleId(googleId);
            if (user != null && isAdditionalInfoFilled(user)) {
                // Additional info is complete, go to HomeActivity
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
                return;
            }
            // If info is incomplete or user is new, save and set up RecyclerView
            saveUserToDatabase(account);
            setupRecyclerView(account);
        } else {
            signIn();
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                saveUserToDatabase(account);
                setupRecyclerView(account);
            } catch (ApiException e) {
                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                Toast.makeText(this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private User saveUserToDatabase(GoogleSignInAccount account) {
        Log.d(TAG, "Attempting to save user: " + account.getId());
        googleId = account.getId();

        User user = accountDAO.getUserByGoogleId(googleId);
        if (user == null) {
            // If user doesn’t exist, insert new
            user = new User(0, account.getId(), account.getDisplayName(), account.getEmail(),
                    account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : null,
                    0, null, 0.0f, 0.0f, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            boolean success = accountDAO.insertUser(
                    user.getGoogleId(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getProfileImageUrl()
            );
            if (!success) {
                Toast.makeText(this, "Lỗi lưu thông tin người dùng", Toast.LENGTH_SHORT).show();
                return null;
            }
            Log.d(TAG, "User save result: success");
        } else {
            Log.d(TAG, "User already exists with GoogleID: " + googleId);
            Toast.makeText(this, "Chào mừng trở lại", Toast.LENGTH_SHORT).show();
        }
        return user;
    }

    private void setupRecyclerView(GoogleSignInAccount account) {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        User user = accountDAO.getUserByGoogleId(account.getId());
        if (user == null) {
            user = new User(0, account.getId(), account.getDisplayName(), account.getEmail(),
                    account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : null,
                    0, null, 0.0f, 0.0f, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        }

        adapter = new UserInfoAdapter(this, user);
        recyclerView.setAdapter(adapter);
    }

    private boolean isAdditionalInfoFilled(User user) {
        return user.getAge() > 0 && user.getGender() != null && user.getGender().trim().length() > 0 &&
                user.getHeight() > 0.0f && user.getWeight() > 0.0f;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}