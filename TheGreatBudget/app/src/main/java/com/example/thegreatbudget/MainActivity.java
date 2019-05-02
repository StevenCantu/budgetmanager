package com.example.thegreatbudget;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {

    public static final int HOUSING = 0;
    public static final int PERSONAL = 1;
    public static final int SAVINGS = 2;
    public static final int MISC = 3;

    private static final String TAG = "MainActivity";

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setupIcons(tabLayout);
    }

    private void setupIcons(TabLayout tabLayout){
        int[] tabIcons = {
                R.drawable.ic_home_black_24dp,
                R.drawable.ic_account_balance_wallet_24dp,
                R.drawable.ic_attach_money_24dp,
                R.drawable.ic_star_black_24dp
        };

        tabLayout.getTabAt(HOUSING).setIcon(tabIcons[HOUSING]);
        tabLayout.getTabAt(PERSONAL).setIcon(tabIcons[PERSONAL]);
        tabLayout.getTabAt(SAVINGS).setIcon(tabIcons[SAVINGS]);
        tabLayout.getTabAt(MISC).setIcon(tabIcons[MISC]);

    }

    private void setupViewPager(ViewPager viewPager){
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Housing(), "housing");
        adapter.addFragment(new Personal(), "Personal");
        adapter.addFragment(new Savings(), "savings");
        adapter.addFragment(new Miscellaneous(), "misc.");
        viewPager.setAdapter(adapter);
    }

}
