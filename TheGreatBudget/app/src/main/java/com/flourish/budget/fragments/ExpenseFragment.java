package com.flourish.budget.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flourish.budget.R;
import com.flourish.budget.activities.DetailsActivity;
import com.flourish.budget.adapters.ExpenseRecyclerAdapter;
import com.flourish.budget.database.BudgetDbHelper;
import com.flourish.budget.model.Category;
import com.flourish.budget.model.Expenses;

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
        ExpenseDialogFragment dialog = new ExpenseDialogFragment();
        dialog.setArguments(new Bundle());
        if (getActivity().getSupportFragmentManager() != null) {
            dialog.show(getActivity().getSupportFragmentManager(), "delete");
        }

        dialog.setOnClickListener(new ExpenseDialogFragment.OnClickListener() {
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
        ExpenseDialogFragment dialog = new ExpenseDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ExpenseDialogFragment.TITLE, "Add Expense");
        bundle.putString(ExpenseDialogFragment.MESSAGE, "Enter your expense.");
        bundle.putBoolean(ExpenseDialogFragment.HAS_EXPENSE, true);
        bundle.putBoolean(ExpenseDialogFragment.HAS_AMOUNT, true);
        dialog.setArguments(bundle);

        if (getActivity().getSupportFragmentManager() != null) {
            dialog.show(getActivity().getSupportFragmentManager(), "delete");
        }

        dialog.setOnClickListener(new ExpenseDialogFragment.OnClickListener() {
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
        ExpenseDialogFragment dialog = new ExpenseDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ExpenseDialogFragment.TITLE, "Amount");
        bundle.putString(ExpenseDialogFragment.MESSAGE, String.format("Enter your %s expense.", expense.getTitle()));
        bundle.putBoolean(ExpenseDialogFragment.HAS_AMOUNT, true);
        dialog.setArguments(bundle);

        if (getActivity().getSupportFragmentManager() != null) {
            dialog.show(getActivity().getSupportFragmentManager(), "delete");
        }

        dialog.setOnClickListener(new ExpenseDialogFragment.OnClickListener() {
            @Override
            public void positiveClick(String expenseText, String amountText) {
                if (amountText.length() == 0) return;

                float num = Float.parseFloat(amountText);
                expense.getHistory().addItem(num);
                expense.setAmount(expense.getHistory().getTotal());
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
