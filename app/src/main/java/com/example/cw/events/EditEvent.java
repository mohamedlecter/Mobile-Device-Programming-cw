package com.example.cw.events;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.cw.R;
import com.example.cw.model.Event;

public class EditEvent extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        ImageView backButton = findViewById(R.id.buttonBackToEvents);
        // Initialize your EditText fields
        EditText eventTitleEditText = findViewById(R.id.editEventTitle);
        EditText eventDscEditText = findViewById(R.id.editEventDsc);
        EditText eventLocationEditText = findViewById(R.id.editEventLocation);
        EditText eventDateEditText = findViewById(R.id.editEventDate);

        // Retrieve the selected event from the intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("selectedEvent")) {
            Event selectedEvent = (Event) intent.getSerializableExtra("selectedEvent");

            Log.d("EditEvent", "Selected Event: " + selectedEvent);

            // Populate the EditText fields with the existing values
            if (selectedEvent != null) {
                eventTitleEditText.setText(selectedEvent.getTitle());
                eventDscEditText.setText(selectedEvent.getDescription());
                eventLocationEditText.setText(selectedEvent.getLocation());
                eventDateEditText.setText(selectedEvent.getStartDate());
            }
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to go back to the home page
                Intent homeIntent = new Intent(EditEvent.this, HomeActivity.class);
                startActivity(homeIntent);
            }
        });
    }
}