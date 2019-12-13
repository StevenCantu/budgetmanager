package com.example.thegreatbudget.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.thegreatbudget.R;
import com.example.thegreatbudget.util.Common;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";

    private Switch mDarkModeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Common.themeSetter(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDarkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "onCheckedChanged: " + isChecked);
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                recreate();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            goHome();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        goHome();
        super.onBackPressed();
    }

    private void initViews() {
        mDarkModeSwitch = findViewById(R.id.dark_mode_switch);
        TextView darkModeText = findViewById(R.id.dark_mode_text);
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            Log.d(TAG, "onCreate: night mode ON");
            mDarkModeSwitch.setChecked(true);
            darkModeText.setText("Disable Dark Mode");
        } else {
            darkModeText.setText("Enable Dark Mode");
        }
    }

    private void goHome() {
        Log.d(TAG, "goHome: ");
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
    }
}

