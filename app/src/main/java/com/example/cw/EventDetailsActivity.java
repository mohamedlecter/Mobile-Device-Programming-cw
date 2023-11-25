package com.example.cw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

            // Retrieve TextView from layout
            TextView eventNameTextView = findViewById(R.id.eventNameTextView);

            // Set event name in the TextView
            eventNameTextView.setText(event.getTitle());
        }
    }
}