package com.example.cw.jobs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.cw.R;
import com.example.cw.events.EditEvent;
import com.example.cw.events.HomeActivity;
import com.example.cw.model.Event;
import com.example.cw.model.Job;

public class EditJob extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_job);


        ImageView backButton = findViewById(R.id.buttonBackToJobs);
        // Initialize your EditText fields
        EditText jobTitleEditText = findViewById(R.id.editJobTitle);
        EditText jobDscEditText = findViewById(R.id.editJobDsc);
        EditText jobLocationEditText = findViewById(R.id.editJobLocation);
        EditText jobSalaryEditText = findViewById(R.id.ediJobSalary);

        // Retrieve the selected event from the intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("selectedJob")) {
            Job selectedJob = (Job) intent.getSerializableExtra("selectedJob");

            Log.d("EditJob", "selected Job: " + selectedJob);

            // Populate the EditText fields with the existing values
            if (selectedJob != null) {
                jobTitleEditText.setText(selectedJob.getTitle());
                jobDscEditText.setText(selectedJob.getDescription());
                jobLocationEditText.setText(selectedJob.getLocation());
                jobSalaryEditText.setText(String.valueOf(selectedJob.getSalary()));

            }
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to go back to the home page
                Intent homeIntent = new Intent(EditJob.this, JobsActivity.class);
                startActivity(homeIntent);
            }
        });

    }
}