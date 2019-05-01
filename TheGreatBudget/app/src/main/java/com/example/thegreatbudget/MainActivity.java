package com.example.thegreatbudget;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Log.i(TAG, "onCreate: it works");
    }

    // TODO: 4/27/2019 github branching.
    // TODO: 4/27/2019 android studio fragments
    // TODO: 4/27/2019 tablayout android studio
}
