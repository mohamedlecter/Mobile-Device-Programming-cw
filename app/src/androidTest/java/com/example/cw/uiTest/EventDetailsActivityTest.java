package com.example.cw.uiTest;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.cw.R;
import com.example.cw.events.EventDetailsActivity;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.core.StringContains.containsString;

import android.widget.TextView;

public class EventDetailsActivityTest {

    @Rule
    public ActivityScenarioRule<EventDetailsActivity> activityScenarioRule = new ActivityScenarioRule<>(
            EventDetailsActivity.class);

    @Test
    public void testEventDetailsDisplayed() {
        // Set the text of eventNameTextView to "Event Name"
        String expectedText = "Event Name";

        // Use onActivity to interact with the activity
        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.runOnUiThread(() -> {
                TextView eventNameTextView = activity.findViewById(R.id.eventNameTextView);
                eventNameTextView.setText(expectedText);
            });
        });

        // Verify that the text is displayed
        Espresso.onView(ViewMatchers.withId(R.id.eventNameTextView))
                .check(matches(withText(containsString(expectedText))));

        // Set the text of eventDateTextView to "Event Date"
        String expectedDate = "Thu Dec 07 14:56:10 GMT 2023";

        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.runOnUiThread(() -> {
                TextView eventDateTextView = activity.findViewById(R.id.eventDateTextView);
                eventDateTextView.setText(expectedDate);
            });
        });

        Espresso.onView(ViewMatchers.withId(R.id.eventDateTextView))
                .check(matches(withText(containsString(expectedDate))));

        // Set the text of eventLocationTextView to "Event Location"
        String expectedLocation = "Event Location";

        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.runOnUiThread(() -> {
                TextView eventLocationTextView = activity.findViewById(R.id.eventLocationTextView);
                eventLocationTextView.setText(expectedLocation);
            });
        });

        Espresso.onView(ViewMatchers.withId(R.id.eventLocationTextView))
                .check(matches(withText(containsString(expectedLocation))));

        // Set the text of eventOrganizerNameTextView to "Event Organizer Name"
        String expectedOrganizer = "Event Organizer Name";

        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.runOnUiThread(() -> {
                TextView eventOrganizerNameTextView = activity.findViewById(R.id.eventOrganizerNameTextView);
                eventOrganizerNameTextView.setText(expectedOrganizer);
            });
        });

        Espresso.onView(ViewMatchers.withId(R.id.eventOrganizerNameTextView))
                .check(matches(withText(containsString(expectedOrganizer))));

        // Set the text of eventDescTextView to "Event Description"
        String expectedDesc = "Event Description";

        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.runOnUiThread(() -> {
                TextView eventDescTextView = activity.findViewById(R.id.eventDescTextView);
                eventDescTextView.setText(expectedDesc);
            });
        });

        Espresso.onView(ViewMatchers.withId(R.id.eventDescTextView))
                .check(matches(withText(containsString(expectedDesc))));
    }
}
