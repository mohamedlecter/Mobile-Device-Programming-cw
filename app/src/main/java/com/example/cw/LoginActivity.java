package com.example.cw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private EditText emailField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
    }

    public void loginClick(View view) {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        Log.d("LoginActivity", "Email: " + email);
        // Authenticate the user
        if (AuthenticationManager.isValidLogin(email, password)) {
            // Successful login
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            // Proceed to the next screen or activity
            // Create an Intent to launch the Home activity
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else {
            // Failed login
            Log.d("LoginActivity", "Login failed for email: " + email);
        }
    }
}
