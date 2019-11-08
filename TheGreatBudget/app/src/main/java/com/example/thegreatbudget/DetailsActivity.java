package com.example.thegreatbudget;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thegreatbudget.adapters.HistoryRecyclerAdapter;
import com.example.thegreatbudget.model.Category;
import com.example.thegreatbudget.model.Expenses;
import com.example.thegreatbudget.model.History;
import com.example.thegreatbudget.model.HistoryItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {

    public static final String EXPENSE_EXTRA = "thegreatbudget.expense.obj.extra";
    private static final String TAG = "DetailsActivity";

    private TextView mExpenseItem;
    private TextView mExpenseTotal;
    private TextView mClearAll;
    private TextView mDelete;

    private History mHistory = new History();
    private ColorStateList mStateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setTitle("Details");

        initColorStateList();
        initViews();
        initClickListeners();
        initRecyclerView();
    }

    private void initViews() {
        String itemName = "";
        float itemAmount = 0f;
        boolean deletable = false;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Expenses expense = bundle.getParcelable(EXPENSE_EXTRA);
            itemName = expense.getTitle();
            itemAmount = expense.getAmount();
            deletable = Category.MISC == expense.getCategoryId();
            mHistory = expense.getHistory();
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
        HistoryRecyclerAdapter adapter = new HistoryRecyclerAdapter(this, historyItems);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnClickListener(historyItemListener);
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
        if (mHistory.getHistory().isEmpty()) {
            mClearAll.setEnabled(false);
        }
        mClearAll.setOnClickListener(clearAllListener);
        mDelete.setOnClickListener(deleteItemListener);
    }

    View.OnClickListener clearAllListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    View.OnClickListener deleteItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    HistoryRecyclerAdapter.OnClickListener historyItemListener = new HistoryRecyclerAdapter.OnClickListener() {
        @Override
        public void onClick(HistoryItem item) {
            Toast.makeText(DetailsActivity.this, item.toString(), Toast.LENGTH_SHORT).show();
        }
    };
}
