package com.example.thegreatbudget.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.thegreatbudget.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");
    }
}

// TODO: 12/8/2019 make model class 
// TODO: 12/8/2019 save in sharedprefs 
