package com.example.cw.events;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cw.CampusLinks;
import com.example.cw.api.Api;
import com.example.cw.R;
import com.example.cw.api.RetrofitClient;
import com.example.cw.SessionManager;
import com.example.cw.adapter.EventAdapter;
import com.example.cw.jobs.JobsActivity;
import com.example.cw.model.Event;
import com.example.cw.profile.profile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements EventAdapter.OnItemClickListener {

    public Api api;
    private List<Event> events;
    public EventAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout; // to handle pull to refresh
    private RecyclerView recyclerView;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        onSeeAllClick();
        getEvents();
        BottomNavigation();
        setupAddButtonListener();

    }

    private void initView() {
        api = RetrofitClient.getInstance().getApi(); // gets the api from the retrofit client
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); // gets the swipeRefreshLayout
        recyclerView = findViewById(R.id.eventsRecyclerView);

        // Set up the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Set up the SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(() -> getEvents());
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
                startActivity(new Intent(HomeActivity.this, HomeActivity.class));
            }
        });

        jobsBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, JobsActivity.class));
            }
        }));
        linksBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, CampusLinks.class));
            }
        }));

        profileBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, profile.class));
            }
        }));

    }

    public void getEvents() {
        Call<List<Event>> call;

        boolean isAdmin = sessionManager.isAdmin();

        if (isAdmin) {
            String userId = sessionManager.getUserId();
            String userToken = sessionManager.getUserToken();

            // Include the user token in the Authorization header
            String authorizationHeader = "Bearer " + userToken;

            Log.d("HomeActivity", "User ID: " + userId);
            call = api.getAdminEvents(userId, authorizationHeader);
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
                        adapter = new EventAdapter(events);

                        // Set the click listener for regular item click

                        if (!isAdmin) {
                            adapter.setOnItemClickListener(new EventAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    // Handle regular item click, e.g., redirect to details page
                                    Event clickedEvent = events.get(position);
                                    Intent intent = new Intent(HomeActivity.this, EventDetailsActivity.class);
                                    intent.putExtra("event", clickedEvent);
                                    startActivity(intent);
                                }
                            });
                        }

                        adapter.setShareClickListener(new EventAdapter.OnShareClickListener() {
                            @Override
                            public void onShareClick(int position) {
                                Event selectedEvent = events.get(position);
                                shareEvent(selectedEvent);
                            }
                        });

                        // Set the click listener for edit item click
                        adapter.setOnEditClickListener(new EventAdapter.OnEditClickListener() {
                            @Override
                            public void onEditClick(int position) {
                                // Handle edit event click, e.g., redirect to EditEvent activity
                                Event selectedEvent = events.get(position);
                                Intent intent = new Intent(HomeActivity.this, EditEvent.class);
                                intent.putExtra("selectedEvent", selectedEvent);
                                startActivity(intent);
                            }
                        });

                        // Set the click listener for delete item click
                        adapter.setOnDeleteClickListener(new EventAdapter.OnDeleteClickListener() {
                            @Override
                            public void onDeleteClick(int position) {
                                // Handle delete event click
                                showDeleteConfirmationDialog(position);
                            }
                        });

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
        // Handle regular item click, e.g., redirect to details page
        Event clickedEvent = events.get(position);
        Intent intent = new Intent(HomeActivity.this, EventDetailsActivity.class);
        intent.putExtra("event", clickedEvent);
        startActivity(intent);
    }

    private void onSeeAllClick() {
        TextView seeAllTextView = findViewById(R.id.seeALl);
        seeAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click event, e.g., redirect to the Events page
                Intent intent = new Intent(HomeActivity.this, Events.class);
                startActivity(intent);
            }
        });
    }

    private void setupAddButtonListener() {
        FloatingActionButton addButtonLayout = findViewById(R.id.buttonAdd);

        addButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to AddEvent activity
                startActivity(new Intent(HomeActivity.this, AddEvent.class));
            }
        });
    }

    private void shareEvent(Event event) {
        // Create a message to share on social media
        String shareMessage = "Check out this new event that i posted on Notts App!\n\n"
                + "Title: " + event.getTitle() + "\n"
                + "Description: " + event.getDescription() + "\n"
                + "Location: " + event.getLocation() + "\n"
                + "Start Date: " + event.getStartDate() + "\n"
                + "End Date: " + event.getFinishDate();

        // Create an Intent to share the message
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

        // Create a chooser dialog to let the user choose the app for sharing
        Intent chooser = Intent.createChooser(shareIntent, "Share via");

        startActivity(chooser);
    }

    private void showDeleteConfirmationDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this event?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Call the deleteEventApi method with the event ID
                String eventId = events.get(position).getId(); // Replace with your actual event ID field
                String userToken = sessionManager.getUserToken(); // Get the user token from SessionManager
                deleteEventApi(eventId, userToken);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cancel the dialog
                dialog.dismiss();
            }
        });

        builder.show();
    }


    private void deleteEventApi(String eventId, String userToken) {
        Log.d("DeleteEvent", "Event id: " + eventId);

        Call<ResponseBody> call = api.deleteEvent(eventId, "Bearer " + userToken);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Show a toast message for successful deletion
                    Toast.makeText(HomeActivity.this, "Event deleted successfully", Toast.LENGTH_SHORT).show();

                    // Refresh the events
                    getEvents();
                } else {
                    Log.e("DeleteEvent", "Unsuccessful response: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("DeleteEvent", "Error: " + t.getMessage());
            }
        });
    }
}