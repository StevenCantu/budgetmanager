package com.example.thegreatbudget.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;

import com.example.thegreatbudget.R;
import com.example.thegreatbudget.fragments.DatePickerFragment;
import com.example.thegreatbudget.util.Common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "SettingsActivity";

    private Switch mDarkModeSwitch;
    private TextView mDayText;
    private int mResetDay;

    private final int chosenDay = 31;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Common.themeSetterNoActionBar(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupActionBar();

        loadResetDay();
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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        updateResetDayText(dayOfMonth);
        saveResetDay(dayOfMonth);
    }

    private void setupActionBar() {
        setTitle("Settings");
        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        calendar = Calendar.getInstance();
        Log.d(TAG, "initViews: " + SimpleDateFormat.getDateInstance().format(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_MONTH, chosenDay);

        mDayText = findViewById(R.id.calendar_reset_day);
        updateResetDayText(mResetDay);
        CardView cardView = findViewById(R.id.calendar_card_view);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getSupportFragmentManager(), "date picker");
//                calendarTest();
            }
        });

        CardView statementCardView = findViewById(R.id.statement_card_view);
        statementCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, StatementActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateResetDayText(int day) {
        String stringDay = String.format(Locale.getDefault(),
                "Budget will reset every %d of the month.\n" +
                "Note: Changing date may reset your current information.",
                day);

        mDayText.setText(stringDay);
    }

    private void loadResetDay() {
        SharedPreferences sp = getSharedPreferences(Common.SHARED_PREFERENCES, MODE_PRIVATE);
        mResetDay = sp.getInt(Common.RESET_DAY_EXTRA, Common.RESET_DAY_DEFAULT);
    }

    private void saveResetDay(int day) {
        SharedPreferences sp = getSharedPreferences(Common.SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Common.RESET_DAY_EXTRA, day);
        editor.putInt(Common.CALCULATED_RESET_DAY_EXTRA, day);
        editor.apply();
    }

    private void goHome() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

