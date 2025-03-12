package com.example.gogo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gogo.R;
import com.example.gogo.database.AccountDAO;
import com.example.gogo.database.DatabaseHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "GoogleSignIn";
    private GoogleSignInClient mGoogleSignInClient;
    private DatabaseHelper databaseHelper;
    private AccountDAO accountDAO;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d(TAG, "Sign-in result received: code=" + result.getResultCode() + ", data=" + result.getData());
                Intent data = result.getData();

                if (result.getResultCode() == RESULT_OK && data != null) {
                    Log.d(TAG, "Sign-in successful, processing result");
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    handleSignInResult(task);
                } else {
                    Log.e(TAG, "Sign-in was canceled or failed: code=" + result.getResultCode() + ", data=" + (data != null ? data.toString() : "null"));
                    if (data != null) {
                        // Kiểm tra extras trong Intent
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            Log.d(TAG, "Extras in Intent: " + extras.keySet());
                            for (String key : extras.keySet()) {
                                Log.d(TAG, "Extra [" + key + "]: " + extras.get(key));
                            }
                            // Thử xử lý Intent ngay cả khi không phải RESULT_OK
                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                            handleSignInResult(task);
                        } else {
                            Log.d(TAG, "No extras found in Intent");
                            Toast.makeText(MainActivity.this, "Đăng nhập bị hủy", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d(TAG, "Result data is null");
                        Toast.makeText(MainActivity.this, "Đăng nhập thất bại: Không có dữ liệu trả về", Toast.LENGTH_SHORT).show();
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
        accountDAO = new AccountDAO(databaseHelper);

        // Kiểm tra Google Play Services
        if (!checkGooglePlayServices()) {
            finish();
            return;
        }

        // Cấu hình Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Gán sự kiện cho nút đăng nhập
        MaterialButton googleSignInButton = findViewById(R.id.googleSignInButton);
        googleSignInButton.setOnClickListener(v -> signIn());

        // Kiểm tra đăng nhập trước đó
        checkPreviousSignIn();
    }

    private boolean checkGooglePlayServices() {
        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
        int resultCode = googleApi.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            Log.e(TAG, "Google Play Services unavailable: " + resultCode);
            if (googleApi.isUserResolvableError(resultCode)) {
                googleApi.getErrorDialog(this, resultCode, 9000).show();
            } else {
                Toast.makeText(this, "Thiết bị không hỗ trợ Google Play Services", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        Log.d(TAG, "Google Play Services is available");
        return true;
    }

    private void checkPreviousSignIn() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            Log.d(TAG, "Previous sign-in detected: " + account.getEmail());
            navigateToHomeActivity();
        } else {
            Log.d(TAG, "No previous sign-in detected");
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        Log.d(TAG, "Launching sign-in intent: " + signInIntent);
        signInLauncher.launch(signInIntent);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d(TAG, "Sign-in successful, account: " + (account != null ? account.getEmail() : "null"));

            if (account != null) {
                saveUserToDatabase(account);
                navigateToHomeActivity();
            } else {
                Log.e(TAG, "Account is null after successful sign-in");
                Toast.makeText(MainActivity.this, "Đăng nhập không thành công", Toast.LENGTH_SHORT).show();
            }
        } catch (ApiException e) {
            Log.w(TAG, "Sign-in failed: code=" + e.getStatusCode() + ", message=" + e.getMessage());
            String errorMessage;
            switch (e.getStatusCode()) {
                case 12501:
                    errorMessage = "Đăng nhập bị hủy bởi người dùng";
                    break;
                case 12500:
                    errorMessage = "Lỗi đăng nhập không xác định";
                    break;
                case 10:
                    errorMessage = "Lỗi cấu hình nhà phát triển (kiểm tra Google Cloud Console)";
                    break;
                default:
                    errorMessage = "Đăng nhập thất bại: " + e.getStatusMessage();
            }
            Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error during sign-in: ", e);
            Toast.makeText(MainActivity.this, "Lỗi không xác định khi đăng nhập", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserToDatabase(GoogleSignInAccount account) {
        Log.d(TAG, "Attempting to save user: " + account.getId());

        // Kiểm tra xem user đã tồn tại chưa
        if (accountDAO.isUserExists(account.getId())) {
            Log.d(TAG, "User already exists with GoogleID: " + account.getId());
            Toast.makeText(MainActivity.this, "Chào mừng trở lại", Toast.LENGTH_SHORT).show();
            // Chuyển sang HomeActivity hoặc load dữ liệu hiện có
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Nếu chưa tồn tại, insert mới
            boolean success = accountDAO.insertUser(
                    account.getId(),
                    account.getDisplayName(),
                    account.getEmail(),
                    account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : null
            );

            Log.d(TAG, "User save result: " + (success ? "success" : "failure"));

            if (success) {
                Toast.makeText(MainActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(MainActivity.this, "Lỗi lưu thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateToHomeActivity() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        Intent intent = new Intent(MainActivity.this, CreateProfileActivity.class);
        if (account != null) {
            intent.putExtra("GOOGLE_ID", account.getId()); // Pass Google ID explicitly
        }
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}