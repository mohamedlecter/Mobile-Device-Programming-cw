package com.example.cw.jobs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cw.R;
import com.example.cw.model.Job;

public class JobDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("job")) {
            Job job = (Job) intent.getSerializableExtra("job");

            // Retrieve TextViews from layout
            TextView jobText = findViewById(R.id.jobText);
            TextView jobLocation = findViewById(R.id.jobLocation);
            TextView jobDuration = findViewById(R.id.jobDuration);
            TextView jobHours = findViewById(R.id.jobHours);
            TextView jobOrganizer = findViewById(R.id.jobOrganizer);
            TextView jobDesc = findViewById(R.id.jobDescriptionText);

            String jobDurationStart = job.getJobDurationStart();
            String jobDurationEnd = job.getJobDurationEnd();

            String displayjobDuration = String.format("%s - %s", jobDurationStart, jobDurationEnd);

            String jobHourStart = job.getJobHourStart();
            String jobHourEnd = job.getJobHourEnd();

            String displayjobHours = String.format("%s - %s", jobHourStart, jobHourEnd);

            // Set job details in the TextViews
            jobText.setText(job.getTitle());
            jobLocation.setText(job.getLocation());
            jobDuration.setText(displayjobDuration);
            jobHours.setText(displayjobHours);
            jobOrganizer.setText(job.getJobOrganizer());
            jobDesc.setText(job.getDescription());

            ImageView shareButton = findViewById(R.id.buttonShare);
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");

                    String shareText = String.format("Check out this job: %s from %s at %s",
                            job.getTitle(), String.format("%s - %s",job.getJobDurationStart(), job.getJobDurationEnd()), job.getLocation());

                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

                    startActivity(Intent.createChooser(shareIntent, "Share Event"));

                }
            });

            ImageView backButton = findViewById(R.id.buttonBackToJobs);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create an intent to go back to the home page
                    Intent homeIntent = new Intent(JobDetailsActivity.this, JobsActivity.class);
                    startActivity(homeIntent);
                }
            });
        }
    }
}