package com.example.samplewebapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FeedbackActivity extends AppCompatActivity {
    protected String message;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
    }

    public void onSubmitFeedbackButtonClick (View v) {
        EditText nameEditText = findViewById(R.id.objectNameEditText);
        String trashName = nameEditText.getText().toString();
        EditText categoryEditText = findViewById(R.id.categoryEditText);
        String category = categoryEditText.getText().toString();
        EditText feedbackEditText = findViewById(R.id.feedbackEditText);
        String feedback = feedbackEditText.getText().toString();

        if (!trashName.isEmpty() || !category.isEmpty()){
            if (trashName.isEmpty() || category.isEmpty() || feedback.isEmpty()){
                Toast.makeText(FeedbackActivity.this, "Please fillout all the fields", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (trashName.isEmpty() && category.isEmpty() && feedback.isEmpty()){
            Toast.makeText(FeedbackActivity.this, "Please fillout at least one field", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                        try {
                            URL url = new URL("http://10.0.2.2:3008/userFilesRequest?name=" + trashName + "&category=" + category + "&feedback=" + feedback);
                            Log.v("start", "http://10.0.2.2:3008/userFilesRequest?name=" + trashName + "?category=" + category + "?feedback=" + feedback);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.connect();

                            Scanner in = new Scanner(url.openStream());
                            String response = in.nextLine();

                            JSONObject jo = new JSONObject(response);
                            message = jo.getString("message");

                        } catch (Exception e) {
                            e.printStackTrace();
                            message = e.toString();
                        }
                    }
            );

            // this waits for up to 2 seconds
            // it's a bit of a hack because it's not truly asynchronous
            // but it should be okay for our purposes (and is a lot easier)
            executor.awaitTermination(1, TimeUnit.SECONDS);
            Toast.makeText(FeedbackActivity.this, message, Toast.LENGTH_SHORT).show();

        }
        catch (Exception e) {
            // uh oh
            e.printStackTrace();
            Toast.makeText(FeedbackActivity.this, message, Toast.LENGTH_SHORT).show();
        }


    }

    public void onBackFeedbackButtonClick (View v){
        finish();
    }

}
