package com.example.android_gallery_app;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class EditActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void editActivityTest() {
        onView(withId(R.id.editbutton)).perform(click());
        onView(withId(R.id.age)).perform(click());
        //1100 = input
        //1001 = add button
        //1200 = move button
        //1300 = remove button
        onView(withId(1101)).perform(typeText("First"), closeSoftKeyboard());
        onView(withId(1001)).perform(click());
        onView(withId(1102)).perform(typeText("Second"), closeSoftKeyboard());
        onView(withId(1002)).perform(click());
        onView(withId(1101)).check(matches(withText("First")));
        onView(withId(1301)).perform(click());
        onView(withId(1102)).check(matches(withText("Second")));
        onView(withId(1101)).check(matches(null));
        onView(withId(R.id.savebutton)).perform(click());
    }
}
