package com.flourish.budget;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.NumberFormat;
import java.util.Locale;

public class IncomeFragmentMainScreen extends Fragment {

    // region constants
    private static final String TAG = "IncomeTestFragmentMainS";
    // endregion

    // region member variables
    private TextView mIncomeText;
    private double mIncome;
    // endregion

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_income_mainscreen, container, false);
        setupViews(view);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");

        Bundle bundle = getArguments();
        if (bundle != null) {
            mIncome = bundle.getDouble(IncomeActivity.INCOME, 0.0);
        }
    }

    /**
     * set text property of the income {@link TextView}
     * @param income income double
     */
    private void setIncomeText(double income) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
        String currencyString = numberFormat.format(income);
        mIncomeText.setText(currencyString);
    }

    /**
     * initialize and setup layout {@link View}
     * @param view inflated {@link View}
     */
    private void setupViews(View view) {
        mIncomeText = view.findViewById(R.id.income_test_text);
        setIncomeText(mIncome);
    }
}
