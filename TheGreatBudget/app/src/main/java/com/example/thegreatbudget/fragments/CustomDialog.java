package com.example.thegreatbudget.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.thegreatbudget.R;

public class CustomDialog extends AppCompatDialogFragment {

    public static final String TITLE = "thegreatbudget.util.customdialog.title";
    public static final String MESSAGE = "thegreatbudget.util.customdialog.message";
    public static final String HAS_EXPENSE = "thegreatbudget.util.customdialog.has.expense";
    public static final String HAS_AMOUNT = "thegreatbudget.util.customdialog.has.amount";

    private OnClickListener onClickListener;

    private String mTitle;
    private String mMessage;
    private String mExpenseText;
    private String mAmountText;
    private boolean mHasExpense;
    private boolean mHasAmount;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mTitle = bundle.getString(TITLE, "Delete");
            mMessage = bundle.getString(MESSAGE, "Are you sure you want to delete?");
            mHasExpense = bundle.getBoolean(HAS_EXPENSE, false);
            mHasAmount = bundle.getBoolean(HAS_AMOUNT, false);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mTitle);
        builder.setMessage(mMessage);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        layoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);

        LinearLayout container = new LinearLayout(getActivity());
        container.setOrientation(LinearLayout.VERTICAL);
        final EditText expenseText = new EditText(getActivity());
        final EditText amountText = new EditText(getActivity());


        if (mHasExpense) {
            expenseText.setInputType(InputType.TYPE_CLASS_TEXT);
            expenseText.setGravity(Gravity.CENTER);
            expenseText.setHint("Enter expense");
            expenseText.setLayoutParams(layoutParams);
            container.addView(expenseText);
        }
        if (mHasAmount) {
            amountText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            amountText.setGravity(Gravity.CENTER);
            amountText.setHint("Enter amount");
            amountText.setLayoutParams(layoutParams);
            container.addView(amountText);
        }

        builder.setView(container);

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mHasExpense) mExpenseText = expenseText.getText().toString();
                if (mHasAmount) mAmountText = amountText.getText().toString();
                onClickListener.positiveClick(mExpenseText, mAmountText);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onClickListener != null) {
                    onClickListener.negativeClick();
                }
            }
        });

        return builder.create();
    }

    public interface OnClickListener {
        void positiveClick(String expenseText, String amountText);

        void negativeClick();
    }

    public void setOnClickListener(OnClickListener listener) {
        onClickListener = listener;
    }
}
