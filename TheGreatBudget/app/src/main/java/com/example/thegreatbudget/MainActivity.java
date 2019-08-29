package com.example.thegreatbudget;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.thegreatbudget.adapters.SectionPageAdapter;
import com.example.thegreatbudget.fragments.Housing;
import com.example.thegreatbudget.fragments.Insurance;
import com.example.thegreatbudget.fragments.Miscellaneous;
import com.example.thegreatbudget.fragments.Personal;
import com.example.thegreatbudget.fragments.Savings;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements Miscellaneous.MiscListener{
    //tabs
    public static final int HOUSING = 0;
    public static final int INSURANCE = 1;
    public static final int PERSONAL = 2;
    public static final int WANTS = 3;
    public static final int MISC = 4;
    //bundles
    public static final String HOUSING_TITLES = "thegreatbudget.main.housing.titles";
    public static final String HOUSING_TITLE_SP = "thegreatbudget.main.housing.title.sp";
    public static final String HOUSING_EXPENSE_SP = "thegreatbudget.main.housing.expense.sp";
    public static final String INCOME_EXTRA = "thegreatbudget.main.income.extra.intent";
    //shared preferences
    public static final String SHARED_PREFERENCES = "thegreatbudget.shared.preferences";
    public static final String TOTAL_EXPENSES = "thegreatbudget.total.expenses";
    public static final String HOUSING_LIST_TITLE = "thegreatbudget.recycler.housing.list.title";
    public static final String HOUSING_LIST_EXPENSE = "thegreatbudget.recycler.housing.list.expense";
    public static final String PERSONAL_LIST_TITLE = "thegreatbudget.recycler.personal.list.title";
    public static final String PERSONAL_LIST_EXPENSE = "thegreatbudget.recycler.personal.list.expense";
    public static final String INSURANCE_LIST_TITLE = "thegreatbudget.recycler.insurance.list.title";
    public static final String INSURANCE_LIST_EXPENSES = "thegreatbudget.recycler.insurance.list.expense";
    public static final String WANTS_LIST_TITLE = "thegreatbudget.recycler.wants.list.title";
    public static final String WANTS_LIST_EXPENSES = "thegreatbudget.recycler.wants.list.expense";
    // other activity
    public static final int INCOME_ACTIVITY_REQUEST = 21;

    private static final String TAG = "MainActivity";

    private SectionPageAdapter mSectionPageAdapter;
    private ViewPager mViewPager;
    private double mAfterExpenses, mHousingExpenses, mPersonalExpenses, mInsuranceExpenses,
            mWantsExpenses, mIncome;
    private TextView mAvailableText;
    private Housing mHousing, mPersonal, mInsurance, mWants;
    private Miscellaneous mMisc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadData();

        mAvailableText = findViewById(R.id.main_income);
        mAvailableText.setOnClickListener(incomeClickListener);

        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

        mIncome = 0f;
        updateAvailable(mAfterExpenses);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);
        setupIcons(tabLayout);
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INCOME_ACTIVITY_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                double income = data.getDoubleExtra(IncomeActivity.EXTRA_INCOME, 0f);
                mIncome = income;
                updateAvailable(mIncome);
                // TODO: 8/29/2019 for now 
                Log.d(TAG, "onActivityResult: " + income);
            }
            // get data from data intent
        }
    }

    /**
     * set up icons for each tab
     * @param tabLayout layout containing tabs
     */
    private void setupIcons(TabLayout tabLayout){
        int[] tabIcons = {
                R.drawable.housing_rent,
                R.drawable.insurance_dark,
                R.drawable.personal,
                R.drawable.wants,
                R.drawable.other
        };

        tabLayout.getTabAt(HOUSING).setIcon(tabIcons[HOUSING]);
        tabLayout.getTabAt(INSURANCE).setIcon(tabIcons[INSURANCE]);
        tabLayout.getTabAt(PERSONAL).setIcon(tabIcons[PERSONAL]);
        tabLayout.getTabAt(WANTS).setIcon(tabIcons[WANTS]);
        tabLayout.getTabAt(MISC).setIcon(tabIcons[MISC]);

    }

    /**
     * set up tabs in ViewPager
     * @param viewPager contains tab layout
     */
    private void setupViewPager(ViewPager viewPager){
        mSectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        mHousing = new Housing();
        mInsurance = new Housing();
        mPersonal = new Housing();//new Personal();
        mWants = new Housing();
        mMisc = new Miscellaneous();
        mSectionPageAdapter.addFragment(mHousing, "Housing");
        mSectionPageAdapter.addFragment(mInsurance, "Insurance");
        mSectionPageAdapter.addFragment(mPersonal, "Personal");
        mSectionPageAdapter.addFragment(mWants, "Wants");
        mSectionPageAdapter.addFragment(mMisc, "Other");
        viewPager.setAdapter(mSectionPageAdapter);

        initializeExpenses();

        mHousing.setHousingListener(housingListener);
        mPersonal.setHousingListener(personalListener);
        mInsurance.setHousingListener(insuranceListener);
        mWants.setHousingListener(wantsListener);
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
                "Groceries",
                "Toiletries",
                "Gasoline/Transportation",
                "Cell Phone"
        };
        String[] insuranceExpenses = {
                "Auto",
                "Health",
                "Life",
                "Renters/Home Owners"
        };
        String[] wantsExpenses = {
                "Clothes",
                "Dining Out",
                "Events",
                "Gym/Clubs",
                "Travel",
                "Home Decor",
                "Streaming Services"
        };
        initializeList(housingExpenses, mHousing, HOUSING_LIST_TITLE, HOUSING_LIST_EXPENSE);
        initializeList(personalExpenses, mPersonal, PERSONAL_LIST_TITLE, PERSONAL_LIST_EXPENSE);
        initializeList(insuranceExpenses, mInsurance, INSURANCE_LIST_TITLE, INSURANCE_LIST_EXPENSES);
        initializeList(wantsExpenses, mWants, WANTS_LIST_TITLE, WANTS_LIST_EXPENSES);
    }

    /**
     * initialize single recycler list
     * @param s list of expenses
     * @param housing fragment of recycler
     */
    private void initializeList(final String[] s, Housing housing, final String titleSP, final String expenseSP){
        Arrays.sort(s);

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
//        editor.putFloat(TOTAL_EXPENSES, mFreeMoney);
        editor.apply();
    }

    /**
     * load state of app from shared preferences
     */
    private void loadData(){
        SharedPreferences sp = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        mAfterExpenses = sp.getFloat(TOTAL_EXPENSES, 0f);
    }

    private void updateAvailable(double value){
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        mAvailableText.setText(numberFormat.format(value));
    }

    /**
     * update all temporary totals for all tabs
     * @param input expenses
     */
    private void updateAllExpenseTabs(float input){
        double expenses = mHousingExpenses + mInsuranceExpenses + mPersonalExpenses + mWantsExpenses;
        mAfterExpenses = mIncome - expenses;
        updateAvailable(mAfterExpenses);
        Log.i(TAG, "updateAllExpenseTabs: " + mAfterExpenses + " expenses: " + expenses);
    }

    Housing.HousingListener housingListener = new Housing.HousingListener() {
        @Override
        public void onHousingSent(float input) {
            Log.i(TAG, "onHousingSent H: " + input);
            mHousingExpenses = input;
            updateAllExpenseTabs(input);
        }
    };

    Housing.HousingListener personalListener = new Housing.HousingListener() {
        @Override
        public void onHousingSent(float input) {
            Log.i(TAG, "onHousingSent P: " + input);
            mPersonalExpenses = input;
            updateAllExpenseTabs(input);
        }
    };

    Housing.HousingListener insuranceListener = new Housing.HousingListener() {
        @Override
        public void onHousingSent(float input) {
            Log.i(TAG, "onHousingSent I: " + input);
            mInsuranceExpenses = input;
            updateAllExpenseTabs(input);
        }
    };

    Housing.HousingListener wantsListener = new Housing.HousingListener() {
        @Override
        public void onHousingSent(float input) {
            Log.i(TAG, "onHousingSent W: " + input);
            mWantsExpenses = input;
            updateAllExpenseTabs(input);
        }
    };

    View.OnClickListener incomeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), IncomeActivity.class);
            intent.putExtra(INCOME_EXTRA, mIncome);
//            startActivity(intent);
            startActivityForResult(intent, INCOME_ACTIVITY_REQUEST);
        }
    };

    @Override
    public void onMiscSent(float input) {
        updateAllExpenseTabs(input);
    }

    // TODO: 8/29/2019 Add edit income for user. Prompt user to click
    // TODO: 8/29/2019 on saved: change from put float to put string
    // TODO: 8/29/2019 make bottom sheet for income, available and expenses 

}
