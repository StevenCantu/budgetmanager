package com.example.thegreatbudget.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.thegreatbudget.R;

public class ExpenseDialogFragment extends DialogFragment {

    public static final String TITLE = "thegreatbudget.fragments.expensedialog.title";
    public static final String MESSAGE = "thegreatbudget.fragments.expensedialog.message";
    public static final String CONFIRM_BUTTON_TEXT = "thegreatbudget.fragments.expensedialog.confirm.button.text";
    public static final String HAS_EXPENSE = "thegreatbudget.fragments.expensedialog.has.expense";
    public static final String HAS_AMOUNT = "thegreatbudget.fragments.expensedialog.has.amount";
    public static final String HAS_CANCEL = "thegreatbudget.fragments.expensedialog.has.cancel";

    private OnClickListener onClickListener;

    private String mTitle;
    private String mMessage;
    private String mExpenseText;
    private String mAmountText;
    private String mConfirmButtonText;
    private boolean mHasExpense;
    private boolean mHasAmount;
    private boolean mHasCancelButton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mTitle = bundle.getString(TITLE, "Delete");
            mMessage = bundle.getString(MESSAGE, "Are you sure you want to delete?");
            mConfirmButtonText = bundle.getString(CONFIRM_BUTTON_TEXT, "CONFIRM");
            mHasExpense = bundle.getBoolean(HAS_EXPENSE, false);
            mHasAmount = bundle.getBoolean(HAS_AMOUNT, false);
            mHasCancelButton = bundle.getBoolean(HAS_CANCEL, true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.custom_dialog_layout, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        TextView title = view.findViewById(R.id.custom_dialog_title);
        TextView message = view.findViewById(R.id.custom_dialog_message);
        final EditText expense = view.findViewById(R.id.custom_dialog_expense);
        final EditText amount = view.findViewById(R.id.custom_dialog_amount);
        CheckBox checkBox = view.findViewById(R.id.custom_dialog_check);
        TextView confirm = view.findViewById(R.id.custom_dialog_positive);
        TextView cancel = view.findViewById(R.id.custom_dialog_negative);
        confirm.setText(mConfirmButtonText.toUpperCase());

        checkBox.setVisibility(View.GONE);
        title.setText(mTitle);
        message.setText(mMessage);
        if (!mHasExpense) {
            expense.setVisibility(View.GONE);
        }
        if (!mHasAmount) {
            amount.setVisibility(View.GONE);
        }

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHasExpense) mExpenseText = expense.getText().toString();
                if (mHasAmount) mAmountText = amount.getText().toString();
                if (onClickListener != null) {
                    onClickListener.positiveClick(mExpenseText, mAmountText);
                }
                dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.negativeClick();
                }
                dismiss();
            }
        });

        if (!mHasCancelButton) {
            cancel.setVisibility(View.INVISIBLE);
        }

    }

    public interface OnClickListener {
        void positiveClick(String expenseText, String amountText);

        void negativeClick();
    }

    public void setOnClickListener(OnClickListener listener) {
        onClickListener = listener;
    }

}
