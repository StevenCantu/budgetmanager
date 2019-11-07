package com.example.thegreatbudget;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.thegreatbudget.adapters.SectionPageAdapter;
import com.example.thegreatbudget.database.BudgetDbHelper;
import com.example.thegreatbudget.fragments.ExpenseFragment;
import com.example.thegreatbudget.model.Category;
import com.example.thegreatbudget.model.Expenses;
import com.example.thegreatbudget.model.History;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    //tabs
    public static final int HOUSING = 0;
    public static final int INSURANCE = 1;
    public static final int PERSONAL = 2;
    public static final int WANTS = 3;
    public static final int MISC = 4;
    //spinner constants
    public static final String INCOME = "Income";
    public static final String EXPENSES = "Expenses";
    public static final String AFTER_EXPENSES = "After Expenses";

    //bundles
    public static final String INCOME_EXTRA = "thegreatbudget.main.income.extra.intent";
    //shared preferences
    public static final String SHARED_PREFERENCES = "thegreatbudget.shared.preferences";
    public static final String INCOME_SHARED_PREFS = "thegreatbudget.income.shared.prefs";
    // other activity
    public static final int INCOME_ACTIVITY_REQUEST = 21;

    private static final String TAG = "MainActivity";

    private double mAfterExpenses, mIncome, mTotalExpenses;
    private TextView mCurrencyText;
    private ExpenseFragment mHousing, mPersonal, mInsurance, mWants;
    private ExpenseFragment mOther;
    private String mCurrentSpinnerItem = INCOME;
    private ImageButton mEditIncomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadData();

        Gson gson = new Gson();
        History history = new History();
        history.addItem(12);
        history.addItem(1);
        history.addItem(2);
        String obj = gson.toJson(history);
        Log.d(TAG, "onCreate: " + history.toString());
        Log.d(TAG, "onCreate: " + obj);
        History h2 = gson.fromJson(obj, History.class);
        Log.d(TAG, "onCreate: " + h2.toString());

        initSpinner();
        mEditIncomeButton.setOnClickListener(incomeClickListener);
        mTotalExpenses = BudgetDbHelper.getInstance(this).totalExpenses();
        mAfterExpenses = mIncome - mTotalExpenses;

        mCurrencyText = findViewById(R.id.main_income);
        updateCurrencyText();

        ViewPager viewPager = findViewById(R.id.container);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
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
                mIncome = data.getDoubleExtra(IncomeActivity.EXTRA_INCOME, 0f);
                mAfterExpenses = mIncome - mTotalExpenses;
                updateCurrencyText();
            }
        }
    }

    private void initSpinner() {
        //bottom menu
        Spinner spinnerTotals = findViewById(R.id.spinner_totals);
        mEditIncomeButton = findViewById(R.id.edit_income_button);
        // Spinner Drop down elements
        final List<String> categories = new ArrayList<>();
        categories.add(INCOME);
        categories.add(EXPENSES);
        categories.add(AFTER_EXPENSES);
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerTotals.setAdapter(dataAdapter);

        spinnerTotals.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch(categories.get(position)) {
                    case INCOME:
                        mCurrentSpinnerItem = INCOME;
                        mEditIncomeButton.setVisibility(View.VISIBLE);
                        break;
                    case EXPENSES:
                        mCurrentSpinnerItem = EXPENSES;
                        mEditIncomeButton.setVisibility(View.INVISIBLE);
                        break;
                    case AFTER_EXPENSES:
                        mCurrentSpinnerItem = AFTER_EXPENSES;
                        mEditIncomeButton.setVisibility(View.INVISIBLE);
                        break;
                }
                updateCurrencyText();
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

        Objects.requireNonNull(tabLayout.getTabAt(HOUSING)).setIcon(tabIcons[HOUSING]);
        Objects.requireNonNull(tabLayout.getTabAt(INSURANCE)).setIcon(tabIcons[INSURANCE]);
        Objects.requireNonNull(tabLayout.getTabAt(PERSONAL)).setIcon(tabIcons[PERSONAL]);
        Objects.requireNonNull(tabLayout.getTabAt(WANTS)).setIcon(tabIcons[WANTS]);
        Objects.requireNonNull(tabLayout.getTabAt(MISC)).setIcon(tabIcons[MISC]);

    }

    /**
     * set up tabs in ViewPager
     *
     * @param viewPager contains tab layout
     */
    private void setupViewPager(ViewPager viewPager) {
        SectionPageAdapter sectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        mHousing = new ExpenseFragment();
        mInsurance = new ExpenseFragment();
        mPersonal = new ExpenseFragment();
        mWants = new ExpenseFragment();
        mOther = new ExpenseFragment();

        sectionPageAdapter.addFragment(mHousing, "Housing");
        sectionPageAdapter.addFragment(mInsurance, "Insurance");
        sectionPageAdapter.addFragment(mPersonal, "Personal");
        sectionPageAdapter.addFragment(mWants, "Wants");
        sectionPageAdapter.addFragment(mOther, "Other");
        viewPager.setAdapter(sectionPageAdapter);

        initializeExpenses();

        mHousing.setOnClickListener(expenseListener);
        mPersonal.setOnClickListener(expenseListener);
        mInsurance.setOnClickListener(expenseListener);
        mWants.setOnClickListener(expenseListener);
        mOther.setOnClickListener(expenseListener);
    }

    /**
     * initialize all recycler lists with expenses
     */
    private void initializeExpenses() {
        initializeCategories(mHousing, Category.HOUSING);
        initializeCategories(mPersonal, Category.PERSONAL);
        initializeCategories(mInsurance, Category.INSURANCE);
        initializeCategories(mWants, Category.WANTS);
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
        editor = putDouble(editor, INCOME_SHARED_PREFS, mIncome);
        editor.apply();
    }

    /**
     * load state of app from shared preferences
     */
    private void loadData() {
        SharedPreferences sp = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        mIncome = getDouble(sp, INCOME_SHARED_PREFS, 0d);
    }

    /**
     * save a Double data type in shared preferences by converting to bytes and storing as Long
     * @param edit SharedPreferences editor
     * @param key string key
     * @param value Double value to store as Long
     * @return SharedPreferences editor with changes made
     */
    SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    /**
     * get the Double value from SharedPreferences stored as Long
     * @param prefs SharedPreferences
     * @param key string key
     * @param defaultValue Double default value
     * @return Double value stored as bytes in SharedPreferences
     */
    double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    /**
     * update UI currency text
     */
    private void updateCurrencyText() {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        switch (mCurrentSpinnerItem) {
            case INCOME:
                mCurrencyText.setText(numberFormat.format(mIncome));
                break;
            case EXPENSES:
                mCurrencyText.setText(numberFormat.format(mTotalExpenses));
                break;
            case AFTER_EXPENSES:
                mCurrencyText.setText(numberFormat.format(mAfterExpenses));
                break;
        }
    }

    ExpenseFragment.OnClickListener expenseListener = new ExpenseFragment.OnClickListener() {
        @Override
        public void expenseUpdated() {
            mTotalExpenses = BudgetDbHelper.getInstance(MainActivity.this).totalExpenses();
            mAfterExpenses = mIncome - mTotalExpenses;
            updateCurrencyText();
        }
    };

    View.OnClickListener incomeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), IncomeActivity.class);
            intent.putExtra(INCOME_EXTRA, mIncome);
            startActivityForResult(intent, INCOME_ACTIVITY_REQUEST);
        }
    };

    // TODO: 11/1/2019 Decide if edit overwrites or adds
    // TODO: 11/1/2019 Every month, reset expenses 
    // TODO: 11/1/2019 lookup how to put hints on app 
    // TODO: 11/1/2019 history for each expense as json
    // TODO: 11/1/2019 display date for each expense 
}
