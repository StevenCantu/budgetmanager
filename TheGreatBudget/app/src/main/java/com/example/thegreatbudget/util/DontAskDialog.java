package com.example.thegreatbudget.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.example.thegreatbudget.R;

import static com.example.thegreatbudget.activities.IncomeActivity.CHECK_EDIT_KEY;

public class DontAskDialog extends AppCompatDialogFragment {
    public static final String MESSAGE = "thegreatbudget.util.DontAskDialog.message";
    public static final String KEY = "thegreatbudget.util.DontAskDialog.key";
    public static final String PREFS = "com.example.thegreatbudget.shared.prefs";
    public static final String ISCHECKED = "checked";
    public static final String NOTCHECKED = "not checked";

    private OnClickListener onClickListener;
    private CheckBox mIgnore;
    private String mMessage;
    private String mKey;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mMessage = bundle.getString(MESSAGE, "Are you sure you want to edit income");
            mKey = bundle.getString(KEY, CHECK_EDIT_KEY);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View layout = layoutInflater.inflate(R.layout.check_layout, null);

        mIgnore = layout.findViewById(R.id.skip_check);
        dialog.setView(layout);
        dialog.setTitle("Attention");
        dialog.setMessage(mMessage);

        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onClickListener != null) {
                    saveChecked(mKey);
                    onClickListener.positiveClick();
                }
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onClickListener != null) {
                    onClickListener.negativeClick();
                }
            }
        });

        return dialog.create();
    }

    private void saveChecked(String key) {
        String checkBoxResult = NOTCHECKED;

        if (mIgnore.isChecked()) {
            checkBoxResult = ISCHECKED;
        }

        SharedPreferences settings = getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
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
