package com.flourish.budget;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.NumberFormat;
import java.util.Locale;

public class IncomeFragmentNumberPad extends Fragment {

    // region constants
    private static final String TAG = "FragmentNumPad";
    private static final int KEYPAD_INDEX = 11;
    static final String MODE = "com.flourish.budget.mode.arg";
    private static final double MAX_INCOME = 999999.99;
    // endregion

    // region member variables
    // listeners
    private FragmentNumberPadListener mListener;
    // number pad
    private NumberPad mNumberPad;
    private TextView mNumberPadInput;
    // buttons
    private Button mEnter;
    private Button[] mInputs = new Button[KEYPAD_INDEX];
    private ImageButton mDelete;
    // income
    private TextView mIncomeText;
    private double mIncome;
    // mode
    private boolean mIsAddMode;
    // other
    private Handler mHandler;
    // endregion

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mNumberPad = new NumberPad();
        mHandler = new Handler();

        Bundle bundle = getArguments();
        if (bundle != null) {
            mIncome = bundle.getDouble(IncomeActivity.INCOME, 0.0);
            mIsAddMode = bundle.getBoolean(MODE, true);
        }

        if (context instanceof FragmentNumberPadListener) {
            mListener = (FragmentNumberPadListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FragmentNumberPadListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income_numberpad, container, false);
        setupViews(view);
        return view;
    }

    public interface FragmentNumberPadListener {
        void enterButtonClicked(double income);
    }

    /**
     * initialize and setup layout {@link View}
     * @param view inflated {@link View}
     */
    private void setupViews(View view) {
        mNumberPadInput = view.findViewById(R.id.income_test_number_pad_input);
        mIncomeText = view.findViewById(R.id.income_test_text);
        mEnter = view.findViewById(R.id.income_test_enter_button);
        mDelete = view.findViewById(R.id.income_test_delete_button);

        for (int i = 0; i < KEYPAD_INDEX; i++) {
            String buttonID = "button" + i;
            if (i == 10) {
                buttonID = "buttonDecimal";
            }

            int resourceID = getResources().getIdentifier(buttonID, "id", view.getContext().getPackageName());
            mInputs[i] = view.findViewById(resourceID);
            mInputs[i].setOnClickListener(numberPadListener);
        }
        mDelete.setOnClickListener(numberPadListener);
        mEnter.setOnClickListener(enterButtonListener);

        setCurrencyTextOn(mNumberPadInput, 0);
        setCurrencyTextOn(mIncomeText, mIncome);
    }

    /**
     * set {@link TextView} text property as currency
     * @param input a double to be displayed
     */
    private void setCurrencyTextOn(TextView textView, double input) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
        String currencyString = numberFormat.format(input);
        textView.setText(currencyString);
    }

    /**
     * handle {@link View} clicked from number pad
     * @param view a button from number pad
     */
    private void buttonClickHandler(View view) {
        switch (view.getId()) {
            case R.id.buttonDecimal:
                if (!mNumberPad.hasDecimal()) {
                    Log.d(TAG, "buttonClickHandler: " + ((Button) view).getText().toString());
                    mNumberPad.push(((Button) view).getText().toString());
                }
                break;
            case R.id.income_test_delete_button:
                mNumberPad.pop();
                break;
            default:
                Log.d(TAG, "buttonClickHandler: " + ((Button) view).getText().toString());
                mNumberPad.push(((Button) view).getText().toString());
                break;
        }
        setCurrencyTextOn(mNumberPadInput, mNumberPad.toDouble());
    }

    /**
     * handle enter {@link Button} click
     */
    private void handleEnterButton() {
        double temp = mIncome;
        if (mIsAddMode) {
            mIncome += mNumberPad.toDouble();
        } else {
            mIncome = mNumberPad.toDouble();
        }

        if (mIncome > MAX_INCOME) {
            mIncome = temp;
            Toast.makeText(getContext(), "You have exceeded the limit.", Toast.LENGTH_SHORT).show();
        }

        setCurrencyTextOn(mIncomeText, mIncome);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mNumberPad.clear();
                setCurrencyTextOn(mNumberPadInput, mNumberPad.toDouble());
                mListener.enterButtonClicked(mIncome);
            }
        }, 350);
    }

    /**
     * listen for enter button click
     */
    private View.OnClickListener enterButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            handleEnterButton();
        }
    };

    /**
     * listen for number pad button click
     */
    private View.OnClickListener numberPadListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClickHandler(v);
        }
    };
}
