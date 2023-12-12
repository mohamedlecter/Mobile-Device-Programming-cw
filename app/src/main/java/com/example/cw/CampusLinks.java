package com.example.cw;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.cw.adapter.LinksAdapter;
import com.example.cw.api.Api;
import com.example.cw.api.RetrofitClient;
import com.example.cw.events.HomeActivity;
import com.example.cw.jobs.JobsActivity;
import com.example.cw.model.Link;
import com.example.cw.profile.profile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CampusLinks extends AppCompatActivity implements LinksAdapter.OnItemClickListener {

    private Api api;
    private List<Link> links;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_links);
        initView();
        getLinks();
        BottomNavigation();
    }

    private void initView() {
        api = RetrofitClient.getInstance().getApi();
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recyclerViewLinks);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sessionManager = new SessionManager(this);

        swipeRefreshLayout.setOnRefreshListener(() -> getLinks());
    }

    private void BottomNavigation() {
        boolean isAdmin = sessionManager.isAdmin();

        FloatingActionButton addButton = findViewById(R.id.buttonAdd);
        addButton.setVisibility(isAdmin ? View.VISIBLE : View.GONE);

        LinearLayout homeBtn = findViewById(R.id.home_Btn);
        LinearLayout jobsBtn = findViewById(R.id.jobs_Btn);
        LinearLayout linksBtn = findViewById(R.id.links_Btn);
        LinearLayout profileBtn = findViewById(R.id.Profile_Btn);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CampusLinks.this, HomeActivity.class));
            }
        });

        jobsBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CampusLinks.this, JobsActivity.class));
            }
        }));
        linksBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CampusLinks.this, CampusLinks.class));

            }
        }));

        profileBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CampusLinks.this, profile.class));
            }
        }));
    }

    private void getLinks() {
        Call<List<Link>> call = api.getLinks();

        call.enqueue(new Callback<List<Link>>() {
            @Override
            public void onResponse(Call<List<Link>> call, Response<List<Link>> response) {
                if (response.isSuccessful()) {
                    links = response.body();

                    if (links != null && !links.isEmpty()) {
                        LinksAdapter adapter = new LinksAdapter(links);
                        adapter.setOnItemClickListener(CampusLinks.this);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Link>> call, Throwable t) {
                // Handle failure (e.g., network issues)
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Link clickedLink = links.get(position);

        if (clickedLink != null && clickedLink.getLink() != null) {
            String linkUrl = clickedLink.getLink();

            // Open the link in a web browser
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl));
            startActivity(browserIntent);
        }
    }
}
