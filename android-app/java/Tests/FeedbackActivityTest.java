package com.example.samplewebapp;

import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class FeedbackActivityTest {

    private FeedbackActivity feedbackActivity;

    @Before
    public void setUp() {
        ActivityScenario<FeedbackActivity> scenario = ActivityScenario.launch(FeedbackActivity.class);
        scenario.onActivity(activity -> feedbackActivity = activity);
    }

    @Test
    public void testEmptyFieldsValidation() {
        // Set up the EditText fields with empty values
        EditText nameEditText = feedbackActivity.findViewById(R.id.objectNameEditText);
        EditText categoryEditText = feedbackActivity.findViewById(R.id.categoryEditText);
        EditText feedbackEditText = feedbackActivity.findViewById(R.id.feedbackEditText);

        nameEditText.setText("");
        categoryEditText.setText("");
        feedbackEditText.setText("");

        // Simulate button click
        feedbackActivity.onSubmitFeedbackButtonClick(new View(feedbackActivity));

        // Assert that the toast message is correct
        assertEquals("Please fillout at least one field", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testPartialFieldsValidation() {
        // Set up the EditText fields with partial values
        EditText nameEditText = feedbackActivity.findViewById(R.id.objectNameEditText);
        EditText categoryEditText = feedbackActivity.findViewById(R.id.categoryEditText);
        EditText feedbackEditText = feedbackActivity.findViewById(R.id.feedbackEditText);

        nameEditText.setText("TestName");
        categoryEditText.setText("");
        feedbackEditText.setText("");

        // Simulate button click
        feedbackActivity.onSubmitFeedbackButtonClick(new View(feedbackActivity));

        // Assert that the toast message is correct
        assertEquals("Please fillout all the fields", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testFullFieldsValidation() {
        // Set up the EditText fields with valid values
        EditText nameEditText = feedbackActivity.findViewById(R.id.objectNameEditText);
        EditText categoryEditText = feedbackActivity.findViewById(R.id.categoryEditText);
        EditText feedbackEditText = feedbackActivity.findViewById(R.id.feedbackEditText);

        nameEditText.setText("TestName");
        categoryEditText.setText("TestCategory");
        feedbackEditText.setText("TestFeedback");

        // Mock the HTTP connection and response
        try {
            URL mockUrl = mock(URL.class);
            HttpURLConnection mockConnection = mock(HttpURLConnection.class);
            Scanner mockScanner = mock(Scanner.class);
            JSONObject mockResponse = new JSONObject();
            mockResponse.put("message", "Success");

            when(mockUrl.openConnection()).thenReturn(mockConnection);
            when(mockConnection.getInputStream()).thenReturn(mockScanner);
            when(mockScanner.nextLine()).thenReturn(mockResponse.toString());

            feedbackActivity.onSubmitFeedbackButtonClick(new View(feedbackActivity));

            // Assert that the message is set correctly and the toast is displayed
            assertEquals("Success", feedbackActivity.message);
            assertEquals("Success", ShadowToast.getTextOfLatestToast());
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testOnBackFeedbackButtonClick() {
        // Simulate back button click
        feedbackActivity.onBackFeedbackButtonClick(new View(feedbackActivity));

        // Assert that the activity is finished
        assertTrue(feedbackActivity.isFinishing());
    }
}
