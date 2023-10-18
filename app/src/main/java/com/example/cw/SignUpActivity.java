package com.example.cw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
    }

    public void signUpClick(View view) {
        String username = usernameField.getText().toString();
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        // Sign up the user
        if (AuthenticationManager.isValidSignUp(username, email, password)) {
            // Successful sign-up
            Toast.makeText(this, "Sign-up successful!", Toast.LENGTH_SHORT).show();
            // Proceed to the login screen or any other relevant action
            // Create an Intent to launch the Login activity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            // Failed sign-up
            Toast.makeText(this, "Sign-up failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}