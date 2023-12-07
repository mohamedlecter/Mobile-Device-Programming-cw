package com.example.cw.jobs;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.cw.R;
import com.example.cw.SessionManager;
import com.example.cw.api.Api;
import com.example.cw.api.RetrofitClient;
import com.example.cw.events.AddEvent;
import com.example.cw.events.HomeActivity;
import com.example.cw.model.Event;
import com.example.cw.model.Job;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddJob extends AppCompatActivity {
    private Api api;

    private EditText jobSalaryEditText;
    private EditText jobLocationEditText;
    private EditText jobDescEditText;
    private EditText jobTitleEditText;
    private EditText companyEditText;

    private TextView startDateTextView;
    private TextView endDateTextView;
    private Button saveButton;

    private Calendar calendarStart;
    private Calendar calendarEnd;
    private Job job; // Declare the job object
    private SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job);
        api = RetrofitClient.getInstance().getApi(); // gets the api from the retrofit client

        // Initialize UI elements
        jobTitleEditText = findViewById(R.id.jobTitle);
        jobDescEditText = findViewById(R.id.jobDsc);
        jobLocationEditText = findViewById(R.id.jobLocation);
        companyEditText = findViewById(R.id.company);
        jobSalaryEditText = findViewById(R.id.salary);

        startDateTextView = findViewById(R.id.startJobDate);
        endDateTextView = findViewById(R.id.endJobDate);

        ImageView backButton = findViewById(R.id.buttonBackToEvents);
        saveButton = findViewById(R.id.buttonSave);


        sessionManager = new SessionManager(this);
        calendarStart = Calendar.getInstance();
        calendarEnd = Calendar.getInstance();
        job = new Job(); // Initialize the Event object

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to go back to the home page
                Intent homeIntent = new Intent(AddJob.this, JobsActivity.class);
                startActivity(homeIntent);
            }
        });

        // Set up the click listener for the date and time picker
        startDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(true); // Pass true to indicate the start date/time
            }
        });

        endDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(false); // Pass false to indicate the end date/time
            }
        });

        // TextWatchers to perform real-time validation
        jobTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateJobTitle(charSequence.toString());
                updateSaveButtonState();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not used
            }
        });

        jobDescEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateJobDescription(charSequence.toString());
                updateSaveButtonState();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not used
            }
        });

        jobLocationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateJobLocation(charSequence.toString());
                updateSaveButtonState();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not used
            }
        });

        companyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateCompany(charSequence.toString());
                updateSaveButtonState();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not used
            }
        });

        jobSalaryEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateJobSalary(charSequence.toString());
                updateSaveButtonState();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not used
            }
        });

        endDateTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateEndDate(charSequence.toString());
                updateSaveButtonState();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not used
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Populate the Job object with data
                job.setTitle(jobTitleEditText.getText().toString());
                job.setDescription(jobDescEditText.getText().toString());
                job.setLocation(jobLocationEditText.getText().toString());
                job.setCompany(companyEditText.getText().toString());
                int salary = Integer.parseInt(jobSalaryEditText.getText().toString());
                job.setSalary(salary);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd, MMM yyyy", Locale.getDefault());

                job.setJobDurationStart(dateFormat.format(calendarStart.getTime()));
                job.setJobDurationEnd(dateFormat.format(calendarEnd.getTime()));

                String userToken = sessionManager.getUserToken();
                String authorizationHeader = "Bearer " + userToken;

                // Makes the API call
                Call<Job> call = api.postJob(authorizationHeader, job);
                call.enqueue(new Callback<Job>() {
                    @Override
                    public void onResponse(Call<Job> call, Response<Job> response) {
                        if (response.isSuccessful()) {
                            Job newJob = response.body();
                            if (newJob != null) {
                                Intent homeIntent = new Intent(AddJob.this, JobsActivity.class);
                                startActivity(homeIntent);
                                finish();
                            }
                        } else {
                            try {
                                String errorBody = response.errorBody().string();
                                Log.e("AddJob", "Error response body: " + errorBody);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Job> call, Throwable t) {
                        Log.e("AddJob", "API call failed: " + t.getMessage());
                    }

                });
            }
        });

    }

    private void showDateTimePicker(final boolean isStartDate) {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (DatePicker datePicker, int year, int month, int day) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, day);

                    // Show TimePicker after selecting the date
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            this,
                            (TimePicker timePicker, int hour, int minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hour);
                                calendar.set(Calendar.MINUTE, minute);

                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                String selectedTime = sdf.format(calendar.getTime());

                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd, MMM yyyy", Locale.getDefault());
                                String formattedDate = dateFormat.format(calendar.getTime());


                                // Set the selected date and time to the appropriate TextView
                                if (isStartDate) {
                                    startDateTextView.setText(selectedTime + " " + formattedDate);
                                    calendarStart = calendar;
                                    job.setJobHourStart(selectedTime);
                                    job.setJobDurationStart(formattedDate);
                                } else {
                                    endDateTextView.setText(selectedTime + " " + formattedDate);
                                    calendarEnd = calendar;
                                    job.setJobHourEnd(selectedTime);
                                    job.setJobDurationEnd(formattedDate);

                                }
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            false
                    );
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }


    private void validateJobTitle(String jobTitle) {
        if (jobTitle.isEmpty()) {
            jobTitleEditText.setError("Job title cannot be empty");
        } else {
            jobTitleEditText.setError(null);
        }

        updateSaveButtonState();
    }

    private void validateJobDescription(String jobDescription) {
        if (jobDescription.isEmpty()) {
            jobDescEditText.setError("Job description cannot be empty");
        } else {
            jobDescEditText.setError(null);
        }

        updateSaveButtonState();
    }

    private void validateJobLocation(String jobLocation) {
        if (jobLocation.isEmpty()) {
            jobLocationEditText.setError("Job location cannot be empty");
        } else {
            jobLocationEditText.setError(null);
        }

        updateSaveButtonState();
    }

    private void validateEndDate(String endDate) {
        // Validate that the end date is not before the start date

        String startDate = startDateTextView.getText().toString().trim();
        Log.d("Addjob", "is valid date: " + isValidDate(startDate, endDate));
        if (isValidDate(startDate, endDate)) {
            endDateTextView.setError(null);
        } else {
            endDateTextView.setError("End date cannot be before the start date");
        }
    }

    private void validateCompany(String company) {
        if (company.isEmpty()) {
            companyEditText.setError("Company cannot be empty");
        } else {
            companyEditText.setError(null);
        }

        updateSaveButtonState();
    }

    private void validateJobSalary(String jobSalary) {
        if (jobSalary.isEmpty()) {
            jobSalaryEditText.setError("Job salary cannot be empty");
        } else {
            try {
                int salary = Integer.parseInt(jobSalary);
                if (salary <= 0) {
                    jobSalaryEditText.setError("Job salary must be a positive number");
                } else {
                    jobSalaryEditText.setError(null);
                }
            } catch (NumberFormatException e) {
                jobSalaryEditText.setError("Invalid job salary format");
            }
        }

        updateSaveButtonState();
    }

    private boolean isValidDate(String startDate, String endDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd, MMM yyyy", Locale.getDefault());

        try {
            Date startDateObj = dateFormat.parse(startDate);
            Date endDateObj = dateFormat.parse(endDate);

            // Check if the end date is not before the start date
            if (endDateObj.before(startDateObj)) {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false; // Handle parsing errors
        }
        return true;
    }


    private void updateSaveButtonState() {
        String jobTitle = jobTitleEditText.getText().toString().trim();
        String jobDes = jobDescEditText.getText().toString().trim();
        String jobLocation = jobLocationEditText.getText().toString().trim();
        String comapny = companyEditText.getText().toString().trim();
        String jobSalary = jobSalaryEditText.getText().toString().trim();


        boolean isJobTitleValid = !jobTitle.isEmpty();
        boolean isJobDescValid = !jobDes.isEmpty();
        boolean isJobLocationValid = !jobLocation.isEmpty();
        boolean isCompanyValid = !comapny.isEmpty();
        boolean isJobSalaryValid = !jobSalary.isEmpty() && isValidJobSalary(jobSalary);
        boolean isEndDateValid = isValidDate(startDateTextView.getText().toString().trim(), endDateTextView.getText().toString().trim());

        saveButton.setEnabled(isJobTitleValid && isJobDescValid &&
                isJobLocationValid &&
                isCompanyValid && isJobSalaryValid && isEndDateValid);
    }

    private boolean isValidJobSalary(String jobSalary) {
        try {
            int salary = Integer.parseInt(jobSalary);
            return salary > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}