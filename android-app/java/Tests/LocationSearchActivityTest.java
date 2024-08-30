package com.example.samplewebapp;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLocationManager;
import org.robolectric.shadows.ShadowLooper;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class LocationSearchActivityTest {

    private LocationSearchActivity locationSearchActivity;
    private LocationManager locationManager;
    private ShadowLocationManager shadowLocationManager;

    @Before
    public void setUp() {
        ActivityScenario<LocationSearchActivity> scenario = ActivityScenario.launch(LocationSearchActivity.class);
        scenario.onActivity(activity -> locationSearchActivity = activity);

        locationManager = (LocationManager) locationSearchActivity.getSystemService(LocationSearchActivity.LOCATION_SERVICE);
        shadowLocationManager = shadowOf(locationManager);
    }

    @Test
    public void testFindNearestDonationFacility_withValidResponse() {
        // Set up a mock location
        Location mockLocation = new Location(LocationManager.GPS_PROVIDER);
        mockLocation.setLatitude(37.7749); // Example latitude
        mockLocation.setLongitude(-122.4194); // Example longitude

        // Mock the HTTP connection and response
        try {
            URL mockUrl = mock(URL.class);
            HttpURLConnection mockConnection = mock(HttpURLConnection.class);
            Scanner mockScanner = mock(Scanner.class);

            // Mock a successful JSON response from the server
            JSONObject mockFacility = new JSONObject();
            mockFacility.put("name", "Sample Donation Facility");
            mockFacility.put("distance", 1.5);

            JSONArray facilitiesArray = new JSONArray();
            facilitiesArray.put(mockFacility);

            JSONObject mockResponse = new JSONObject();
            mockResponse.put("facilities", facilitiesArray);

            when(mockUrl.openConnection()).thenReturn(mockConnection);
            when(mockConnection.getInputStream()).thenReturn(mockScanner);
            when(mockScanner.nextLine()).thenReturn(mockResponse.toString());

            // Trigger location change manually
            shadowLocationManager.simulateLocation(mockLocation);

            // Allow the background task to complete
            ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

            // Verify the result is displayed correctly
            TextView resultTextView = locationSearchActivity.findViewById(R.id.resultTextView);
            assertEquals("Nearest Facility: Sample Donation Facility\nDistance: 1.5 km", resultTextView.getText().toString());
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testFindNearestDonationFacility_withNoFacilitiesFound() {
        // Set up a mock location
        Location mockLocation = new Location(LocationManager.GPS_PROVIDER);
        mockLocation.setLatitude(37.7749); // Example latitude
        mockLocation.setLongitude(-122.4194); // Example longitude

        // Mock the HTTP connection and response
        try {
            URL mockUrl = mock(URL.class);
            HttpURLConnection mockConnection = mock(HttpURLConnection.class);
            Scanner mockScanner = mock(Scanner.class);

            // Mock an empty JSON response from the server
            JSONObject mockResponse = new JSONObject();
            mockResponse.put("facilities", new JSONArray());

            when(mockUrl.openConnection()).thenReturn(mockConnection);
            when(mockConnection.getInputStream()).thenReturn(mockScanner);
            when(mockScanner.nextLine()).thenReturn(mockResponse.toString());

            // Trigger location change manually
            shadowLocationManager.simulateLocation(mockLocation);

            // Allow the background task to complete
            ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

            // Verify the result is displayed correctly
            TextView resultTextView = locationSearchActivity.findViewById(R.id.resultTextView);
            assertEquals("No facilities found nearby.", resultTextView.getText().toString());
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testOnBackButtonClick() {
        // Simulate back button click
        locationSearchActivity.onBackButtonClick(new View(locationSearchActivity));

        // Assert that the activity is finished
        assertTrue(locationSearchActivity.isFinishing());
    }

    private ShadowLocationManager shadowOf(LocationManager locationManager) {
        return (ShadowLocationManager) org.robolectric.Shadows.shadowOf(locationManager);
    }
}
