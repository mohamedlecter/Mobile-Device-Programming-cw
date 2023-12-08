package com.example.cw.jobs;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cw.CampusLinks;
import com.example.cw.SessionManager;
import com.example.cw.api.Api;
import com.example.cw.events.EventDetailsActivity;
import com.example.cw.events.HomeActivity;
import com.example.cw.R;
import com.example.cw.api.RetrofitClient;
import com.example.cw.adapter.JobAdapter;
import com.example.cw.model.Event;
import com.example.cw.model.Job;
import com.example.cw.profile.profile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobsActivity extends AppCompatActivity implements JobAdapter.OnItemClickListener {

    public Api api;
    private List<Job> jobs;
    private SwipeRefreshLayout swipeRefreshLayout; // to handle pull to refresh
    private RecyclerView recyclerView;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs);
        initView();
        onSeeAllClick();
        getJobs();
        BottomNavigation();
        setupAddButtonListener();

    }

    private void initView() {
        api = RetrofitClient.getInstance().getApi();
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); // gets the swipeRefreshLayout
        recyclerView = findViewById(R.id.jobsRecyclerView);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Set up the SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(() -> getJobs());
    }

    private void BottomNavigation() {
        boolean isAdmin = sessionManager.isAdmin();

        // Set the visibility of the add button based on the user's role
        FloatingActionButton addButton = findViewById(R.id.buttonAdd);
        addButton.setVisibility(isAdmin ? View.VISIBLE : View.GONE);

        LinearLayout homeBtn = findViewById(R.id.home_Btn);
        LinearLayout jobsBtn = findViewById(R.id.jobs_Btn);
        LinearLayout linksBtn = findViewById(R.id.links_Btn);
        LinearLayout profileBtn = findViewById(R.id.Profile_Btn);


        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(JobsActivity.this, HomeActivity.class));
            }
        });

        jobsBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(JobsActivity.this, JobsActivity.class));
            }
        }));

        linksBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(JobsActivity.this, CampusLinks.class));
            }
        }));

        profileBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(JobsActivity.this, profile.class));
            }
        }));
    }

    public void getJobs() {
        Call<List<Job>> call;

        boolean isAdmin = sessionManager.isAdmin();
        if (isAdmin) {
            String userId = sessionManager.getUserId();
            Log.d("JobActiviy", "User ID: " + userId);
            call = api.getAdminJobs(userId);
        } else {
            call = api.getJobs();
        }
        call.enqueue(new Callback<List<Job>>() {
            @Override
            public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
                swipeRefreshLayout.setRefreshing(false); // Stop the refresh animation

                if (response.isSuccessful()) {
                    jobs = response.body();

                    if (jobs != null && !jobs.isEmpty()) {
                        JobAdapter adapter = new JobAdapter(jobs);
                        // Set the click listener for edit item click


                        adapter.setOnItemClickListener(new JobAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                    // Handle regular item click, e.g., redirect to details page
                                    Job selectedJob = jobs.get(position);
                                    Intent intent = new Intent(JobsActivity.this, JobDetailsActivity.class);
                                    intent.putExtra("job", selectedJob);
                                    startActivity(intent);
                            }
                        });
                        adapter.setOnEditClickListener(new JobAdapter.OnEditClickListener() {
                            @Override
                            public void onEditClick(int position) {
                                Job selectedJob = jobs.get(position);
                                Intent intent = new Intent(JobsActivity.this, EditJob.class);
                                intent.putExtra("selectedJob", selectedJob);
                                startActivity(intent);
                            }
                        });
                        // Set the click listener for delete item click
                        adapter.setOnDeleteClickListener(new JobAdapter.OnDeleteClickListener() {
                            @Override
                            public void onDeleteClick(int position) {
                                showDeleteConfirmationDialog(position);
                            }
                        });


                        recyclerView.setAdapter(adapter);


                    } else {
                        // Handle case where no jobs are returned
                    }
                } else {
                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(Call<List<Job>> call, Throwable t) {
                // Handle failure (e.g., network issues)
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Job clickedJob = jobs.get(position);

        // Pass job details to the details activity
        Intent intent = new Intent(JobsActivity.this, JobDetailsActivity.class);
        intent.putExtra("job", clickedJob);
        startActivity(intent);
    }

    private void onSeeAllClick() {
        TextView seeAllTextView = findViewById(R.id.seeALl);
        seeAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JobsActivity.this, SeeAllJobs.class);
                startActivity(intent);
            }
        });
    }

    private void showDeleteConfirmationDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this job?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String jobId = jobs.get(position).getId();
                String userToken = sessionManager.getUserToken();
                deleteJobApi(jobId, userToken);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cancel the dialog
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void setupAddButtonListener() {
        FloatingActionButton addButtonLayout = findViewById(R.id.buttonAdd);

        addButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(JobsActivity.this, AddJob.class));
            }
        });
    }

    private void deleteJobApi(String jobId, String userToken) {
        Log.d("DeleteJob", "Job id: " + jobId);

        Call<ResponseBody> call = api.deleteJob(jobId, "Bearer " + userToken);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Show a toast message for successful deletion
                    Toast.makeText(JobsActivity.this, "Job deleted successfully", Toast.LENGTH_SHORT).show();

                    // Refresh the jobs
                    getJobs();
                } else {
                    Log.e("DeleteJob", "Unsuccessful response: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("DeleteJob", "Error: " + t.getMessage());
            }
        });
    }
}
