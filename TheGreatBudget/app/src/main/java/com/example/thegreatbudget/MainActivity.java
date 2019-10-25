package com.example.thegreatbudget;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thegreatbudget.adapters.SectionPageAdapter;
import com.example.thegreatbudget.fragments.ExpenseFragment;
import com.example.thegreatbudget.model.Category;
import com.example.thegreatbudget.model.Expenses;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    //tabs
    public static final int HOUSING = 0;
    public static final int INSURANCE = 1;
    public static final int PERSONAL = 2;
    public static final int WANTS = 3;
    public static final int MISC = 4;
    //bundles
    public static final String INCOME_EXTRA = "thegreatbudget.main.income.extra.intent";
    //shared preferences
    public static final String SHARED_PREFERENCES = "thegreatbudget.shared.preferences";
    public static final String TOTAL_EXPENSES = "thegreatbudget.total.expenses";
    // other activity
    public static final int INCOME_ACTIVITY_REQUEST = 21;

    private static final String TAG = "MainActivity";

    private SectionPageAdapter mSectionPageAdapter;
    private ViewPager mViewPager;
    private double mAfterExpenses, mHousingExpenses, mPersonalExpenses, mInsuranceExpenses,
            mWantsExpenses, mIncome, mTotalExpenses;
    private TextView mAvailableText;
    private ExpenseFragment mHousing2, mPersonal2, mInsurance2, mWants2;
    private ExpenseFragment mOther;

    private Spinner spinnerTotals;
    private ImageButton mEditIncomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadData();

        initSpinner();

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
                // TODO: 8/29/2019 for now show income
                Log.d(TAG, "onActivityResult: " + income);
            }
            // get data from data intent
        }
    }

    private void initSpinner() {
        spinnerTotals = findViewById(R.id.spinner_totals);
        mEditIncomeButton = findViewById(R.id.edit_income_button);
        // Spinner Drop down elements
        final List<String> categories = new ArrayList<>();
        categories.add("Income");
        categories.add("Expenses");
        categories.add("After Expenses");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerTotals.setAdapter(dataAdapter);

        spinnerTotals.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //switch case for each category in the spinner
                //ex: income would show total income and edit will be enabled
                //Only enable edit for income

                switch(categories.get(position)) {
                    case "Income":
                        mEditIncomeButton.setVisibility(View.VISIBLE);
                        updateAvailable(mIncome);
                        break;
                    case "Expenses":
                        mEditIncomeButton.setVisibility(View.INVISIBLE);
                        updateAvailable(mTotalExpenses);
                        break;
                    case "After Expenses":
                        mEditIncomeButton.setVisibility(View.INVISIBLE);
                        updateAvailable(mAfterExpenses);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * set up icons for each tab
     *
     * @param tabLayout layout containing tabs
     */
    private void setupIcons(TabLayout tabLayout) {
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
     *
     * @param viewPager contains tab layout
     */
    private void setupViewPager(ViewPager viewPager) {
        mSectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        mHousing2 = new ExpenseFragment();
        mInsurance2 = new ExpenseFragment();
        mPersonal2 = new ExpenseFragment();
        mWants2 = new ExpenseFragment();
        mOther = new ExpenseFragment();

        mSectionPageAdapter.addFragment(mHousing2, "Housing");
        mSectionPageAdapter.addFragment(mInsurance2, "Insurance");
        mSectionPageAdapter.addFragment(mPersonal2, "Personal");
        mSectionPageAdapter.addFragment(mWants2, "Wants");
        mSectionPageAdapter.addFragment(mOther, "Other");
        viewPager.setAdapter(mSectionPageAdapter);

        initializeExpenses();

        mHousing2.setOnClickListener(expenseListener);
        mPersonal2.setOnClickListener(expenseListener);
        mInsurance2.setOnClickListener(expenseListener);
        mWants2.setOnClickListener(expenseListener);
        mOther.setOnClickListener(expenseListener);
    }

    /**
     * initialize all recycler lists with expenses
     */
    private void initializeExpenses() {
        initializeCategories(mHousing2, Category.HOUSING);
        initializeCategories(mPersonal2, Category.PERSONAL);
        initializeCategories(mInsurance2, Category.INSURANCE);
        initializeCategories(mWants2, Category.WANTS);
        initializeCategories(mOther, Category.MISC);
    }

    /**
     * initialize expense recycler
     *
     * @param fragment expense fragment instance
     * @param category Category to query
     */
    private void initializeCategories(ExpenseFragment fragment, int category) {
        Bundle bundle = new Bundle();
        bundle.putInt(ExpenseFragment.CATEGORY, category);
        fragment.setArguments(bundle);
    }

    /**
     * save the state of the app from shared preferences
     */
    private void saveData() {
        SharedPreferences sp = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
//        editor.putFloat(TOTAL_EXPENSES, mFreeMoney);
        editor.apply();
    }

    /**
     * load state of app from shared preferences
     */
    private void loadData() {
        SharedPreferences sp = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        mAfterExpenses = sp.getFloat(TOTAL_EXPENSES, 0f);
    }

    private void updateAvailable(double value) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        mAvailableText.setText(numberFormat.format(value));
    }

    /**
     * update all temporary totals for all tabs
     *
     * @param input expenses
     */
    private void updateAllExpenseTabs(float input) {
        double expenses = mHousingExpenses + mInsuranceExpenses + mPersonalExpenses + mWantsExpenses;
        mAfterExpenses = mIncome - expenses;
        updateAvailable(mAfterExpenses);
        Log.i(TAG, "updateAllExpenseTabs: " + mAfterExpenses + " expenses: " + expenses);
    }

    ExpenseFragment.OnClickListener expenseListener = new ExpenseFragment.OnClickListener() {
        @Override
        public void amountClick(Expenses expense) {
            Log.d(TAG, "amountClick: " + expense);
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

    // TODO: 10/22/2019 delete unnecessary code

    // TODO: 10/25/2019 Text in spinner matches the text view
    // TODO: 10/25/2019 only allow edit for income in spinner
    // TODO: 10/25/2019 only save income on shared preferences
    // TODO: 10/25/2019 make sure mTotal and mAfterExpenses is always up to date
}
