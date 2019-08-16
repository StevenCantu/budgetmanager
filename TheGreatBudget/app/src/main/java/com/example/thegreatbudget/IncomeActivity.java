package com.example.thegreatbudget;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;

public class IncomeActivity extends AppCompatActivity {
    private static final String TAG = "IncomeActivity";
    public static final int KEYPAD_INDEX = 11;
    public static final int INCOME_LIMIT = 10;
    public static final int MAX_SIZE = 10;

    private FloatingActionButton mAddButton;
    private ImageView mDivider;
    private ImageButton mUndoButton;
    private ImageButton mEditButton;
    private GridLayout mGrid;
    private TextView mIncomeInput;
    private TextView mIncomeText;
    private Button mEnter;
    private Button[] inputs = new Button[11];
    private ImageButton mDelete;
    private Handler handler;

    private Deque<Double> mTemps = new ArrayDeque<>();
    private double mIncome;
    private double mIncomeTemp;
    private String mDecimalInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        handler = new Handler();

        Intent intent = getIntent();
        mIncome = intent.getFloatExtra(MainActivity.INCOME_EXTRA, 0f);
        mDecimalInput = "";

        for (int i = 0; i < KEYPAD_INDEX; i++) {
            String buttonID = "button" + i;
            if (i == 10) {
                buttonID = "buttonDecimal";
            }
            int resourceID = getResources().getIdentifier(buttonID, "id", getPackageName());
            inputs[i] = findViewById(resourceID);
            inputs[i].setOnClickListener(keypadListener);

        }
        mAddButton = findViewById(R.id.add_input);
        mUndoButton = findViewById(R.id.undo_income);
        mEditButton = findViewById(R.id.edit_income);
        mIncomeInput = findViewById(R.id.income_input);
        mDivider = findViewById(R.id.income_divider);
        mGrid = findViewById(R.id.keypadLayout);
        mDelete = findViewById(R.id.buttonDelete);
        mEnter = findViewById(R.id.buttonEnter);
        mEnter.setOnClickListener(enterButtonListener);
        mIncomeText = findViewById(R.id.income_text);
        updateIncome(mIncome);
        mAddButton.setOnClickListener(incomeTextClickListener);
//        mIncomeText.setOnClickListener(incomeTextClickListener);
        mDelete.setOnClickListener(keypadListener);
        mUndoButton.setOnClickListener(undoListener);

    }

    View.OnClickListener undoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!mTemps.isEmpty()) {
                mIncome = mTemps.removeLast();
                updateIncome(mIncome);
            }
        }
    };

    View.OnClickListener enterButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!mDecimalInput.isEmpty()) {
                addToDeque(mIncome);
                Log.d("DEBUG", "onClick: " + mTemps);
                mIncomeTemp = mIncome;
                mIncome += Double.parseDouble(mDecimalInput);
                updateIncome(mIncome);
                mDecimalInput = "";
                mIncomeInput.setText("");
            }
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    moveIncomeView(Gravity.CENTER);
                    setButtonsVisibility(View.INVISIBLE);
                    mUndoButton.setVisibility(View.VISIBLE);
                    mEditButton.setVisibility(View.VISIBLE);
                    mAddButton.show();
                    mDecimalInput = "";
                    mIncomeInput.setText("");
                }
            }, 350);
        }
    };

    /**
     *
     */
    View.OnClickListener incomeTextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            moveIncomeView(Gravity.CENTER_HORIZONTAL);
            setButtonsVisibility(View.VISIBLE);
            mUndoButton.setVisibility(View.INVISIBLE);
            mEditButton.setVisibility(View.INVISIBLE);
            mAddButton.hide();
        }
    };

    View.OnClickListener keypadListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            mIncome += Float.parseFloat(mOne.getText().toString());
//            updateIncome(mIncome);

//            setButtonsVisibility(View.GONE);
//            moveIncomeView(Gravity.CENTER);
            buttonPicker(v);
        }
    };

    private void addToDeque(double item){
        if (mTemps.size() < MAX_SIZE) {
            mTemps.add(item);
        } else {
            mTemps.removeFirst();
            mTemps.add(item);
        }
    }

    private void buttonPicker(View v) {

        switch (v.getId()) {
            case R.id.buttonDecimal:
                if (!mDecimalInput.contains(".")) {
                    double test = 555555;
                    String s = ((Button) v).getText().toString();
                    addToDecimal(s);
                    Log.i(TAG, "buttonPicker: test " + test);
                }
                break;
            case R.id.button0:
                String s = ((Button) v).getText().toString();
                addToDecimal(s);
                break;
            case R.id.buttonDelete:
                deleteFromDecimal();
                break;
            default:
                s = ((Button) v).getText().toString();
                addToDecimal(s);
                break;
        }
    }

    private void addToDecimal(String s) {
        if (s.equals(".")) {
            mDecimalInput = mDecimalInput + s;
        } else if (mDecimalInput.contains(".")) {
            int index = mDecimalInput.indexOf(".");
            if (mDecimalInput.length() <= index + 2) {
                if (mDecimalInput.length() == index + 2) {
                    if (!s.equals("0")) {
                        mDecimalInput = mDecimalInput + s;
                    }
                } else {
                    mDecimalInput = mDecimalInput + s;
                }

            }
        } else {
            if (mDecimalInput.length() < INCOME_LIMIT) {
                mDecimalInput = mDecimalInput + s;
            }
        }

        updateInputBox(mDecimalInput);
    }

    private void deleteFromDecimal() {
        if (!mDecimalInput.isEmpty()) {
            if (mDecimalInput.contains(".") && mDecimalInput.length() - 1 == mDecimalInput.indexOf(".")) {
                mDecimalInput = mDecimalInput.substring(0, mDecimalInput.length() - 2);
            } else {
                mDecimalInput = mDecimalInput.substring(0, mDecimalInput.length() - 1);
            }
            updateInputBox(mDecimalInput);
        }
    }


    private void setButtonsVisibility(final int visibility) {
        for (int i = 0; i < KEYPAD_INDEX; i++) {
            inputs[i].setVisibility(visibility);
        }
        mIncomeInput.setVisibility(visibility);
        mDivider.setVisibility(visibility);
        mEnter.setVisibility(visibility);
        mGrid.setVisibility(visibility);
        mDelete.setVisibility(visibility);

    }

    private void updateInputBox(String s) {
        if (s.isEmpty()) {
            s = "0";
        }
        double currency = Double.parseDouble(s);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        String money = numberFormat.format(currency);
        mIncomeInput.setText(money);
    }

    private void updateIncome(double value) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        String money = numberFormat.format(value);
        mIncomeText.setText(String.format(Locale.US, "Income: %s", money));
    }

    private void moveIncomeView(final int gravity) {
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) mIncomeText.getLayoutParams();
        mIncomeText.setGravity(gravity);
        mIncomeText.setLayoutParams(p);
    }

    // TODO: 8/15/2019 add edit functionality
    // TODO: 8/15/2019 make input bigger and better
    // TODO: 8/15/2019 set default colors
    // TODO: 8/15/2019 add dialog for undo 
    // TODO: 8/15/2019 return income to main activity 
}
