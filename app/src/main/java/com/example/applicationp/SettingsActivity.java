package com.example.applicationp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button btnChangePassword = findViewById(R.id.btnChangePassword);
        Button btnLogout = findViewById(R.id.btnLogout);
        Button btnAdminPage = findViewById(R.id.btnAdminPage);
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);

        String userRole = sharedPreferences.getString("USER_ROLE", "");
        if ("admin".equals(userRole)) {
            btnAdminPage.setVisibility(View.VISIBLE);
        }

        btnAdminPage.setOnClickListener(view -> {
            String baseUrl = getString(R.string.api_base_url);
            String adminUrl = baseUrl + "admin/";


            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(adminUrl));
            startActivity(browserIntent);
        });

        btnChangePassword.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Đổi Mật Khẩu");

            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_change_password, null);
            builder.setView(dialogView);

            TextInputLayout textInputCurrentPassword = dialogView.findViewById(R.id.textInputCurrentPassword);
            TextInputLayout textInputNewPassword = dialogView.findViewById(R.id.textInputNewPassword);
            TextInputLayout textInputConfirmNewPassword = dialogView.findViewById(R.id.textInputConfirmNewPassword);


            builder.setPositiveButton("Đổi Mật Khẩu", (dialogInterface, i) -> {
                String currentPassword = textInputCurrentPassword.getEditText().getText().toString();
                String newPassword = textInputNewPassword.getEditText().getText().toString();
                String confirmNewPassword = textInputConfirmNewPassword.getEditText().getText().toString();

                if (!newPassword.equals(confirmNewPassword)) {
                    Toast.makeText(SettingsActivity.this, "Mật khẩu mới và xác nhận mật khẩu không khớp.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String username = sharedPreferences.getString("USERNAME", "");

                JSONObject changePasswordParams = new JSONObject();
                try {
                    changePasswordParams.put("username", username);
                    changePasswordParams.put("old_password", currentPassword);
                    changePasswordParams.put("new_password", newPassword);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String changePasswordUrl =  getString(R.string.api_base_url) +"api/change_password/";

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST, changePasswordUrl, changePasswordParams,
                        response -> {
                            Toast.makeText(SettingsActivity.this, "Mật khẩu đã được thay đổi thành công.", Toast.LENGTH_SHORT).show();
                        },
                        error -> {
                            Toast.makeText(SettingsActivity.this, "Đã xảy ra lỗi khi đổi mật khẩu.", Toast.LENGTH_SHORT).show();
                        }
                );

                RequestQueue requestQueue = Volley.newRequestQueue(SettingsActivity.this);
                requestQueue.add(jsonObjectRequest);
            });

            builder.setNegativeButton("Hủy", (dialogInterface, i) -> dialogInterface.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        });


        btnLogout.setOnClickListener(view -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intentToLogin = new Intent(this, LoginActivity.class);
            intentToLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentToLogin);
            finish();
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_settings);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_library) {
                Intent intentToLibrary = new Intent(SettingsActivity.this, LibraryActivity.class);
                intentToLibrary.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intentToLibrary);
                return true;
            } else if (item.getItemId() == R.id.navigation_settings) {

                return true;
            }
            return false;
        });
    }
}