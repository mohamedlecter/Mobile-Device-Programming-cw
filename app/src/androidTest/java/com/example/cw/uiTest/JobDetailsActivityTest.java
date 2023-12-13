package com.example.cw.uiTest;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.cw.R;
import com.example.cw.jobs.JobDetailsActivity;
import com.example.cw.model.Job;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.core.StringContains.containsString;

import android.content.Intent;
import android.widget.TextView;

public class JobDetailsActivityTest {
    @Rule
    public ActivityScenarioRule<JobDetailsActivity> activityScenarioRule = new ActivityScenarioRule<>(
            JobDetailsActivity.class);

    @Test
    public void testJobDetailsDisplayed() {
        String expectedJobTitle = "Event Name";

        // Use onActivity to interact with the activity
        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.runOnUiThread(() -> {
                TextView jobTtileTextView = activity.findViewById(R.id.jobText);
                jobTtileTextView.setText(expectedJobTitle);
            });
        });

        // Verify that the text is displayed
        Espresso.onView(ViewMatchers.withId(R.id.jobText))
                .check(matches(withText(containsString(expectedJobTitle))));

        // Set the text of eventLocationTextView to "Event Location"
        String expectedLocation = "Job Location";

        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.runOnUiThread(() -> {
                TextView jobLocationTextView = activity.findViewById(R.id.jobLocation);
                jobLocationTextView.setText(expectedLocation);
            });
        });

        Espresso.onView(ViewMatchers.withId(R.id.jobLocation))
                .check(matches(withText(containsString(expectedLocation))));

        // Set the text of eventOrganizerNameTextView to "Event Organizer Name"
        String expectedOrganizer = "Job Organizer Name";

        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.runOnUiThread(() -> {
                TextView jobOrganizerNameTextView = activity.findViewById(R.id.jobOrganizer);
                jobOrganizerNameTextView.setText(expectedOrganizer);
            });
        });

        Espresso.onView(ViewMatchers.withId(R.id.jobOrganizer))
                .check(matches(withText(containsString(expectedOrganizer))));

        String expectedDesc = "Job Description";

        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.runOnUiThread(() -> {
                TextView jobDescTextView = activity.findViewById(R.id.jobDescriptionText);
                jobDescTextView.setText(expectedDesc);
            });
        });

        Espresso.onView(ViewMatchers.withId(R.id.jobDescriptionText))
                .check(matches(withText(containsString(expectedDesc))));
    }

}
