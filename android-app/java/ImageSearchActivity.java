package com.example.samplewebapp;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ImageSearchActivity extends AppCompatActivity {

    private String imageUriString;
    private TextView resultTextView;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_search);

        resultTextView = findViewById(R.id.resultTextView);
        webView = findViewById(R.id.webView);

        imageUriString = getIntent().getStringExtra("imageUri");
        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            processImageWithMl5(imageUri);
        } else {
            resultTextView.setText("No image provided.");
        }
    }

    private void processImageWithMl5(Uri imageUri) {
        // Load the image in a WebView and use ml5.js to process the image for object detection
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Inject ml5.js code here to detect objects and call handleDetectedObjects() with results
                String ml5Script = "ml5.imageClassifier('MobileNet').then(classifier => {" +
                        "const img = document.getElementById('targetImage');" +
                        "classifier.predict(img, (err, results) => {" +
                        "if (err) { console.error(err); return; }" +
                        "Android.handleDetectedObjects(JSON.stringify(results));" +
                        "});" +
                        "});";
                webView.evaluateJavascript(ml5Script, null);
            }
        });

        // Load the image into the WebView for processing
        webView.loadData("<html><body><img id='targetImage' src='" + imageUri.toString() + "' /></body></html>", "text/html", "UTF-8");
    }

    @android.webkit.JavascriptInterface
    public void handleDetectedObjects(String jsonResults) {
        Log.v("DetectedObjects", jsonResults);

        // Assuming that jsonResults is a JSON array with detected object names
        try {
            List<String> detectedObjects = parseDetectedObjects(jsonResults);
            searchObjectsInDatabase(detectedObjects);
        } catch (Exception e) {
            e.printStackTrace();
            resultTextView.setText("Error processing image: " + e.getMessage());
        }
    }


private List<String> parseDetectedObjects(String jsonResults) {
    List<String> detectedObjects = new ArrayList<>();
    try {
        // Parse the JSON array from the ml5.js result
        JSONArray jsonArray = new JSONArray(jsonResults);

        // Loop through each object in the array
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);

            // Extract the label (object name)
            String label = object.getString("label");

            // Add the label to the list of detected objects
            detectedObjects.add(label);
        }
    } catch (Exception e) {
        e.printStackTrace();
        // Handle potential parsing errors
    }
    return detectedObjects;
}

    private void searchObjectsInDatabase(List<String> objects) {
        // Use the same HTTP-based inquiry method as in SearchActivity to query the online database
        new Thread(() -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            for (String object : objects) {
                executor.execute(() -> {
                    try {
                        URL url = new URL("http://10.0.2.2:3008/search?name=" + object);
                        Log.v("start", "http://10.0.2.2:3008/search?name=" + object);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.connect();

                        Scanner in = new Scanner(url.openStream());
                        String response = in.nextLine();

                        JSONObject jo = new JSONObject(response);
                        String message = jo.getString("message");

                        runOnUiThread(() -> {
                            String currentText = resultTextView.getText().toString();
                            resultTextView.setText(currentText + "\n" + object + " -> " + message);
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> resultTextView.setText("Error: " + e.toString()));
                    }
                });
            }

            // Wait for all tasks to complete
            try {
                executor.awaitTermination(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
