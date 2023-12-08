package com.example.cw;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.cw.api.Api;
import com.example.cw.jobs.JobsActivity;
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
public class GetJobsTest {

    @Rule
    public ActivityScenarioRule<JobsActivity> jobsActivityScenarioRule = new ActivityScenarioRule<>(JobsActivity.class);

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Api api;

    @Test
    public void testGetJobs() throws IOException {
        // Create a mock response
        List<Job> mockJobs = new ArrayList<>();

        Response<List<Job>> mockResponse = Response.success(mockJobs);

        // Create a mock call
        Call<List<Job>> mockCall = Mockito.mock(Call.class);
        when(mockCall.execute()).thenReturn(mockResponse);

        // Mock the API instance
        when(api.getJobs()).thenReturn(mockCall);

        // Perform the test
        ActivityScenario<JobsActivity> scenario = jobsActivityScenarioRule.getScenario();
        scenario.onActivity(activity -> {
            activity.api = api;
            activity.getJobs();
        });
    }
}
