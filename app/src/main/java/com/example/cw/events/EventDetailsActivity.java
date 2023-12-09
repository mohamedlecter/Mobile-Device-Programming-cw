package com.example.cw.events;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cw.R;
import com.example.cw.model.Event;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class EventDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("event")) {
            Event event = (Event) intent.getSerializableExtra("event");

            // Retrieve TextViews from layout
            TextView eventNameTextView = findViewById(R.id.eventNameTextView);
            TextView eventDateTextView = findViewById(R.id.eventDateTextView);
            TextView eventLocationTextView = findViewById(R.id.eventLocationTextView);
            TextView eventOrganizerNameTextView = findViewById(R.id.eventOrganizerNameTextView);
            TextView eventDescTextView = findViewById(R.id.eventDescTextView);
            ImageView eventImageView = findViewById(R.id.eventImage);

//            // Format and display date, day, month, and time
//            String formattedYear = String.valueOf(event.getYear());
//            String formattedDay = String.valueOf(event.getDay());
//            String formattedMonth = event.getMonth();

//            String displayDateTime = String.format("%s, %s %s", formattedDay, formattedMonth, formattedYear);

            // Set event details in the TextViews
            eventNameTextView.setText(event.getTitle());
            eventDateTextView.setText(event.getStartDate());
            eventLocationTextView.setText(event.getLocation());
            eventOrganizerNameTextView.setText(event.getEventOrganizer());
            eventDescTextView.setText(event.getDescription());

            eventLocationTextView.setText(event.getLocation());

            try {
//                http://:4000/uploads/1700915869210ICN.jpg
                String serverBaseUrl = "https://mdp-server-07db49d63c9e.herokuapp.com";
//                    String serverBaseUrl = "http://localhost:4000";
                String imagePath = serverBaseUrl + "/" + event.getImagePath().replace("\\", "/");
                Log.d("ImagePath 2", imagePath);
                Picasso.get()
                        .load(imagePath)
                        .into(eventImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d("Picasso", "Image loaded successfully");
                            }
                            @Override
                            public void onError(Exception e) {
                                Log.e("Picasso", "Error loading image: " + e.getMessage());
                            }
                        });

            } catch (IllegalArgumentException e) {
                // Log an error or provide a placeholder image if needed
                Log.e("EventAdapter", "Failed to load image: " + e.getMessage());
            }

            // Button for redirecting to the home page
            ImageView backButton = findViewById(R.id.buttonBackToHome);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create an intent to go back to the home page
                    Intent homeIntent = new Intent(EventDetailsActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                }
            });
        }
    }
}