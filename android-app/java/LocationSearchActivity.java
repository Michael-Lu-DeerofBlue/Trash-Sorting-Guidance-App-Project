package com.example.samplewebapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LocationSearchActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;
    protected String message;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                findNearestDonationFacility(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(LocationSearchActivity.this, "Please enable GPS", Toast.LENGTH_SHORT).show();
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    private void findNearestDonationFacility(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try {
                    URL url = new URL("http://10.0.2.2:3008/findNearestFacility?lat=" + latitude + "&lng=" + longitude);
                    Log.v("LocationSearch", "http://10.0.2.2:3008/findNearestFacility?lat=" + latitude + "&lng=" + longitude);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.connect();

                    Scanner in = new Scanner(url.openStream());
                    String response = in.nextLine();

                    JSONObject jo = new JSONObject(response);
                    JSONArray facilities = jo.getJSONArray("facilities");
                    if (facilities.length() > 0) {
                        JSONObject nearestFacility = facilities.getJSONObject(0);
                        message = "Nearest Facility: " + nearestFacility.getString("name") +
                                  "\nDistance: " + nearestFacility.getDouble("distance") + " km";
                    } else {
                        message = "No facilities found nearby.";
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    message = "Error: " + e.toString();
                }
            });

            executor.awaitTermination(2, TimeUnit.SECONDS);

            runOnUiThread(() -> {
                TextView resultTextView = findViewById(R.id.resultTextView);
                resultTextView.setText(message);
            });
        } catch (Exception e) {
            e.printStackTrace();
            TextView resultTextView = findViewById(R.id.resultTextView);
            resultTextView.setText("Error: " + e.toString());
        }
    }

    public void onBackButtonClick(View v) {
        finish();
    }
}
