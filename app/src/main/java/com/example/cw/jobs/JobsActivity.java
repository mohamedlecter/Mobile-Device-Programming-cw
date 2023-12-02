package com.example.cw.jobs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cw.SessionManager;
import com.example.cw.api.Api;
import com.example.cw.events.EventDetailsActivity;
import com.example.cw.events.HomeActivity;
import com.example.cw.R;
import com.example.cw.api.RetrofitClient;
import com.example.cw.adapter.JobAdapter;
import com.example.cw.model.Event;
import com.example.cw.model.Job;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobsActivity extends AppCompatActivity implements JobAdapter.OnItemClickListener {

    private Api api;
    private List<Job> jobs;
    private SwipeRefreshLayout swipeRefreshLayout; // to handle pull to refresh
    private RecyclerView recyclerView;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs);
        initView();
        BottomNavigation();
        getJobs();
        onSeeAllClick();
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
        LinearLayout homeBtn = findViewById(R.id.home_Btn);
        LinearLayout jobsBtn = findViewById(R.id.jobs_Btn);


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
    }

    private void getJobs() {
        Call<List<Job>> call;

        boolean isAdmin = sessionManager.isAdmin();
        if (isAdmin) {
            String userId = sessionManager.getUserId();
            Log.d("JobActiviy", "User ID: " + userId);
            call = api.getAdminJobs(userId); // You need to provide the userId
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
                        adapter.setOnItemClickListener(JobsActivity.this);
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
}
