package com.example.cw.events;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.cw.R;
import com.example.cw.jobs.JobDetailsActivity;
import com.example.cw.jobs.JobsActivity;

public class AddEvent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);


        ImageView backButton = findViewById(R.id.buttonBackToEvents);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to go back to the home page
                Intent homeIntent = new Intent(AddEvent.this, HomeActivity.class);
                startActivity(homeIntent);
            }
        });
    }
}