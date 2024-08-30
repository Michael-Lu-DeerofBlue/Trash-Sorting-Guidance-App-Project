package com.example.samplewebapp;

import android.content.Intent;
import android.os.Build;
import android.view.View;

import androidx.test.core.app.ActivityScenario;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowIntent;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class MainMenuActivityTest {

    private MainMenuActivity mainMenuActivity;

    @Before
    public void setUp() {
        ActivityScenario<MainMenuActivity> scenario = ActivityScenario.launch(MainMenuActivity.class);
        scenario.onActivity(activity -> mainMenuActivity = activity);
    }

    @Test
    public void testOnSearchButtonClick() {
        // Simulate Search button click
        View searchButton = new View(mainMenuActivity);
        mainMenuActivity.onSearchButtonClick(searchButton);

        // Capture the started activity
        ShadowActivity shadowActivity = ShadowActivity.extract(mainMenuActivity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = ShadowIntent.extract(startedIntent);

        // Assert that the correct activity is started
        assertNotNull(startedIntent);
        assertEquals(SearchActivity.class.getName(), shadowIntent.getComponent().getClassName());
        assertEquals(MainMenuActivity.SearchActivity_ID, shadowActivity.getNextStartedActivityRequestCode());
    }

    @Test
    public void testOnFeedbackButtonClick() {
        // Simulate Feedback button click
        View feedbackButton = new View(mainMenuActivity);
        mainMenuActivity.onFeedbackButtonClick(feedbackButton);

        // Capture the started activity
        ShadowActivity shadowActivity = ShadowActivity.extract(mainMenuActivity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = ShadowIntent.extract(startedIntent);

        // Assert that the correct activity is started
        assertNotNull(startedIntent);
        assertEquals(FeedbackActivity.class.getName(), shadowIntent.getComponent().getClassName());
        assertEquals(MainMenuActivity.FeedbackActivity_ID, shadowActivity.getNextStartedActivityRequestCode());
    }
}
