package com.example.cw;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cw.adapter.LinksAdapter;
import com.example.cw.api.Api;
import com.example.cw.api.RetrofitClient;
import com.example.cw.events.HomeActivity;
import com.example.cw.jobs.JobDetailsActivity;
import com.example.cw.jobs.JobsActivity;
import com.example.cw.model.Link;
import com.example.cw.profile.profile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CampusLinks extends AppCompatActivity {

    private Api api;
    private List<Link> links;
    private SwipeRefreshLayout swipeRefreshLayout;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_links);
        initView();
        BottomNavigation();
    }

    private void initView() {
        api = RetrofitClient.getInstance().getApi();
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); // gets the swipeRefreshLayout
        sessionManager = new SessionManager(this);


        // Find the Nottingham Apps card view by ID
        CardView nottinghamAppsCard = findViewById(R.id.NottinghamApps);

        // Set an OnClickListener on the card view
        nottinghamAppsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check network availability before opening the URL
                if (isNetworkAvailable()) {
                    String url = "https://example.com";
                    openUrlInBrowser(url);
                } else {
                    // Handle the case when the network is not available
                    // You may want to show a message to the user
                        Toast.makeText(CampusLinks.this, "No internet connection", Toast.LENGTH_SHORT).show();
                        Log.d("CamusLink", "No internet connection");
                    // or display a Snackbar, etc.
                }
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
                startActivity(new Intent(CampusLinks.this, HomeActivity.class));
            }
        });

        jobsBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CampusLinks.this, JobsActivity.class));
            }
        }));
        linksBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CampusLinks.this, CampusLinks.class));
            }
        }));
        profileBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CampusLinks.this, profile.class));
            }
        }));
    }

    private void openUrlInBrowser(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    // Function to check if the device is connected to the internet
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}