package com.example.cw.uiTest;

import android.icu.util.Calendar;
import android.view.View;
import android.widget.DatePicker;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.cw.R;
import com.example.cw.events.AddEvent;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CreateEvent {
    @Rule
    public ActivityScenarioRule<AddEvent> activityScenarioRule =
            new ActivityScenarioRule<>(AddEvent.class);


    @Test
    public void createEventTest() {
        // Disable animations
        ActivityScenario.launch(AddEvent.class).onActivity(activity -> {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        });
        // Input event details
        Espresso.onView(ViewMatchers.withId(R.id.eventTitle))
                .perform(ViewActions.typeText("Test Event Title"));

        Espresso.onView(ViewMatchers.withId(R.id.eventDsc))
                .perform(ViewActions.typeText("Test Event Description"));

        Espresso.onView(ViewMatchers.withId(R.id.eventLocation))
                .perform(ViewActions.typeText("Test Event Location"));

        // Perform image selection (assume an image is selected from the gallery)

        // Open the start date picker
        Espresso.onView(ViewMatchers.withId(R.id.startDate)).perform(ViewActions.click());

        // Get the current date
        Calendar currentDate = Calendar.getInstance();

        // Set the date in the start date picker
        Espresso.onView(ViewMatchers.withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(ViewActions.actionWithAssertions(new ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return ViewMatchers.isAssignableFrom(DatePicker.class);
                    }

                    @Override
                    public String getDescription() {
                        return "Set date on DatePicker";
                    }

                    @Override
                    public void perform(UiController uiController, View view) {
                        DatePicker datePicker = (DatePicker) view;
                        datePicker.updateDate(
                                currentDate.get(Calendar.YEAR),
                                currentDate.get(Calendar.MONTH),
                                currentDate.get(Calendar.DAY_OF_MONTH)
                        );
                    }
                }));


        // Perform date and time selection for start and end dates
        Espresso.onView(ViewMatchers.withId(R.id.endDate)).perform(ViewActions.click());

        // Set the date in the end date picker
        Espresso.onView(ViewMatchers.withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(ViewActions.actionWithAssertions(new ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return ViewMatchers.isAssignableFrom(DatePicker.class);
                    }

                    @Override
                    public String getDescription() {
                        return "Set date on DatePicker";
                    }

                    @Override
                    public void perform(UiController uiController, View view) {
                        DatePicker datePicker = (DatePicker) view;
                        datePicker.updateDate(
                                currentDate.get(Calendar.YEAR),
                                currentDate.get(Calendar.MONTH),
                                currentDate.get(Calendar.DAY_OF_MONTH) + 2
                        );
                    }
                }));


        // Perform button click actions
        Espresso.onView(ViewMatchers.withId(R.id.btnPickImage))
                .perform(ViewActions.click());


        // Perform save button click action
        Espresso.onView(ViewMatchers.withId(R.id.buttonSave))
                .perform(ViewActions.click());


        // Enable animations after the test
        Espresso.onIdle();
    }
}
