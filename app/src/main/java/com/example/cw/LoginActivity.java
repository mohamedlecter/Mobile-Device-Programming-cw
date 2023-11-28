package com.example.cw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LoginActivity extends AppCompatActivity {
    private EditText emailField;
    private EditText passwordField;
    private SessionManager sessionManager;
    private Button signInButton;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);
        // Initialize BottomNavigationView
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        signInButton = findViewById(R.id.signInButton);

        // TextWatchers to perform real-time validation
        emailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateEmailFormat(charSequence.toString());
                updateSignInButtonState();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not used
            }
        });

        passwordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validatePassword(charSequence.toString());
                updateSignInButtonState();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not used
            }
        });

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

        if (email.isEmpty() || password.isEmpty()) {
            if (email.isEmpty()) {
                emailField.setError("Email cannot be empty");
            }

            if (password.isEmpty()) {
                passwordField.setError("Password cannot be empty");
            }

            return;
        }
        // Check if the email is in the correct format
        if (!isValidEmail(email)) {
            Toast.makeText(LoginActivity.this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Authenticate the user
        new Thread(() -> {
            Call<ResponseBody> call = RetrofitClient.getInstance().getApi().login(email, password);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) { // if the sign up is success, sign up the user and navigate to login page
                        try {
                            JSONObject jsonResponse = new JSONObject(response.body().string());
                            JSONObject userData = jsonResponse.getJSONObject("data").getJSONObject("user");

                            boolean isAdmin = userData.getBoolean("isAdmin");
                            String userId = userData.getString("_id");

                            Log.d("LoginActivity", "User ID: " + userId);
                            Log.d("LoginActivity", "is admin: " + isAdmin);
                            // Save isAdmin and userId to SessionManager
                            sessionManager.saveIsAdmin(isAdmin);
                            sessionManager.saveUserId(userId);

                            // Log the saved values for verification
                            Log.d("LoginActivity", "isAdmin saved: " + isAdmin);
                            Log.d("LoginActivity", "userId saved: " + userId);

                            String s = response.body().string();
                            Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();

                            // Check if the user is an admin
                            if (isAdmin) {
                                // Navigate to AdminHomeActivity
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                            } else {
                                // Navigate to HomeActivity for regular users
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }

//                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class); // If login is successful, navigate to Home
//                            startActivity(intent);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Log.e("LoginActivity", "Error: " + response.code());
                        Toast.makeText(LoginActivity.this, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
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

    private void validateEmailFormat(String email) {
        boolean isValid = isValidEmail(email);

        if (!isValid) {
            emailField.setError("Please enter a valid email address");
        } else {
            emailField.setError(null);
        }
    }

    private void validatePassword(String password) {
        if (password.isEmpty()) {
            passwordField.setError("Password cannot be empty");
        } else {
            passwordField.setError(null);
        }
    }
    private boolean isValidEmail(String email) {
        // Define a simple regex pattern for email validation
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        // Use the Pattern and Matcher classes for validation
        return email.matches(emailPattern);
    }

    private void updateSignInButtonState() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        boolean isEmailValid = isValidEmail(email);
        boolean isPasswordValid = !password.isEmpty();

        signInButton.setEnabled(isEmailValid && isPasswordValid);
    }

}
