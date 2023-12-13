package com.example.cw.uiTest;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.cw.R;
import com.example.cw.auth.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);
    @Test
    public void validUserTest() {

        Espresso.onView(ViewMatchers.withId(R.id.emailField))
                .perform(ViewActions.typeText("test@test.com"));

        Espresso.onView(ViewMatchers.withId(R.id.passwordField))
                .perform(ViewActions.typeText("test"));

        Espresso.onView(ViewMatchers.withId(R.id.signInButton))
                .perform(ViewActions.click());

    }

    @Test
    public void unValidUserTest() {

        Espresso.onView(ViewMatchers.withId(R.id.emailField))
                .perform(ViewActions.typeText("test!testcom"));

        Espresso.onView(ViewMatchers.withId(R.id.passwordField))
                .perform(ViewActions.typeText("test"));

        Espresso.onView(ViewMatchers.withId(R.id.signInButton))
                .perform(ViewActions.click());

    }
}
