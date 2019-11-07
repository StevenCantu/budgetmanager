package com.example.thegreatbudget.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.thegreatbudget.R;
import com.example.thegreatbudget.database.BudgetContract;
import com.example.thegreatbudget.model.Expenses;

import java.text.NumberFormat;
import java.util.Locale;

public class ExpenseRecyclerAdapter extends RecyclerView.Adapter<ExpenseRecyclerAdapter.ViewHolder> {

    private static final String TAG = "ExpenseRecyclerAdapter";

    private Context mContext;
    private Cursor mCursor;
    private OnItemClickedListener mListener;

    public ExpenseRecyclerAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        final long id = mCursor.getLong(mCursor.getColumnIndex(BudgetContract.BudgetTable._ID));
        final String expense = mCursor.getString(mCursor.getColumnIndex(BudgetContract.BudgetTable.EXPENSE));
        final float amount = mCursor.getFloat(mCursor.getColumnIndex(BudgetContract.BudgetTable.AMOUNT));
        final int categoryId = mCursor.getInt(mCursor.getColumnIndex(BudgetContract.BudgetTable.CATEGORY_ID));
        // TODO: 11/6/2019 add History and include in Expense ctor

        viewHolder.mExpense.setText(expense);
        viewHolder.mAmount.setText(stringAmount(amount));
        viewHolder.itemView.setTag(id);

        viewHolder.mAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.amountClicked(new Expenses(id, expense, amount, categoryId));
                }
            }
        });
        viewHolder.mExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.itemClicked(new Expenses(id, expense, amount, categoryId));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    /**
     * convert expense amount to a currency formatted string
     * or "Enter amount" if there is no amount.
     *
     * @param amount Expense amount
     * @return formatted string
     */
    private String stringAmount(float amount) {
        if (amount <= 0) return "Enter amount";
        else {
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
            return numberFormat.format(amount);
        }
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }

        mCursor = newCursor;

        if (newCursor != null) {
            Log.d(TAG, "swapCursor: ");
            notifyDataSetChanged();
        }
    }

    public void setOnItemClickedListener(OnItemClickedListener clickedListener) {
        mListener = clickedListener;
    }

    public interface OnItemClickedListener {
        void amountClicked(Expenses expense);
        void itemClicked(Expenses expense);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mExpense;
        private TextView mAmount;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            mExpense = itemView.findViewById(R.id.recycler_text);
            mAmount = itemView.findViewById(R.id.recycler_edit);
        }
    }
}
