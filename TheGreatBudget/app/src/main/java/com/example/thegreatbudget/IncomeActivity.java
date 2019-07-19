package com.example.thegreatbudget;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.lang.Math.pow;

public class IncomeActivity extends AppCompatActivity {
    private static final String TAG = "IncomeActivity";
    public static final int KEYPAD_INDEX = 11;
    public static final int INCOME_LIMIT = 10;

    private ImageView mDivider;
    private GridLayout mGrid;
    private TextView mIncomeText, mIncomeInput;
    private Button mEnter;
    private Button[] inputs = new Button[11];
    private ImageButton mDelete;
    private float mIncome;
    private List<Integer> numArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        Intent intent = getIntent();
        mIncome = intent.getFloatExtra(MainActivity.INCOME_EXTRA, 0f);

        for (int i = 0; i < KEYPAD_INDEX; i++) {
            String buttonID = "button" + i;
            if (i == 10) {
                buttonID = "buttonDecimal";
            }
            int resourceID = getResources().getIdentifier(buttonID, "id", getPackageName());
            inputs[i] = findViewById(resourceID);
            inputs[i].setOnClickListener(keypadListener);

        }
        mIncomeInput = findViewById(R.id.income_input);
        mDivider = findViewById(R.id.income_divider);
        mGrid = findViewById(R.id.keypadLayout);
        mDelete = findViewById(R.id.buttonDelete);
        mEnter = findViewById(R.id.buttonEnter);
        mIncomeText = findViewById(R.id.income_text);
        updateIncome(mIncome);
        mIncomeText.setOnClickListener(incomeTextClickListener);
        mDelete.setOnClickListener(keypadListener);

    }

    View.OnClickListener incomeTextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            moveIncomeView(Gravity.CENTER_HORIZONTAL);
            setButtonsVisibility(View.VISIBLE);
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

    private void buttonPicker(View v) {
        int num;
        switch (v.getId()) {
            case R.id.buttonDecimal:
                double test = 555555;
                Log.i(TAG, "buttonPicker: test " + test);
                break;
            case R.id.button0:
                num = Integer.parseInt(((Button) v).getText().toString());
                if (!numArray.isEmpty()) {
                    addtoNumArray(num);
                }
                Log.i(TAG, "buttonPicker: " + arrayToInt(numArray));
                break;
            case R.id.buttonDelete:
                if (!numArray.isEmpty()) {
                    numArray.remove(numArray.size() - 1);
                    Log.i(TAG, "buttonPicker: " + numArray.toString());
                }
                break;
            default:
                num = Integer.parseInt(((Button) v).getText().toString());
                addtoNumArray(num);
                Log.i(TAG, "buttonPicker: " + arrayToInt(numArray));
                break;
        }
    }

    private void addtoNumArray(int num) {
        if (numArray.size() < INCOME_LIMIT) {
            numArray.add(num);
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

    private void updateIncome(float value) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        String money = numberFormat.format(value);
        mIncomeText.setText(String.format(Locale.US, "Income: %s", money));
    }

    private void moveIncomeView(final int gravity) {
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) mIncomeText.getLayoutParams();
        mIncomeText.setGravity(gravity);
        mIncomeText.setLayoutParams(p);
    }

    private long arrayToInt(List<Integer> list){
        long total = 0;
        for (Integer i : list) { // assuming list is of type List<Integer>
            total = 10*total + i;
        }
        mIncomeInput.setText(String.valueOf(total));
        return total;
    }
}
