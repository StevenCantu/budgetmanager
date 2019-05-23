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

import com.example.thegreatbudget.adapters.SectionPageAdapter;
import com.example.thegreatbudget.fragments.Housing;
import com.example.thegreatbudget.fragments.Insurance;
import com.example.thegreatbudget.fragments.Miscellaneous;
import com.example.thegreatbudget.fragments.Personal;
import com.example.thegreatbudget.fragments.Savings;


public class MainActivity extends AppCompatActivity implements
        Personal.PersonalListener, Savings.SavingsListener, Miscellaneous.MiscListener,
        Insurance.InsuranceListener{
    //tabs
    public static final int HOUSING = 0;
    public static final int INSURANCE = 1;
    public static final int PERSONAL = 2;
    public static final int SAVINGS = 3;
    public static final int MISC = 4;
    //bundles
    public static final String HOUSING_TITLES = "thegreatbudget.main.housing.titles";
    public static final String HOUSING_TITLE_SP = "thegreatbudget.main.housing.title.sp";
    public static final String HOUSING_EXPENSE_SP = "thegreatbudget.main.housing.expense.sp";
    //shared preferences
    public static final String SHARED_PREFERENCES = "thegreatbudget.shared.preferences";
    public static final String TOTAL_EXPENSES = "thegreatbudget.total.expenses";
    public static final String HOUSING_LIST_TITLE = "thegreatbudget.recycler.housing.list.title";
    public static final String HOUSING_LIST_EXPENSE = "thegreatbudget.recycler.housing.list.expense";
    public static final String PERSONAL_LIST_TITLE = "thegreatbudget.recycler.personal.list.title";
    public static final String PERSONAL_LIST_EXPENSE = "thegreatbudget.recycler.personal.list.expense";

    private static final String TAG = "MainActivity";

    private SectionPageAdapter mSectionPageAdapter;
    private ViewPager mViewPager;
    private float mTotalExpenses;
    private Housing mHousing, mPersonal;
    private Insurance mInsurance;
    //private Personal mPersonal;
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
                R.drawable.ic_home_black_24dp,
                R.drawable.ic_account_balance_wallet_24dp,
                R.drawable.ic_attach_money_24dp,
                R.drawable.ic_star_black_24dp
        };

        tabLayout.getTabAt(HOUSING).setIcon(tabIcons[HOUSING]);
        tabLayout.getTabAt(INSURANCE).setIcon(tabIcons[INSURANCE]);
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
        mInsurance = new Insurance();
        mPersonal = new Housing();//new Personal();
        mSavings = new Savings();
        mMisc = new Miscellaneous();
        mSectionPageAdapter.addFragment(mHousing, "Housing");
        mSectionPageAdapter.addFragment(mInsurance, "Insurance");
        mSectionPageAdapter.addFragment(mPersonal, "Personal");
        mSectionPageAdapter.addFragment(mSavings, "Savings");
        mSectionPageAdapter.addFragment(mMisc, "Misc.");
        viewPager.setAdapter(mSectionPageAdapter);

        initializeExpenses();

        mHousing.setHousingListener(housingListener);
        mPersonal.setHousingListener(housingListener);
    }

    /**
     * initialize all recycler lists with expenses
     */
    private void initializeExpenses(){
        String[] housingExpenses = {
                "Rent/Mortgage",
                "Electricity",
                "Gas",
                "Internet/Cable",
                "Water/Sewage"
        };
        String[] personalExpenses = {
                "Car loan",
                "Student loan"
        };

        initializeList(housingExpenses, mHousing, HOUSING_LIST_TITLE, HOUSING_LIST_EXPENSE);
        initializeList(personalExpenses, mPersonal, PERSONAL_LIST_TITLE, PERSONAL_LIST_EXPENSE);
    }

    /**
     * initialize single recycler list
     * @param s list of expenses
     * @param housing fragment of recycler
     */
    private void initializeList(final String[] s, Housing housing, final String titleSP, final String expenseSP){
        Bundle bundle = new Bundle();
        bundle.putStringArray(HOUSING_TITLES, s);
        bundle.putString(HOUSING_TITLE_SP, titleSP);
        bundle.putString(HOUSING_EXPENSE_SP, expenseSP);
        housing.setArguments(bundle);
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
//        mHousing.updateHousing(input);
//        mPersonal.updatePersonal(input);
//        mSavings.updateSavings(input);
//        mMisc.updateMisc(input);
//        mInsurance.updateInsurance(input);
    }

    Housing.HousingListener housingListener = new Housing.HousingListener() {
        @Override
        public void onHousingSent(float input) {
            Log.i(TAG, "onHousingSent: " + input);
        }
    };

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

    @Override
    public void onInsuranceSent(float input) {
        updateAllExpenseTabs(input);
    }
}
