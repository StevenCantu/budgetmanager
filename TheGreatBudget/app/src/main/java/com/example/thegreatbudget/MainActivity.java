package com.example.thegreatbudget;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity implements Housing.HousingListener,
        Personal.PersonalListener, Savings.SavingsListener, Miscellaneous.MiscListener {

    public static final int HOUSING = 0;
    public static final int PERSONAL = 1;
    public static final int SAVINGS = 2;
    public static final int MISC = 3;
    public static final String SHARED_PREFERENCES = "thegreatbudget.shared.preferences";
    public static final String TOTAL_EXPENSES = "thegreatbudget.total.expenses";

    private static final String TAG = "MainActivity";

    private SectionPageAdapter mSectionPageAdapter;
    private ViewPager mViewPager;
    private float mTotalExpenses;
    private Housing mHousing;
    private Personal mPersonal;
    private Savings mSavings;
    private Miscellaneous mMisc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadData();

        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);
        setupIcons(tabLayout);
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveData();
    }

    /**
     * set up icons for each tab
     * @param tabLayout layout containing tabs
     */
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

    /**
     * set up tabs in ViewPager
     * @param viewPager contains tab layout
     */
    private void setupViewPager(ViewPager viewPager){
        mSectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        mHousing = new Housing();
        mPersonal = new Personal();
        mSavings = new Savings();
        mMisc = new Miscellaneous();
        mSectionPageAdapter.addFragment(mHousing, "housing");
        mSectionPageAdapter.addFragment(mPersonal, "Personal");
        mSectionPageAdapter.addFragment(mSavings, "savings");
        mSectionPageAdapter.addFragment(mMisc, "misc.");
        viewPager.setAdapter(mSectionPageAdapter);
    }

    /**
     * save the state of the app from shared preferences
     */
    private void saveData(){
        SharedPreferences sp = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(TOTAL_EXPENSES, mTotalExpenses);
        editor.apply();
    }

    /**
     * load state of app from shared preferences
     */
    private void loadData(){
        SharedPreferences sp = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        mTotalExpenses = sp.getFloat(TOTAL_EXPENSES, 0f);
    }

    /**
     * update all temporary totals for all tabs
     * @param input expenses
     */
    private void updateAllExpenseTabs(Float input){
        mHousing.updateHousing(input);
        mPersonal.updatePersonal(input);
        mSavings.updateSavings(input);
        mMisc.updateMisc(input);
    }

    @Override
    public void onHousingSent(float input) {
        updateAllExpenseTabs(input);
    }

    @Override
    public void onMiscSent(float input) {
        updateAllExpenseTabs(input);
    }

    @Override
    public void onPersonalSent(float input) {
        updateAllExpenseTabs(input);
    }

    @Override
    public void onSavingsSent(float input) {
        updateAllExpenseTabs(input);
    }
}
