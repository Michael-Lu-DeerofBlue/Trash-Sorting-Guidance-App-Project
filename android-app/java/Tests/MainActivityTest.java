package com.example.samplewebapp;

import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class MainActivityTest {

    private MainActivity mainActivity;

    @Before
    public void setUp() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity -> mainActivity = activity);
    }

    @Test
    public void testOnConnectButtonClick_Success() {
        // Mock the TextView
        TextView tv = mainActivity.findViewById(R.id.statusField);

        // Mock the network response
        try {
            URL mockUrl = mock(URL.class);
            HttpURLConnection mockConnection = mock(HttpURLConnection.class);
            Scanner mockScanner = mock(Scanner.class);
            JSONObject mockResponse = new JSONObject();
            mockResponse.put("message", "Connection Successful");

            when(mockUrl.openConnection()).thenReturn(mockConnection);
            when(mockConnection.getInputStream()).thenReturn(mockScanner);
            when(mockScanner.nextLine()).thenReturn(mockResponse.toString());

            // Simulate button click
            mainActivity.onConnectButtonClick(new View(mainActivity));

            // Assert that the message was correctly set and displayed
            assertEquals("Connection Successful", mainActivity.message);
            assertEquals("Connection Successful", tv.getText().toString());
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testOnConnectButtonClick_Failure() {
        // Mock the TextView
        TextView tv = mainActivity.findViewById(R.id.statusField);

        // Simulate a network failure
        try {
            URL mockUrl = mock(URL.class);
            when(mockUrl.openConnection()).thenThrow(new RuntimeException("Failed to connect"));

            // Simulate button click
            mainActivity.onConnectButtonClick(new View(mainActivity));

            // Assert that the failure message was correctly set and displayed
            assertTrue(mainActivity.message.contains("Failed to connect"));
            assertTrue(tv.getText().toString().contains("Failed to connect"));
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testOnConnectButtonClick_Timeout() {
        // Mock the TextView
        TextView tv = mainActivity.findViewById(R.id.statusField);

        // Simulate a delay that exceeds the timeout
        try {
            URL mockUrl = mock(URL.class);
            HttpURLConnection mockConnection = mock(HttpURLConnection.class);
            when(mockUrl.openConnection()).thenReturn(mockConnection);
            when(mockConnection.getInputStream()).thenAnswer(invocation -> {
                Thread.sleep(3000); // Simulate delay
                return null;
            });

            // Simulate button click
            mainActivity.onConnectButtonClick(new View(mainActivity));

            // Assert that the message is null due to timeout and no response was received
            assertNull(mainActivity.message);
            assertEquals("", tv.getText().toString());
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }
}
