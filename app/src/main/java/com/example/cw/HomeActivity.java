package com.example.cw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;


import com.example.cw.model.Event;
import com.google.android.material.navigation.NavigationView;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements EventAdapter.OnItemClickListener, NavigationView.OnNavigationItemSelectedListener {
    private Api api;
    private List<Event> events;
    private SwipeRefreshLayout swipeRefreshLayout; // to handle pull to refresh
    private RecyclerView recyclerView;
    private SessionManager sessionManager;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        api = RetrofitClient.getInstance().getApi(); // gets the api from the retrofit client
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); // gets the swipeRefreshLayout
        recyclerView = findViewById(R.id.eventsRecyclerView);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Set up the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Set up the SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(() -> getEvents());

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        // Call the method to get events
        getEvents();
    }

    public void onNavigationButtonClick(View view) {
        drawerLayout.openDrawer(GravityCompat.START);
    }
    private void getEvents() {
        Call<List<Event>> call;

        boolean isAdmin = sessionManager.isAdmin();
        if (isAdmin) {
            String userId = sessionManager.getUserId();
            Log.d("HomeActivity", "User ID: " + userId);
            call = api.getAdminEvents(userId); // You need to provide the userId
        } else {
            call = api.getEvents();
        }
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                swipeRefreshLayout.setRefreshing(false); // Stop the refresh animation
                if (response.isSuccessful()) {
                    events = response.body();

                    if (events != null && !events.isEmpty()) {
                        EventAdapter adapter = new EventAdapter(events);
                        adapter.setOnItemClickListener(HomeActivity.this);
                        recyclerView.setAdapter(adapter);
                    } else {
                        // Handle case where no events are returned
                    }
                } else {
                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                // Handle failure (e.g., network issues)
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        // Handle click event, e.g., redirect to details page
        Event clickedEvent = events.get(position);

        // Pass event details to the details activity
        Intent intent = new Intent(HomeActivity.this, EventDetailsActivity.class );
        intent.putExtra("event", clickedEvent);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here
        int itemId = item.getItemId();

        if (itemId == R.id.nav_event) {
            // Handle "event" click, e.g., redirect to EventActivity
            startActivity(new Intent(HomeActivity.this, EventActivity.class));
        } else if (itemId == R.id.nav_job) {
            // Handle "Job Connect" click, e.g., redirect to JobConnectActivity
            startActivity(new Intent(HomeActivity.this, JobConnectActivity.class));
        } else if (itemId == R.id.nav_links) {
            // Handle "Campus Links" click, e.g., redirect to CampusLinksActivity
            startActivity(new Intent(HomeActivity.this, CampusLinksActivity.class));
        } else if (itemId == R.id.nav_profile) {
            // Handle "My Profile" click, e.g., redirect to MyProfileActivity
            startActivity(new Intent(HomeActivity.this, MyProfileActivity.class));
        } else if (itemId == R.id.nav_signout) {
            // Handle "Sign Out" click, e.g., perform sign-out logic
            // For example, you can clear user session and go to LoginActivity
            // sessionManager.logoutUser();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish(); // Close the current activity
        }

        // Close the drawer after handling the click
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
