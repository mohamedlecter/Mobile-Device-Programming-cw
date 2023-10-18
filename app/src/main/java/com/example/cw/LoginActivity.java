package com.example.cw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
    }

    public void loginClick(View view) {
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();

        // Authenticate the user
        if (AuthenticationManager.isValidLogin(username, password)) {
            // Successful login
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            // Proceed to the next screen or activity
            // Create an Intent to launch the Home activity
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else {
            // Failed login
            Toast.makeText(this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
