package com.example.cw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.cw.model.Event;

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


            // Set event details in the TextViews
            eventNameTextView.setText(event.getTitle());
            eventDateTextView.setText(event.getDate());
            eventLocationTextView.setText(event.getLocation());
//            eventOrganizerNameTextView.setText("Organizer: " + event.getOrganizer());
            eventDescTextView.setText(event.getDescription());


            // Button for redirecting to the home page
            Button backButton = findViewById(R.id.button3);
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