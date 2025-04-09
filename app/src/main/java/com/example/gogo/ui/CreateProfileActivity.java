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

        dbHelper = new DatabaseHelper(this);
        accountDAO = new AccountDAO(dbHelper);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            googleId = account.getId();
            User user = accountDAO.getUserByGoogleId(googleId);
            if (user != null && isAdditionalInfoFilled(user)) {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("USER_ID", user.getUserId());
                intent.putExtra("GOOGLE_ID", googleId);
                intent.putExtra("IS_ADMIN", user.isAdmin());
                startActivity(intent);
                finish();
                return;
            }
            user = saveUserToDatabase(account);
            if (user != null) {
                setupRecyclerView(user);
            }
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
                User user = saveUserToDatabase(account);
                if (user != null) {
                    setupRecyclerView(user);
                }
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
            user = new User(
                    0,
                    account.getId(),
                    account.getDisplayName(),
                    account.getEmail(),
                    account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "default_url",
                    0,
                    null,
                    0.0f,
                    0.0f,
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                    "minhtuanha2829@gmail.com".equalsIgnoreCase(account.getEmail())
            );
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
            user = accountDAO.getUserByGoogleId(googleId);
        } else {
            Log.d(TAG, "User already exists with GoogleID: " + googleId);
            Toast.makeText(this, "Chào mừng trở lại", Toast.LENGTH_SHORT).show();
        }
        return user;
    }

    private void setupRecyclerView(User user) {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UserInfoAdapter(this, user);
        recyclerView.setAdapter(adapter);
    }

    private boolean isAdditionalInfoFilled(User user) {
        return user.getAge() > 0 && user.getGender() != null && !user.getGender().trim().isEmpty() &&
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