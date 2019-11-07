package com.example.thegreatbudget;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.thegreatbudget.model.Category;
import com.example.thegreatbudget.model.Expenses;

import java.text.NumberFormat;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {

    public static final String EXPENSE_EXTRA = "thegreatbudget.expense.obj.extra";

    private TextView mExpenseItem;
    private TextView mExpenseTotal;
    private TextView mClearAll;
    private TextView mDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setTitle("Details");

        String itemName = "";
        float itemAmount = 0f;
        boolean deletable = false;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Expenses expense = bundle.getParcelable(EXPENSE_EXTRA);
            itemName = expense.getTitle();
            itemAmount = expense.getAmount();
            deletable = Category.MISC == expense.getCategoryId();
        }

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        mExpenseItem = findViewById(R.id.expense_item);
        mExpenseTotal = findViewById(R.id.expense_total);
        mClearAll = findViewById(R.id.clear_all);
        mDelete = findViewById(R.id.delete_expense);

        if (deletable) mDelete.setVisibility(View.VISIBLE);
        else mDelete.setVisibility(View.INVISIBLE);

        mExpenseItem.setText(itemName);
        mExpenseTotal.setText(numberFormat.format(itemAmount));

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
}
