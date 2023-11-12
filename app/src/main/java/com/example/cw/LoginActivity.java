package com.example.cw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
            Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
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
                            String s = response.body().string();
                            Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class); // If login is successful, navigate to Home
                            startActivity(intent);
                        } catch (IOException e) {
                            e.printStackTrace();
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
}
