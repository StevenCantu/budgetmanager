package com.example.thegreatbudget.activities;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thegreatbudget.R;
import com.example.thegreatbudget.adapters.HistoryRecyclerAdapter;
import com.example.thegreatbudget.database.BudgetDbHelper;
import com.example.thegreatbudget.model.Category;
import com.example.thegreatbudget.model.Expenses;
import com.example.thegreatbudget.model.History;
import com.example.thegreatbudget.model.HistoryItem;
import com.example.thegreatbudget.util.CustomDialog;
import com.example.thegreatbudget.util.DontAskDialog;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {

    public static final String EXPENSE_EXTRA = "thegreatbudget.expense.obj.extra";
    private static final String TAG = "DetailsActivity";
    public static final String CLEAR_ALL_KEY = "DetailsActivity.skipMessage.clearall";
    public static final String DELETE_KEY = "DetailsActivity.skipMessage.delete";


    private TextView mExpenseItem;
    private TextView mExpenseTotal;
    private TextView mClearAll;
    private TextView mDelete;

    private History mHistory = new History();
    private ColorStateList mStateList;
    private BudgetDbHelper mDataBase;
    private Expenses mExpense = new Expenses();

    HistoryRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setTitle("Details");
        mDataBase = BudgetDbHelper.getInstance(this);

        initColorStateList();
        initViews();
        initClickListeners();
        initRecyclerView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    private void initViews() {
        String itemName = "";
        float itemAmount = 0f;
        boolean deletable = false;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
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
                mExpense.getHistory().getHistory().remove((int) viewHolder.itemView.getTag());
                Log.d(TAG, "onSwiped: " + mExpense.getHistory().getTotal());
                updateDetails();
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void initColorStateList() {
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_enabled}, // disabled
        };
        int[] colors = new int[] {
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
        CustomDialog dialog = new CustomDialog();
        Bundle bundle = new Bundle();
        bundle.putString(CustomDialog.TITLE, "Amount");
        bundle.putString(CustomDialog.MESSAGE, String.format("Enter your %s expense.", mExpense.getTitle()));
        bundle.putBoolean(CustomDialog.HAS_AMOUNT, true);
        dialog.setArguments(bundle);

        if (this.getSupportFragmentManager() != null) {
            dialog.show(this.getSupportFragmentManager(), "delete");
        }

        dialog.setOnClickListener(new CustomDialog.OnClickListener() {
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
        DontAskDialog dialog = new DontAskDialog();

        Bundle bundle = new Bundle();
        bundle.putString(DontAskDialog.MESSAGE, "Are you sure you want to delete this expense tab?");
        bundle.putString(DontAskDialog.KEY, DELETE_KEY);

        dialog.setArguments(bundle);
        if (getSupportFragmentManager() != null && isNotChecked(DELETE_KEY)) {
            dialog.show(getSupportFragmentManager(), "delete");
        }
        dialog.setOnClickListener(new DontAskDialog.OnClickListener() {
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
        DontAskDialog dialog = new DontAskDialog();

        Bundle bundle = new Bundle();
        bundle.putString(DontAskDialog.MESSAGE, "Are you sure you want to clear the history?");
        bundle.putString(DontAskDialog.KEY, CLEAR_ALL_KEY);

        dialog.setArguments(bundle);
        if (getSupportFragmentManager() != null && isNotChecked(CLEAR_ALL_KEY)) {
            dialog.show(getSupportFragmentManager(), "clearall");
        }
        dialog.setOnClickListener(new DontAskDialog.OnClickListener() {
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
        SharedPreferences settings = getSharedPreferences(DontAskDialog.PREFS, MODE_PRIVATE);
        String skipMessage = settings.getString(key, DontAskDialog.NOTCHECKED);

        return !DontAskDialog.ISCHECKED.equals(skipMessage);
    }

    View.OnClickListener clearAllListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences sp = getSharedPreferences(DontAskDialog.PREFS, MODE_PRIVATE);
            String checked = sp.getString(CLEAR_ALL_KEY, DontAskDialog.NOTCHECKED);
            if (DontAskDialog.ISCHECKED.equals(checked)) {
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
                SharedPreferences sp = getSharedPreferences(DontAskDialog.PREFS, MODE_PRIVATE);
                String checked = sp.getString(DELETE_KEY, DontAskDialog.NOTCHECKED);
                if (DontAskDialog.ISCHECKED.equals(checked)) {
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
            Toast.makeText(DetailsActivity.this, item.toString(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onSwiped: " + mExpense.getHistory().getTotal());
        }
    };
}
