package com.example.cw;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.cw.api.Api;
import com.example.cw.events.HomeActivity;
import com.example.cw.jobs.JobsActivity;
import com.example.cw.model.Event;
import com.example.cw.model.Job;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class GetEventsTest {

    @Rule
    public ActivityScenarioRule<HomeActivity> hommeActivityScenarioRule = new ActivityScenarioRule<>(HomeActivity.class);

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Api api;

    @Test
    public void testGetEvents() throws IOException {
        // Create a mock response
        List<Event> mockEvents = new ArrayList<>();

        Response<List<Event>> mockResponse = Response.success(mockEvents);

        // Create a mock call
        Call<List<Event>> mockCall = Mockito.mock(Call.class);
        when(mockCall.execute()).thenReturn(mockResponse);

        // Mock the API instance
        when(api.getEvents()).thenReturn(mockCall);

        // Set the mock API instance
        hommeActivityScenarioRule.getScenario().onActivity(activity -> activity.api = api);

        // Perform the test
        hommeActivityScenarioRule.getScenario().onActivity(HomeActivity::getEvents);
    }
}
