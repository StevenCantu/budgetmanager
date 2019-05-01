package com.example.thegreatbudget;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.fragment_navigation);
        nav.setOnNavigationItemSelectedListener(navSwitch);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navSwitch =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()) {
                        case R.id.housing:
                            selectedFragment = new HousingFragment();
                            break;
                        case R.id.personal:
                            selectedFragment = new PersonalFragment();
                            break;
                        case R.id.Savings:
                            selectedFragment = new SavingsFragment();
                            break;
                        case R.id.Other:
                            selectedFragment = new OtherFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_information,selectedFragment).commit();

                    return true;
                }
            };

    // TODO: TODOS done
}
