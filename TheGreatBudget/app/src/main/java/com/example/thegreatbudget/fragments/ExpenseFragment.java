package com.example.thegreatbudget.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.thegreatbudget.DetailsActivity;
import com.example.thegreatbudget.R;
import com.example.thegreatbudget.adapters.ExpenseRecyclerAdapter;
import com.example.thegreatbudget.database.BudgetDbHelper;
import com.example.thegreatbudget.model.Category;
import com.example.thegreatbudget.model.Expenses;
import com.example.thegreatbudget.util.CustomDialog;

import static android.app.Activity.RESULT_CANCELED;

public class ExpenseFragment extends Fragment {

    public static final String CATEGORY = "thegreatbudget.fragments.expense.category";
    private static final String TAG = "ExpenseFragment";
    public static final int DETAILS_ACTIVITY_REQUEST = 20;


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
        Log.d(TAG, "onCreateView: gffdgdfgfg");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DETAILS_ACTIVITY_REQUEST && resultCode == RESULT_CANCELED) {
            if (mListener != null) {
                mListener.expenseUpdated();
            }
            swapCursor();
        }
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
                buildEditDialog(expense);
            }

            @Override
            public void itemClicked(Expenses expense) {
                Intent intent = new Intent(mContext, DetailsActivity.class);
                intent.putExtra(DetailsActivity.EXPENSE_EXTRA, expense);
                startActivityForResult(intent, DETAILS_ACTIVITY_REQUEST);
            }
        });

        if (mCategory == Category.MISC) {
            addExpenseText.setVisibility(View.VISIBLE);

            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView,
                                      @NonNull RecyclerView.ViewHolder viewHolder,
                                      @NonNull RecyclerView.ViewHolder viewHolder1) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                    Log.d(TAG, "onSwiped: " + viewHolder.getAdapterPosition());
                    Log.d(TAG, "onSwiped: " + viewHolder.itemView.getTag());
                    buildDeleteDialog((Long) viewHolder.itemView.getTag());
                }
            }).attachToRecyclerView(recyclerView);
        }
    }

    private void buildDeleteDialog(final long id) {
        CustomDialog dialog = new CustomDialog();
        dialog.setArguments(new Bundle());
        if (getActivity().getSupportFragmentManager() != null) {
            dialog.show(getActivity().getSupportFragmentManager(), "delete");
        }

        dialog.setOnClickListener(new CustomDialog.OnClickListener() {
            @Override
            public void positiveClick(String expenseText, String amountText) {
                mDataBase.deleteExpense(id);
                swapCursor();

                if (mListener != null) {
                    mListener.expenseUpdated();
                }
            }

            @Override
            public void negativeClick() {
                swapCursor();
            }
        });
    }

    private void buildAddDialog() {
        CustomDialog dialog = new CustomDialog();
        Bundle bundle = new Bundle();
        bundle.putString(CustomDialog.TITLE, "Add Expense");
        bundle.putString(CustomDialog.MESSAGE, "Enter your expense.");
        bundle.putBoolean(CustomDialog.HAS_EXPENSE, true);
        bundle.putBoolean(CustomDialog.HAS_AMOUNT, true);
        dialog.setArguments(bundle);

        if (getActivity().getSupportFragmentManager() != null) {
            dialog.show(getActivity().getSupportFragmentManager(), "delete");
        }

        dialog.setOnClickListener(new CustomDialog.OnClickListener() {
            @Override
            public void positiveClick(String expenseText, String amountText) {
                final Expenses expense = new Expenses();
                if (amountText.length() == 0 || expenseText.length() == 0) return;

                float num = Float.parseFloat(amountText);
                expense.setAmount(num);
                expense.getHistory().addItem(num);
                expense.setTitle(expenseText);
                expense.setCategoryId(Category.MISC);
                mDataBase.addExpense(expense);
                swapCursor();

                if (mListener != null) {
                    mListener.expenseUpdated();
                }
            }

            @Override
            public void negativeClick() {
            }
        });
    }

    private void buildEditDialog(final Expenses expense) {
        CustomDialog dialog = new CustomDialog();
        Bundle bundle = new Bundle();
        bundle.putString(CustomDialog.TITLE, "Amount");
        bundle.putString(CustomDialog.MESSAGE, String.format("Enter your %s expense.", expense.getTitle()));
        bundle.putBoolean(CustomDialog.HAS_AMOUNT, true);
        dialog.setArguments(bundle);

        if (getActivity().getSupportFragmentManager() != null) {
            dialog.show(getActivity().getSupportFragmentManager(), "delete");
        }

        dialog.setOnClickListener(new CustomDialog.OnClickListener() {
            @Override
            public void positiveClick(String expenseText, String amountText) {
                if (amountText.length() == 0) return;

                float num = Float.parseFloat(amountText);
                expense.getHistory().addItem(num);
                expense.setAmount(expense.getHistory().getTotal());
//                expense.setAmount(num);
                mDataBase.editExpense(expense);
                swapCursor();

                if (mListener != null) {
                    mListener.expenseUpdated();
                }
            }

            @Override
            public void negativeClick() {
            }
        });
    }

    public void swapCursor() {
        mAdapter.swapCursor(mDataBase.getExpensesCursor(mCategory));
    }

    public void setOnClickListener(OnClickListener clickListener) {
        mListener = clickListener;
    }

    public interface OnClickListener {
        void expenseUpdated();
    }
}
