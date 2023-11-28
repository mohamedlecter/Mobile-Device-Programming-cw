package com.example.cw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText emailField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);

        // Navigate to signupActivity when "Don’t have an account?  Sign up" is clicked
        TextView dontHaveAcc = findViewById(R.id.dontHaveAcc);
        dontHaveAcc.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        // Navigate to Admin Login when "Sign in as admin" is clicked
        TextView signInAsAdmin = findViewById(R.id.signInAsAdmin);
        signInAsAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, AdminLoginActivity.class);
            startActivity(intent);
        });
    }

    public void loginClick(View view) {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        // Authenticate the user
        new Thread(() -> {
            Call<ResponseBody> call = RetrofitClient.getInstance().getApi().login(email, password);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) { // if the sign up is success, sign up the user and navigate to login page
                        try {
                            // Parse the response to get user data, including isAdmin and userId
                            JSONObject jsonResponse = new JSONObject(response.body().string());
                            JSONObject userData = jsonResponse.getJSONObject("data").getJSONObject("user");

                            boolean isAdmin = userData.getBoolean("isAdmin");
                            String userId = userData.getString("_id");

                            // Save isAdmin and userId to SharedPreferences
                            saveUserDetailsToSharedPreferences(isAdmin, userId);

                            // Log the saved values for verification
                            Log.d("LoginActivity", "isAdmin retrieved: " + isAdmin);
                            Log.d("LoginActivity", "userId retrieved: " + userId);

                            String s = response.body().string();
                            Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();


                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class); // If login is successful, navigate to Home
                            startActivity(intent);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Log.e("SignUpActivity", "Error: " + response.code());
                        // Handle unsuccessful response
                        Toast.makeText(LoginActivity.this, "Sign up failed", Toast.LENGTH_SHORT).show();

                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Sign up failed. Please try again.", Toast.LENGTH_SHORT).show();
                    Log.e("SignUpActivity", "Error: " + t.getMessage());
                }

            });
        }).start();
    }

    // Save isAdmin and userId to SharedPreferences
    private void saveUserDetailsToSharedPreferences(boolean isAdmin, String userId) {
        SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Save isAdmin
        editor.putBoolean("isAdmin", isAdmin);

        // Save userId
        editor.putString("userId", userId);

        // Apply the changes
        editor.apply();

        // Log the saved values for verification
        Log.d("SharedPreferences", "isAdmin saved: " + isAdmin);
        Log.d("SharedPreferences", "userId saved: " + userId);
    }

}
