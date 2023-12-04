package com.example.cw.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.cw.CampusLinks;
import com.example.cw.R;
import com.example.cw.SessionManager;
import com.example.cw.api.Api;
import com.example.cw.api.RetrofitClient;
import com.example.cw.auth.LoginActivity;
import com.example.cw.events.HomeActivity;
import com.example.cw.jobs.JobsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class profile extends AppCompatActivity {
    private Api api;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initView();
        BottomNavigation();


    }


    private void initView() {
//        api = RetrofitClient.getInstance().getApi();
//        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
//        recyclerView = findViewById(R.id.linksRecyclerView);
//      recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        swipeRefreshLayout.setOnRefreshListener(() -> {
//            // You may implement the refresh logic for the profile here
//        });
        sessionManager = new SessionManager(this);
        // Get the reference to the sign-out button
        Button btnSignOut = findViewById(R.id.btnSignOut);

        // Set an onClickListener for the sign-out button
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call the signOut method when the button is clicked
                signOut();
            }
        });
    }

    private void BottomNavigation() {

        boolean isAdmin = sessionManager.isAdmin();

        // Set the visibility of the add button based on the user's role
        FloatingActionButton addButton = findViewById(R.id.buttonAdd);
        addButton.setVisibility(isAdmin ? View.VISIBLE : View.GONE);


        LinearLayout homeBtn = findViewById(R.id.home_Btn);
        LinearLayout jobsBtn = findViewById(R.id.jobs_Btn);
        LinearLayout linksBtn = findViewById(R.id.links_Btn);
        LinearLayout profileBtn = findViewById(R.id.Profile_Btn);


        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(profile.this, HomeActivity.class));
            }
        });

        jobsBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(profile.this, JobsActivity.class));
            }
        }));
        linksBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(profile.this, CampusLinks.class));
            }
        }));
        profileBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(profile.this, profile.class));
            }
        }));
    }
    // Method to handle sign-out
    public void signOut() {
        // Clear the session using the SessionManager
        SessionManager sessionManager = new SessionManager(getApplicationContext());

        String userIdBeforeLogout = sessionManager.getUserId();

        // Log the user ID before logging out
        Log.d("UserProfile", "User ID before logout: " + userIdBeforeLogout);

        sessionManager.clearSession();


        // Get the user ID after logging out
        String userIdAfterLogout = sessionManager.getUserId();

        // Log the user ID after logging out
        Log.d("UserProfile", "User ID after logout: " + userIdAfterLogout);


        // Redirect to the login
        Intent intent = new Intent(profile.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close the current activity
    }
}