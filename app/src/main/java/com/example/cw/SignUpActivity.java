package com.example.cw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

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

public class SignUpActivity extends AppCompatActivity {
    private EditText usernameField;
    private EditText emailField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        usernameField = findViewById(R.id.usernameField);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);

        // Navigate to loginActivity when "Already have an account? Sign in" is clicked
        TextView alreadyHaveAcc = findViewById(R.id.alreadyHaveAcc);
        alreadyHaveAcc.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    public void signUpClick(View view) {
        String username = usernameField.getText().toString();
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();


        new Thread(() -> {
            Call<ResponseBody> call = RetrofitClient.getInstance().getApi().signUp(username, email, password);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) { // if the sign up is success, sign up the user and navigate to login page
                        try {
                            String s = response.body().string();
                            Toast.makeText(SignUpActivity.this, s, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class); // If signup is successful, navigate to LoginActivity
                            startActivity(intent);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    } else {
                        Log.e("SignUpActivity", "Error: " + response.code());
                        // Handle unsuccessful response
                        Toast.makeText(SignUpActivity.this, "Sign up failed", Toast.LENGTH_SHORT).show();

                    }
                }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(SignUpActivity.this, "Sign up failed. Please try again.", Toast.LENGTH_SHORT).show();
                        Log.e("SignUpActivity", "Error: " + t.getMessage());
                    }

                });
        }).start();
    }
}