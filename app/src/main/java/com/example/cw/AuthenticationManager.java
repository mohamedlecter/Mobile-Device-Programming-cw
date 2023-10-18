package com.example.cw;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationManager {
    // Simulated user database
    private static final Map<String, String> userDatabase = new HashMap<>();

    public static boolean isValidLogin(String username, String password) {
        // Simulated logic: Check if the username and password match a stored user
        String storedPassword = userDatabase.get(username);
        return storedPassword != null && storedPassword.equals(password);
    }

    public static boolean isValidSignUp(String username, String email, String password) {
        // Simulated logic: Check if the username is not already taken
        if (userDatabase.containsKey(username)) {
            return false; // Username is already in use
        }

        // Simulated logic: Store the new user's data
        userDatabase.put(username, password);

        return true;
    }
}
