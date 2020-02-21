package com.flourish.budget.activities;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.flourish.budget.R;
import com.flourish.budget.adapters.HistoryRecyclerAdapter;
import com.flourish.budget.database.BudgetDbHelper;
import com.flourish.budget.fragments.ExpenseDialogFragment;
import com.flourish.budget.fragments.NeverAskAgainDialog;
import com.flourish.budget.model.Category;
import com.flourish.budget.model.Expenses;
import com.flourish.budget.model.History;
import com.flourish.budget.model.HistoryItem;
import com.flourish.budget.util.Common;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.flourish.budget.util.Common.IS_CHECKED;
import static com.flourish.budget.util.Common.NOT_CHECKED;
import static com.flourish.budget.util.Common.SHARED_PREFERENCES;

public class DetailsActivity extends AppCompatActivity {

    public static final String READ_ONLY_EXTRA = "thegreatbudget.activities.read.only.extra";
    public static final String EXPENSE_EXTRA = "thegreatbudget.expense.obj.extra";
    private static final String TAG = "DetailsActivity";
    public static final String CLEAR_ALL_KEY = "DetailsActivity.skipMessage.clearall";
    public static final String DELETE_KEY = "DetailsActivity.skipMessage.delete";


    private TextView mExpenseItem;
    private TextView mExpenseTotal;
    private TextView mClearAll;
    private TextView mDelete;

    private HistoryItem mTempHistoryItem = new HistoryItem(0);
    private History mHistory = new History();
    private ColorStateList mStateList;
    private BudgetDbHelper mDataBase;
    private Expenses mExpense = new Expenses();
    private HistoryRecyclerAdapter mAdapter;

