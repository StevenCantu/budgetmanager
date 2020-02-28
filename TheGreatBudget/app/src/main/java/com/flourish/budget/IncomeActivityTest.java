package com.flourish.budget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import static com.flourish.budget.util.Common.IS_CHECKED;
import static com.flourish.budget.util.Common.NOT_CHECKED;
import static com.flourish.budget.util.Common.SHARED_PREFERENCES;

import com.flourish.budget.activities.MainActivity;
import com.flourish.budget.fragments.NeverAskAgainDialog;
import com.flourish.budget.util.Common;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;

public class IncomeActivityTest extends AppCompatActivity
        implements IncomeTestFragmentNumberPad.FragmentNumberPadListener {

    // region constants
    private static final String TAG = "IncomeActivityTest";
    public static final String FRAGMENT_TAG = "IncomeFragment";
    public static final String INCOME = "com.flourish.budget.income.arg";
    public static final String CHECK_UNDO_KEY = "com.flourish.budget.skipMessage.undo";
    public static final String CHECK_EDIT_KEY = "com.flourish.budget.skipMessage.edit";
    public static final String EXTRA_INCOME = "com.flourish.budget.income.activity.extra.income";
    public static final int MAX_HISTORY_SIZE = 10;
    // endregion

    // region member variables
    // menu
    private Menu mIncomeMenu;
    // fragment
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    // undo
    private double mTempIncome;
    private double mTempUndo;
    private Deque<Double> mIncomeHistory = new ArrayDeque<>();
    // other
    private double mIncome;
    private FloatingActionButton mAddIncomeButton;
    private boolean mIsAddMode;
    private Snackbar mSnackbar;
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Common.themeSetterNoActionBar(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_test);

        setupActionBar();
        getIncomeFromIntent();
        mAddIncomeButton = findViewById(R.id.activity_income_test_add_button);

        mFragmentManager = getSupportFragmentManager();

        switchToMainScreenFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAddIncomeButton.setOnClickListener(addButtonListener);
        mAddIncomeButton.addOnHideAnimationListener(hideAnimationListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAddIncomeButton.removeOnHideAnimationListener(hideAnimationListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.income_activity_menu, menu);
        mIncomeMenu = menu;
        showMainScreenMenuItems(mIncomeMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
                handleEditMenuClick();
                return true;
            case R.id.menu_undo:
                handleUndoMenuClick();
                return true;
            case R.id.menu_cancel:
                handleNumberPadToMainTransition();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    /**
     * replace {@link Fragment} in {@link android.widget.FrameLayout} with an {@link IncomeTestFragmentMainScreen}
     */
    private void switchToMainScreenFragment() {
        Bundle bundle = new Bundle();
        bundle.putDouble(INCOME, mIncome);
        Fragment fragment = new IncomeTestFragmentMainScreen();
        fragment.setArguments(bundle);
        setFragment(fragment);
        setTitle("Income");

        Intent intent = new Intent();
        intent.putExtra(EXTRA_INCOME, mIncome);
        setResult(RESULT_OK, intent);
    }

    /**
     * replace {@link Fragment} in {@link android.widget.FrameLayout} with an {@link IncomeTestFragmentNumberPad}
     */
    private void switchToNumberPadScreenFragment() {
        setTitle(mIsAddMode ? "Add Income" : "Edit Income");
        addToIncomeHistory(mIncome);
        if (mSnackbar != null) {
            mSnackbar.dismiss();
        }

        Bundle bundle = new Bundle();
        bundle.putDouble(INCOME, mIncome);
        bundle.putBoolean(IncomeTestFragmentNumberPad.MODE, mIsAddMode);
        Fragment fragment = new IncomeTestFragmentNumberPad();
        fragment.setArguments(bundle);
        setFragment(fragment);
    }

    /**
     * show edit and undo menu items, hide cancel menu item
     * @param menu a {@link Menu}
     */
    private void showMainScreenMenuItems(Menu menu) {
        menu.findItem(R.id.menu_cancel).setVisible(false);
        menu.findItem(R.id.menu_edit).setVisible(true);
        menu.findItem(R.id.menu_undo).setVisible(true);
    }

    /**
     * show cancel menu item, hide edit and undo menu items
     * @param menu a {@link Menu}
     */
    private void showNumberScreenMenuItems(Menu menu) {
        menu.findItem(R.id.menu_cancel).setVisible(true);
        menu.findItem(R.id.menu_edit).setVisible(false);
        menu.findItem(R.id.menu_undo).setVisible(false);
    }

    /**
     * retrieve income from intent if it exists, default = 0.0
     */
    private void getIncomeFromIntent() {
        Intent intent = getIntent();
        mIncome = intent.getDoubleExtra(MainActivity.INCOME_EXTRA, 0.0f);
        Log.d(TAG, "getIncomeFromIntent: " + mIncome);
    }

    /**
     * replaces the default actionbar with a {@link Toolbar}
     */
    private void setupActionBar() {
        setTitle("Income");
        Toolbar toolbar = findViewById(R.id.income_test_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * places {@link Fragment} into the {@link android.widget.FrameLayout}
     * @param fragment a {@link Fragment}
     */
    private void setFragment(Fragment fragment) {
        mFragmentManager.popBackStack();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.income_test_frame, fragment, FRAGMENT_TAG);
        mFragmentTransaction.addToBackStack(FRAGMENT_TAG);
        mFragmentTransaction.commit();
    }

    /**
     * get checkbox checked property
     * @param key checkbox key
     * @return true if checkbox is unchecked, false otherwise
     */
    private boolean isCheckBoxNotChecked(String key) {
        SharedPreferences property = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        String skipMessage = property.getString(key, NOT_CHECKED);

        return !IS_CHECKED.equals(skipMessage);
    }

    /**
     * show edit, never ask again, dialog box
     */
    private void handleEditMenuClick() {
        boolean isNotChecked = isCheckBoxNotChecked(CHECK_EDIT_KEY);
        mIsAddMode = false;
        
        NeverAskAgainDialog dialog = new NeverAskAgainDialog();
        Bundle bundle = new Bundle();
        bundle.putString(NeverAskAgainDialog.KEY, CHECK_EDIT_KEY);
        dialog.setArguments(bundle);
        if (isNotChecked) {
            dialog.show(getSupportFragmentManager(), "handleEditMenuClick");
        } else {
            mAddIncomeButton.hide();
        }
        
        dialog.setOnClickListener(new NeverAskAgainDialog.OnClickListener() {
            @Override
            public void positiveClick() {
                mAddIncomeButton.hide();
            }

            @Override
            public void negativeClick() {

            }
        });
    }

    /**
     * show undo, never ask again, dialog box
     */
    private void handleUndoMenuClick() {
        boolean isNotChecked = isCheckBoxNotChecked(CHECK_UNDO_KEY);

        NeverAskAgainDialog dialog = new NeverAskAgainDialog();
        Bundle bundle = new Bundle();
        bundle.putString(NeverAskAgainDialog.MESSAGE, "Are you sure you want to undo your previous income change?");
        bundle.putString(NeverAskAgainDialog.KEY, CHECK_UNDO_KEY);
        dialog.setArguments(bundle);

        if (isNotChecked) {
            dialog.show(getSupportFragmentManager(), "showUndoDialog");
        } else {
            undoIncome();
        }

        dialog.setOnClickListener(new NeverAskAgainDialog.OnClickListener() {
            @Override
            public void positiveClick() {
                undoIncome();
            }

            @Override
            public void negativeClick() {

            }
        });
    }

    /**
     * switch to {@link IncomeTestFragmentMainScreen}, main screen {@link Menu},
     * and show {@link FloatingActionButton}
     */
    private void handleNumberPadToMainTransition() {
        switchToMainScreenFragment();
        showMainScreenMenuItems(mIncomeMenu);
        mAddIncomeButton.show();
    }

    /**
     * update income, income label, and switch screen
     * @param income new income
     */
    private void  handleEnterButtonClick(double income) {
        mIncome = income;
        handleNumberPadToMainTransition();
    }

    /**
     * add new income to income history
     * @param item new income
     */
    private void addToIncomeHistory(double item) {
        if (mIncomeHistory.size() < MAX_HISTORY_SIZE) {
            mIncomeHistory.add(item);
        } else {
            mIncomeHistory.removeFirst();
            mIncomeHistory.add(item);
        }
    }

    /**
     * display {@link Snackbar} with undo information, and an action to redo
     */
    private void showUndoSnackBar() {
        String msg = String.format(Locale.getDefault(), "You have removed $%.2f.", mTempIncome);
        mSnackbar = Snackbar.make(findViewById(R.id.activity_income_test), msg, Snackbar.LENGTH_LONG);
        mSnackbar.setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        redoIncome();
                    }
                }).show();
    }

    /**
     * redo undone changes
     */
    private void redoIncome() {
        mIncome = mTempIncome;
        mIncomeHistory.add(mTempUndo);
        switchToMainScreenFragment();
        showMainScreenMenuItems(mIncomeMenu);
    }

    /**
     * undo income to previous income, save old income in temporary variable for redo
     */
    private void undoIncome() {
        if (!mIncomeHistory.isEmpty()) {
            mTempIncome = mIncome;
            mIncome = mIncomeHistory.removeLast();
            mTempUndo = mIncome;
            switchToMainScreenFragment();
            showMainScreenMenuItems(mIncomeMenu);
            showUndoSnackBar();
        }
    }

    @Override
    public void enterButtonClicked(double income) {
        handleEnterButtonClick(income);
    }

    /**
     * listen for {@link FloatingActionButton} click
     */
    private View.OnClickListener addButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: ");
            mIsAddMode = true;
            mAddIncomeButton.hide();
        }
    };

    /**
     * listen for {@link FloatingActionButton} hide animation events
     */
    private Animator.AnimatorListener hideAnimationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            Log.d(TAG, "onAnimationStart: ");
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            Log.d(TAG, "onAnimationEnd: ");
            switchToNumberPadScreenFragment();
            showNumberScreenMenuItems(mIncomeMenu);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            Log.d(TAG, "onAnimationCancel: ");
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            Log.d(TAG, "onAnimationRepeat: ");
        }
    };
}

