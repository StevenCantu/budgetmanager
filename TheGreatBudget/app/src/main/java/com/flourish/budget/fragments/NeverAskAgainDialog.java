package com.flourish.budget.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.flourish.budget.R;

import static com.flourish.budget.util.Common.IS_CHECKED;
import static com.flourish.budget.util.Common.NOT_CHECKED;
import static com.flourish.budget.util.Common.SHARED_PREFERENCES;

public class NeverAskAgainDialog extends DialogFragment {

    public static final String MESSAGE = "thegreatbudget.fragments.NeverAskAgainDialog.message";
    public static final String KEY = "thegreatbudget.fragments.NeverAskAgainDialog.key";
    private static final String EMPTY_KEY = "EMPTY";

    private OnClickListener onClickListener;
    private String mMessage;
    private String mKey;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mMessage = bundle.getString(MESSAGE, "Are you sure you want to edit income?");
            mKey = bundle.getString(KEY, EMPTY_KEY);
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
        EditText expense = view.findViewById(R.id.custom_dialog_expense);
        EditText amount = view.findViewById(R.id.custom_dialog_amount);
        final CheckBox checkBox = view.findViewById(R.id.custom_dialog_check);
        TextView confirm = view.findViewById(R.id.custom_dialog_positive);
        TextView cancel = view.findViewById(R.id.custom_dialog_negative);

        expense.setVisibility(View.GONE);
        amount.setVisibility(View.GONE);

        title.setText("Attention");
        message.setText(mMessage);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    saveChecked(mKey, checkBox);
                    onClickListener.positiveClick();
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
    }

    private void saveChecked(String key, CheckBox checkBox) {
        if (EMPTY_KEY.equals(key)) return;

        String checkBoxResult = NOT_CHECKED;

        if (checkBox.isChecked()) {
            checkBoxResult = IS_CHECKED;
        }

        SharedPreferences settings = getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(key, checkBoxResult);
        editor.apply();
    }

    public interface OnClickListener {
        void positiveClick();

        void negativeClick();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