    private boolean mIsReadOnly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Common.themeSetterNoActionBar(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setupActionBar();
        mDataBase = BudgetDbHelper.getInstance(this);

        initColorStateList();
        initViews();
        initClickListeners();
        initRecyclerView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mIsReadOnly) {
            return super.onCreateOptionsMenu(menu);
        }
        MenuInflater inflater = getMenuInflater();
        inflater.inflate((R.menu.details_activity_menu), menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_expense) {
            buildEditDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void setupActionBar() {
        setTitle("Details");
        Toolbar toolbar = findViewById(R.id.details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        String itemName = "";
        float itemAmount = 0f;
        boolean deletable = false;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mIsReadOnly = bundle.getBoolean(READ_ONLY_EXTRA, false);
            mExpense = bundle.getParcelable(EXPENSE_EXTRA);
            itemName = mExpense.getTitle();
            itemAmount = mExpense.getAmount();
            deletable = Category.MISC == mExpense.getCategoryId();
            mHistory = mExpense.getHistory();
        }

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        mExpenseItem = findViewById(R.id.expense_item);
        mExpenseTotal = findViewById(R.id.expense_total);
        mClearAll = findViewById(R.id.clear_all);
        mClearAll.setTextColor(mStateList);
        mDelete = findViewById(R.id.delete_expense);
        mDelete.setTextColor(mStateList);

        if (deletable) mDelete.setVisibility(View.VISIBLE);
        else mDelete.setVisibility(View.INVISIBLE);

        if (mIsReadOnly) {
            mClearAll.setVisibility(View.INVISIBLE);
            mDelete.setVisibility(View.INVISIBLE);
        }

        mExpenseItem.setText(itemName);
        mExpenseTotal.setText(numberFormat.format(itemAmount));
    }

    private void initRecyclerView() {
        List<HistoryItem> historyItems = new ArrayList<>(mHistory.getHistory());
        Collections.reverse(historyItems);
        RecyclerView recyclerView = findViewById(R.id.history_recycler_view);
        mAdapter = new HistoryRecyclerAdapter(this, historyItems);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.setOnClickListener(historyItemListener);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                mTempHistoryItem = mExpense.getHistory().getHistory().get((int) viewHolder.itemView.getTag());
                mExpense.getHistory().getHistory().remove((int) viewHolder.itemView.getTag());
                Log.d(TAG, "onSwiped: " + mExpense.getHistory().getTotal());
                updateDetails();

                String msg = "You have removed " + mTempHistoryItem.toString() + " from the list";
                Snackbar.make(findViewById(R.id.activity_details), msg, Snackbar.LENGTH_LONG)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mExpense.getHistory().addItem(mTempHistoryItem);
                                updateDetails();
                            }
                        })
                        .show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void initColorStateList() {
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled}, // enabled
                new int[]{-android.R.attr.state_enabled}, // disabled
        };
        int[] colors = new int[]{
                ResourcesCompat.getColor(getResources(), R.color.accent, null),
                Color.GRAY,
        };
        mStateList = new ColorStateList(states, colors);
    }

    private void initClickListeners() {
        mClearAll.setEnabled(!mHistory.getHistory().isEmpty());
        mClearAll.setOnClickListener(clearAllListener);
        mDelete.setOnClickListener(deleteItemListener);
    }

    private void buildEditDialog() {
        ExpenseDialogFragment dialog = new ExpenseDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ExpenseDialogFragment.TITLE, "Amount");
        bundle.putString(ExpenseDialogFragment.MESSAGE, String.format("Enter your %s expense.", mExpense.getTitle()));
        bundle.putBoolean(ExpenseDialogFragment.HAS_AMOUNT, true);
        dialog.setArguments(bundle);

        if (this.getSupportFragmentManager() != null) {
            dialog.show(this.getSupportFragmentManager(), "delete");
        }

        dialog.setOnClickListener(new ExpenseDialogFragment.OnClickListener() {
            @Override
            public void positiveClick(String expenseText, String amountText) {
                if (amountText.length() == 0) return;
                float num = Float.parseFloat(amountText);
                mExpense.getHistory().addItem(num);
                updateDetails();
            }

            @Override
            public void negativeClick() {
            }
        });
    }

    private void updateDetails() {
        mExpense.setAmount(mExpense.getHistory().getTotal());
        mDataBase.editExpense(mExpense);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        List<HistoryItem> historyItems = new ArrayList<>(mExpense.getHistory().getHistory());
        Collections.reverse(historyItems);
        mAdapter.swapList(historyItems);
        mExpenseTotal.setText(numberFormat.format(mExpense.getAmount()));

        mClearAll.setEnabled(!mHistory.getHistory().isEmpty());
    }

    private void deleteDialog() {
        NeverAskAgainDialog dialog = new NeverAskAgainDialog();

        Bundle bundle = new Bundle();
        bundle.putString(NeverAskAgainDialog.MESSAGE, "Are you sure you want to delete this expense tab?");
        bundle.putString(NeverAskAgainDialog.KEY, DELETE_KEY);

        dialog.setArguments(bundle);
        if (getSupportFragmentManager() != null && isNotChecked(DELETE_KEY)) {
            dialog.show(getSupportFragmentManager(), "delete");
        }
        dialog.setOnClickListener(new NeverAskAgainDialog.OnClickListener() {
            @Override
            public void positiveClick() {
                mDataBase.deleteExpense(mExpense.getId());
                setResult(RESULT_CANCELED);
                DetailsActivity.this.finish();
            }

            @Override
            public void negativeClick() {

            }
        });
    }

    private void clearAllDialog() {
        NeverAskAgainDialog dialog = new NeverAskAgainDialog();

        Bundle bundle = new Bundle();
        bundle.putString(NeverAskAgainDialog.MESSAGE, "Are you sure you want to clear the history?");
        bundle.putString(NeverAskAgainDialog.KEY, CLEAR_ALL_KEY);

        dialog.setArguments(bundle);
        if (getSupportFragmentManager() != null && isNotChecked(CLEAR_ALL_KEY)) {
            dialog.show(getSupportFragmentManager(), "clearall");
        }
        dialog.setOnClickListener(new NeverAskAgainDialog.OnClickListener() {
            @Override
            public void positiveClick() {
                mExpense.getHistory().clearAll();
                updateDetails();
            }

            @Override
            public void negativeClick() {

            }
        });
    }

    private boolean isNotChecked(String key) {
        SharedPreferences settings = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        String skipMessage = settings.getString(key, NOT_CHECKED);

        return !IS_CHECKED.equals(skipMessage);
    }

    View.OnClickListener clearAllListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences sp = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
            String checked = sp.getString(CLEAR_ALL_KEY, NOT_CHECKED);
            if (IS_CHECKED.equals(checked)) {
                mExpense.getHistory().clearAll();
                updateDetails();
            } else {
                clearAllDialog();
            }
        }
    };

    View.OnClickListener deleteItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mExpense.getCategoryId() == Category.MISC) {
                SharedPreferences sp = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
                String checked = sp.getString(DELETE_KEY, NOT_CHECKED);
                if (IS_CHECKED.equals(checked)) {
                    mDataBase.deleteExpense(mExpense.getId());
                    setResult(RESULT_CANCELED);
                    DetailsActivity.this.finish();
                } else {
                    deleteDialog();
                }
            }
        }
    };

    HistoryRecyclerAdapter.OnClickListener historyItemListener = new HistoryRecyclerAdapter.OnClickListener() {
        @Override
        public void onClick(HistoryItem item) {
            Log.d(TAG, "onSwiped: " + mExpense.getHistory().getTotal());
        }
    };
}
