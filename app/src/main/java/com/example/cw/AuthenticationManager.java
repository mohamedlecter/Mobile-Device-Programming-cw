package com.example.cw;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationManager {
    // Simulated user database
    private static final Map<String, String> userDatabase = new HashMap<>();

    public static boolean isValidLogin(String email, String password) {
        // Simulated logic: Check if the email and password match a stored user
        String storedPassword = userDatabase.get(email);
        return storedPassword != null && storedPassword.equals(password);
    }

    public static boolean isValidSignUp(String email, String username, String password) {
        // Simulated logic: Check if the username is not already taken
        if (userDatabase.containsKey(email)) {
            return false; // email is already in use
        }

        // Simulated logic: Store the new user's data
        userDatabase.put(email, password);

        return true;
    }
}
