package com.example.cw;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cw.adapter.JobAdapter;
import com.example.cw.model.Job;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobsActivity extends AppCompatActivity implements JobAdapter.OnItemClickListener {

    private Api api;
    private List<Job> jobs;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs);
        initView();
        BottomNavigation();
        getJobs();
    }

    private void initView() {
        api = RetrofitClient.getInstance().getApi();
        recyclerView = findViewById(R.id.jobsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
        Call<List<Job>> call = api.getJobs();
        call.enqueue(new Callback<List<Job>>() {
            @Override
            public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
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
        // Handle click event, e.g., redirect to details page
        Job clickedJob = jobs.get(position);

        // Implement your action here
    }
}
