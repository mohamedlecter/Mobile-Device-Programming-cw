package com.example.cw.jobs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cw.R;
import com.example.cw.SessionManager;
import com.example.cw.api.Api;
import com.example.cw.api.RetrofitClient;
import com.example.cw.events.EditEvent;
import com.example.cw.events.EventUtils;
import com.example.cw.events.HomeActivity;
import com.example.cw.model.Event;
import com.example.cw.model.Job;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditJob extends AppCompatActivity {
    private EditText jobTitleEditText, jobDscEditText, jobLocationEditText, jobSalaryEditText, companyEditText;
    private TextView startDateEditText, endDateEditText;
    private ImageView backButton;
    private Button saveButton;
    private Job selectedJob;
    private SessionManager sessionManager;
    private Api api;
    private Calendar calendarStart;
    private Calendar calendarEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_job);
        sessionManager = new SessionManager(this);
        api = RetrofitClient.getInstance().getApi();

        calendarStart = Calendar.getInstance();
        calendarEnd = Calendar.getInstance();


        // Initialize your EditText fields
        backButton = findViewById(R.id.buttonBackToJobs);
        jobTitleEditText = findViewById(R.id.editJobTitle);
        jobDscEditText = findViewById(R.id.editJobDsc);
        jobLocationEditText = findViewById(R.id.editJobLocation);
        companyEditText = findViewById(R.id.editCompany);
        jobSalaryEditText = findViewById(R.id.editSalary);
        startDateEditText = findViewById(R.id.editStartJobDate);
        endDateEditText = findViewById(R.id.editEndJobDate);
        saveButton = findViewById(R.id.editButtonSave);

        // Retrieve the selected event from the intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("selectedJob")) {
            selectedJob = (Job) intent.getSerializableExtra("selectedJob");

            Log.d("EditJob", "selected Job: " + selectedJob);

            // Populate the EditText fields with the existing values
            if (selectedJob != null) {
                jobTitleEditText.setText(selectedJob.getTitle());
                jobDscEditText.setText(selectedJob.getDescription());
                jobLocationEditText.setText(selectedJob.getLocation());
                companyEditText.setText(selectedJob.getCompany());
                jobSalaryEditText.setText(String.valueOf(selectedJob.getSalary()));

                startDateEditText.setText(selectedJob.getJobDurationStart());
                endDateEditText.setText(selectedJob.getJobDurationEnd());

            }
        }

        startDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobUtils.showDateTimePicker(
                        EditJob.this,
                        true,
                        startDateEditText,
                        endDateEditText,
                        calendarStart,
                        calendarEnd,
                        selectedJob
                );
            }
        });

        endDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobUtils.showDateTimePicker(
                        EditJob.this,
                        false,
                        startDateEditText,
                        endDateEditText,
                        calendarStart,
                        calendarEnd,
                        selectedJob
                );
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to go back to the home page
                Intent homeIntent = new Intent(EditJob.this, JobsActivity.class);
                startActivity(homeIntent);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Populate the Job object with data
                selectedJob.setTitle(jobTitleEditText.getText().toString());
                selectedJob.setDescription(jobDscEditText.getText().toString());
                selectedJob.setLocation(jobLocationEditText.getText().toString());
                selectedJob.setCompany(companyEditText.getText().toString());


                // Create a new Job object to store the updated values
                Job updatedJob = new Job();

                // Check and update the title
                String updatedTitle = jobTitleEditText.getText().toString();
                if (!updatedTitle.equals(selectedJob.getTitle())) {
                    updatedJob.setTitle(updatedTitle);
                } else {
                    updatedJob.setTitle(selectedJob.getTitle());
                }

                // Check and update the description
                String updatedDescription = jobDscEditText.getText().toString();
                if (!updatedDescription.equals(selectedJob.getDescription())) {
                    updatedJob.setDescription(updatedDescription);
                } else {
                    updatedJob.setDescription(selectedJob.getDescription());
                }

                // Check and update the location
                String updatedLocation = jobLocationEditText.getText().toString();
                if (!updatedLocation.equals(selectedJob.getLocation())) {
                    updatedJob.setLocation(updatedLocation);
                } else {
                    updatedJob.setLocation(selectedJob.getLocation());
                }
                // Check and update the company
                String updatedCompany = companyEditText.getText().toString();
                if (!updatedCompany.equals(selectedJob.getCompany())) {
                    updatedJob.setCompany(updatedCompany);
                } else {
                    updatedJob.setCompany(selectedJob.getCompany());
                }

                // Check and update the salary
                int updatedSalary = Integer.parseInt(jobSalaryEditText.getText().toString());
                if (updatedSalary != selectedJob.getSalary()) {
                    updatedJob.setSalary(updatedSalary);
                } else {
                    updatedJob.setSalary(selectedJob.getSalary());
                }

                if (calendarStart != null) {
                    updatedJob.setJobHourStart(calendarStart.getTime().toString());
                    updatedJob.setJobDurationStart(selectedJob.getJobDurationStart());  // Retain the original start date
                } else {
                    updatedJob.setJobHourStart(selectedJob.getJobHourStart());
                    updatedJob.setJobDurationStart(selectedJob.getJobDurationStart());
                }

                // Check and update the end date
                if (calendarEnd != null) {
                    updatedJob.setJobHourEnd(calendarEnd.getTime().toString());
                    updatedJob.setJobDurationEnd(selectedJob.getJobDurationEnd());  // Retain the original finish date
                } else {
                    updatedJob.setJobHourEnd(selectedJob.getJobHourEnd());
                    updatedJob.setJobDurationEnd(selectedJob.getJobDurationEnd());
                }


                // Print the states before the problematic code block
                Log.d("EditJob", "CalendarStart: " + calendarStart.getTime().toString());
                Log.d("EditJob", "CalendarEnd: " + calendarEnd.getTime().toString());

                // Print the states before the problematic code block
                Log.d("EditJob", "getJobHourStart: " + updatedJob.getJobHourStart());
                Log.d("EditJob", "setJobDurationStart: " + updatedJob.getJobDurationStart());


                // Print the states before the problematic code block
                Log.d("EditJob", "setJobHourEnd: " + updatedJob.getJobHourEnd());
                Log.d("EditJob", "setJobDurationEnd: " + updatedJob.getJobDurationEnd());

                // Make the API call
                updateJob(updatedJob);

            }
        });
    }

    private void updateJob(Job updatedJob) {
        String userToken = sessionManager.getUserToken();
        String authorizationHeader = "Bearer " + userToken;

        // Make the API call for updating the job
        Call<Job> call = api.updateJob(authorizationHeader, selectedJob.getId(), updatedJob);

        call.enqueue(new Callback<Job>() {
            @Override
            public void onResponse(Call<Job> call, Response<Job> response) {
                if (response.isSuccessful()) {
                    Job updatedJob = response.body();
                    if (updatedJob != null) {
                        Log.d("EditJob", "Updated Job ID: " + updatedJob.getId());

                        Intent jobsIntent = new Intent(EditJob.this, JobsActivity.class);
                        startActivity(jobsIntent);
                        finish();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("EditJob", "Error response body: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Job> call, Throwable t) {
                Log.e("EditJob", "API call failed: " + t.getMessage());
            }
        });
    }

}