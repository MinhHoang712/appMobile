package com.example.applicationp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        String refreshToken = sharedPreferences.getString("REFRESH_TOKEN", "");

        if (!refreshToken.isEmpty()) {
            validateRefreshToken(refreshToken);
        } else {
            setupLoginInterface();
        }
    }

    private void validateRefreshToken(String refreshToken) {
        String url = getString(R.string.api_base_url) + "api/token/refresh/";
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try {
            postData.put("refresh", refreshToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                postData,
                response -> {
                    try {
                        String newAccessToken = response.getString("access");
                        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("ACCESS_TOKEN", newAccessToken);
                        editor.apply();
                        navigateToLibrary();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        setupLoginInterface();
                    }
                },
                error -> setupLoginInterface()
        );

        queue.add(jsonObjectRequest);
    }

    private void setupLoginInterface() {
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        buttonLogin.setOnClickListener(v -> performLogin());
        buttonSignUp.setOnClickListener(v -> navigateToRegister());
    }

    private void performLogin() {
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        if (!username.isEmpty() && !password.isEmpty()) {
            sendLoginRequest(username, password);
        } else {
            Toast.makeText(LoginActivity.this, "Username và Password không được để trống", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendLoginRequest(String username, String password) {
        String url = getString(R.string.api_base_url) + "api/login/";
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try {
            postData.put("username", username);
            postData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                postData,
                response -> handleLoginSuccess(response),
                error -> handleLoginError(error)
        );

        queue.add(jsonObjectRequest);
    }

    private void handleLoginSuccess(JSONObject response) {
        try {
            String username = response.getString("username");
            String refreshToken = response.getString("refresh");
            String accessToken = response.getString("access");
            String userRole = response.getString("user_role");
            saveTokens(username, refreshToken, accessToken, userRole);
            navigateToLibrary();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(LoginActivity.this, "Lỗi khi phân tích phản hồi đăng nhập", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleLoginError(VolleyError error) {
        String errorMessage = "Lỗi không xác định";
        if (error.networkResponse != null && error.networkResponse.data != null) {
            try {
                String responseBody = new String(error.networkResponse.data, "utf-8");
                JSONObject data = new JSONObject(responseBody);
                errorMessage = data.optString("error", "Lỗi không xác định");
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(LoginActivity.this, "Lỗi đăng nhập: " + errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void saveTokens(String username, String refreshToken, String accessToken, String userRole) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USERNAME", username);
        editor.putString("REFRESH_TOKEN", refreshToken);
        editor.putString("ACCESS_TOKEN", accessToken);
        editor.putString("USER_ROLE", userRole);
        editor.apply();
    }

    private void navigateToLibrary() {
        Intent intent = new Intent(this, LibraryActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToRegister() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }
}
