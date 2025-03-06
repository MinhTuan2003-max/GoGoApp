package com.example.gogo.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gogo.R;
import com.example.gogo.database.DatabaseHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "GoogleSignIn";
    private GoogleSignInClient mGoogleSignInClient;
    private DatabaseHelper databaseHelper;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        handleSignInResult(task);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Cấu hình Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        MaterialButton googleSignInButton = findViewById(R.id.googleSignInButton);
        googleSignInButton.setOnClickListener(v -> signIn());

        // Kiểm tra đăng nhập trước đó
        checkPreviousSignIn();
    }

    private void checkPreviousSignIn() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            navigateToHomeActivity();
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        signInLauncher.launch(signInIntent);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                // Lưu thông tin người dùng vào database
                saveUserToDatabase(account);

                // Chuyển sang HomeActivity
                navigateToHomeActivity();
            }
        } catch (ApiException e) {
            Log.w(TAG, "Sign-in failed: " + e.getStatusCode());
            Toast.makeText(MainActivity.this, R.string.sign_in_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserToDatabase(GoogleSignInAccount account) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("GoogleId", account.getId());
            values.put("FullName", account.getDisplayName());
            values.put("Email", account.getEmail());
            values.put("ProfileImageUrl", account.getPhotoUrl() != null
                    ? account.getPhotoUrl().toString()
                    : null);
            values.put("CreatedAt", new Date().getTime());

            long result = db.insertWithOnConflict("Users", null, values, SQLiteDatabase.CONFLICT_REPLACE);

            if (result == -1) {
                Toast.makeText(MainActivity.this, "Lỗi lưu thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi lưu người dùng: " + e.getMessage());
            Toast.makeText(MainActivity.this, "Lỗi lưu thông tin người dùng", Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
    }

    private void navigateToHomeActivity() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}