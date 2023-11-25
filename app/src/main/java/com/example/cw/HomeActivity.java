package com.example.cw;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;

import com.example.cw.Api;
import com.example.cw.EventAdapter;
import com.example.cw.RetrofitClient;
import com.example.cw.model.Event;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements EventAdapter.OnItemClickListener {
    private Api api;
    private List<Event> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        api = RetrofitClient.getInstance().getApi();

        // Call the method to get events
        getEvents();
    }

    private void getEvents() {
        Call<List<Event>> call = api.getEvents();
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful()) {
                    events = response.body();

                    RecyclerView recyclerView = findViewById(R.id.eventsRecyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));

                    if (events != null && !events.isEmpty()) {
                        EventAdapter adapter = new EventAdapter(events);
                        adapter.setOnItemClickListener(HomeActivity.this);
                        recyclerView.setAdapter(adapter);
                    } else {
                        // Handle case where no events are returned
                    }
                } else {
                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                // Handle failure (e.g., network issues)
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        // Handle click event, e.g., redirect to details page
        Event clickedEvent = events.get(position);

        // Pass event details to the details activity
        Intent intent = new Intent(HomeActivity.this, EventDetailsActivity.class );
        intent.putExtra("event", clickedEvent);
        startActivity(intent);
    }
}
