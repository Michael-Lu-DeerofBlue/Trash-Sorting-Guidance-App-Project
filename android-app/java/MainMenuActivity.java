package com.example.samplewebapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {

    public  static final int SearchActivity_ID = 1;
    public  static final int FeedbackActivity_ID = 2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);
    }

    public void onSearchButtonClick(View v) {
        Intent i = new Intent(this, SearchActivity.class);
        startActivityForResult(i, SearchActivity_ID);
    }

    public void onFeedbackButtonClick(View v) {
        Intent i = new Intent(this, FeedbackActivity.class);
        startActivityForResult(i, FeedbackActivity_ID);
    }

}
