package com.example.samplewebapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SearchActivity extends AppCompatActivity {
    protected String message;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

    }

    public void onConfirmButtonClick (View v) {
        TextView tv = findViewById(R.id.resultTextView);
        EditText inputEditText = findViewById(R.id.inputEditText);
        String inputString = inputEditText.getText().toString();
        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                        try {
                            URL url = new URL("http://10.0.2.2:3008/search?name=" + inputString);
                            Log.v("start", "http://10.0.2.2:3008/search?name=" + inputString);
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

            tv.setText(message);
        }
        catch (Exception e) {
            // uh oh
            e.printStackTrace();
            tv.setText(e.toString());
        }


    }

    public void onBackSearchButtonClick (View v){
        finish();
    }

}
