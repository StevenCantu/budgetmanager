package com.example.thegreatbudget;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.thegreatbudget.activities.MainActivity;

public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 750;
    public static boolean isLaunched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final Intent intent = new Intent(SplashScreen.this, MainActivity.class);

        if (isLaunched) {
            startActivity(intent);
            finish();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                    finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }

    }
}
