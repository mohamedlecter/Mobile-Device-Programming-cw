package com.example.cw.jobs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.cw.R;
import com.example.cw.events.AddEvent;
import com.example.cw.events.HomeActivity;

public class AddJob extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job);

        ImageView backButton = findViewById(R.id.buttonBackToEvents);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to go back to the home page
                Intent homeIntent = new Intent(AddJob.this, JobsActivity.class);
                startActivity(homeIntent);
            }
        });
    }
}