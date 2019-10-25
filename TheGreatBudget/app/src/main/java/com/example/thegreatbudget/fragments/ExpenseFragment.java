package com.example.thegreatbudget.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thegreatbudget.R;
import com.example.thegreatbudget.adapters.ExpenseRecyclerAdapter;
import com.example.thegreatbudget.database.BudgetDbHelper;
import com.example.thegreatbudget.model.Category;
import com.example.thegreatbudget.model.Expenses;

public class ExpenseFragment extends Fragment {

    public static final String CATEGORY = "thegreatbudget.fragments.expense.category";
    private static final String TAG = "ExpenseFragment";

    private int mCategory = Category.MISC;
    private TextView addExpenseText;
    private Cursor mCursor;
    private Context mContext;
    private OnClickListener mListener;
    private ExpenseRecyclerAdapter mAdapter;

    private BudgetDbHelper mDataBase;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mDataBase = BudgetDbHelper.getInstance(context);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mCategory = bundle.getInt(CATEGORY, Category.MISC);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expense_fragment, container, false);
        mCursor = mDataBase.getExpensesCursor(mCategory);
        initRecyclerView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildAddDialog();
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCursor.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener = null;
    }

    private void initRecyclerView(View view) {
        addExpenseText = view.findViewById(R.id.add_misc_text);
        RecyclerView recyclerView = view.findViewById(R.id.expense_recycler);
        mAdapter = new ExpenseRecyclerAdapter(getContext(), mCursor);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter.setOnItemClickedListener(new ExpenseRecyclerAdapter.OnItemClickedListener() {
            @Override
            public void amountClicked(Expenses expense) {
                buildDialog(expense);
                mListener.amountClick(expense);
            }
        });
        
        if (mCategory == Category.MISC) {
            addExpenseText.setVisibility(View.VISIBLE);
        }
    }

    private void buildAddDialog() {
        final Expenses expense = new Expenses();
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Add Expense");
        builder.setMessage("Enter your expense.");

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        layoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);

        // Set up the input
        final EditText amountText = new EditText(mContext);
        final EditText expenseText = new EditText(mContext);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        amountText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        amountText.setGravity(Gravity.CENTER);
        amountText.setHint("Enter amount");

        expenseText.setInputType(InputType.TYPE_CLASS_TEXT);
        expenseText.setGravity(Gravity.CENTER);
        expenseText.setHint("Enter expense");

        amountText.setLayoutParams(layoutParams);
        expenseText.setLayoutParams(layoutParams);
        LinearLayout container = new LinearLayout(mContext);
        container.setOrientation(LinearLayout.VERTICAL);
        container.addView(expenseText);
        container.addView(amountText);
        builder.setView(container);

        // Set up the buttons
        builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (amountText.getText().length() == 0 || expenseText.getText().length() == 0) return;

                float num = Float.parseFloat(amountText.getText().toString());
                expense.setAmount(num);
                expense.setTitle(expenseText.getText().toString());
                expense.setCategoryId(Category.MISC);
                mDataBase.addExpense(expense);
                Log.d(TAG, "onClick: " + mDataBase.totalExpenses());
                swapCursor();

            }
        });
        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void buildDialog(final Expenses expense) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Amount");
        builder.setMessage(String.format("Enter your %s expense.", expense.getTitle()));

        // Set up the input
        final EditText input = new EditText(mContext);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setGravity(Gravity.CENTER);
        input.setHint("Enter amount");
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        layoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        input.setLayoutParams(layoutParams);
        FrameLayout container = new FrameLayout(mContext);
        container.addView(input);
        builder.setView(container);

        // Set up the buttons
        builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().length() == 0) return;

                float num = Float.parseFloat(input.getText().toString());
                expense.setAmount(num);
                mDataBase.editExpense(expense);
                Log.d(TAG, "onClick: " + mDataBase.totalExpenses());
                swapCursor();

            }
        });
        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void swapCursor() {
        mAdapter.swapCursor(mDataBase.getExpensesCursor(mCategory));
    }

    public void setOnClickListener(OnClickListener clickListener) {
        mListener = clickListener;
    }

    public interface OnClickListener {
        void amountClick(Expenses expense);
    }

    // TODO: 10/23/2019 make dialog class
    // TODO: 10/23/2019 make click listener for add method

}
