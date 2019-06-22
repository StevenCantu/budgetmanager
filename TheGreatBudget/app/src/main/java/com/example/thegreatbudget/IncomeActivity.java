package com.example.thegreatbudget;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.lang.Math.pow;

public class IncomeActivity extends AppCompatActivity {
    private static final String TAG = "IncomeActivity";

    private TextView mIncomeText;
    private Button mOne, mTwo, mThree, mFour, mFive, mSix, mSeven, mEight, mNine, mZero, mDecimal,
    mDelete;
    private float mIncome;
    private List<Integer> numArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        Intent intent = getIntent();
        mIncome = intent.getFloatExtra(MainActivity.INCOME_EXTRA, 0f);

        mOne = findViewById(R.id.button1);
        mTwo = findViewById(R.id.button2);
        mThree = findViewById(R.id.button3);
        mFour = findViewById(R.id.button4);
        mFive = findViewById(R.id.button5);
        mSix = findViewById(R.id.button6);
        mSeven = findViewById(R.id.button7);
        mEight = findViewById(R.id.button8);
        mNine = findViewById(R.id.button9);
        mDecimal = findViewById(R.id.buttonDecimal);
        mZero = findViewById(R.id.button0);
        mDelete = findViewById(R.id.buttonDelete);
        mIncomeText = findViewById(R.id.income_text);
        updateIncome(mIncome);
        mIncomeText.setOnClickListener(incomeTextClickListener);
        mOne.setOnClickListener(oneListener);
        mTwo.setOnClickListener(oneListener);
        mThree.setOnClickListener(oneListener);
        mFour.setOnClickListener(oneListener);
        mFive.setOnClickListener(oneListener);
        mSix.setOnClickListener(oneListener);
        mSeven.setOnClickListener(oneListener);
        mEight.setOnClickListener(oneListener);
        mNine.setOnClickListener(oneListener);
        mDecimal.setOnClickListener(oneListener);
        mZero.setOnClickListener(oneListener);
        mDelete.setOnClickListener(oneListener);

    }

    View.OnClickListener incomeTextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            moveIncomeView(Gravity.CENTER_HORIZONTAL);
            setButtonsVisibility(View.VISIBLE);
        }
    };

    View.OnClickListener oneListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            mIncome += Float.parseFloat(mOne.getText().toString());
//            updateIncome(mIncome);

//            setButtonsVisibility(View.GONE);
//            moveIncomeView(Gravity.CENTER);
            buttonPicker(v);
        }
    };

    private void buttonPicker(View v){
        switch (v.getId()){
            case R.id.button1:
                int num = Integer.parseInt(mOne.getText().toString());
                numArray.add(num);
                Log.i(TAG, "buttonPicker: 1" + numArray);
                break;
            case R.id.button2:
                num = Integer.parseInt(mTwo.getText().toString());
                numArray.add(num);
                Log.i(TAG, "buttonPicker: 2"+ numArray);
                break;
            case R.id.button3:
                num = Integer.parseInt(mThree.getText().toString());
                numArray.add(num);
                Log.i(TAG, "buttonPicker: 3"+ numArray);
                break;
            case R.id.button4:
                num = Integer.parseInt(mFour.getText().toString());
                numArray.add(num);
                Log.i(TAG, "buttonPicker: 4"+ numArray);
                break;
            case R.id.button5:
                num = Integer.parseInt(mFive.getText().toString());
                numArray.add(num);
                Log.i(TAG, "buttonPicker: 5"+ numArray);
                break;
            case R.id.button6:
                num = Integer.parseInt(mSix.getText().toString());
                numArray.add(num);
                Log.i(TAG, "buttonPicker: 6"+ numArray);
                break;
            case R.id.button7:
                num = Integer.parseInt(mSeven.getText().toString());
                numArray.add(num);
                Log.i(TAG, "buttonPicker: 7"+ numArray);
                break;
            case R.id.button8:
                num = Integer.parseInt(mEight.getText().toString());
                numArray.add(num);
                Log.i(TAG, "buttonPicker: 8"+ numArray);
                break;
            case R.id.button9:
                num = Integer.parseInt(mNine.getText().toString());
                numArray.add(num);
                Log.i(TAG, "buttonPicker: 9"+ numArray);
                break;
            case R.id.buttonDecimal:
                double test = parseArray(numArray);
                Log.i(TAG, "buttonPicker: test " + test);
                break;
            case R.id.button0:
                num = Integer.parseInt(mZero.getText().toString());
                if (!numArray.isEmpty()) {
                    numArray.add(num);
                }
                Log.i(TAG, "buttonPicker: 0"+ numArray);
                break;
            case R.id.buttonDelete:
                if(!numArray.isEmpty()) {
                    numArray.remove(numArray.size() - 1);
                }
                break;
            default:
                break;
        }
    }

    private void setButtonsVisibility(final int visibility){
        mOne.setVisibility(visibility);
        mTwo.setVisibility(visibility);
        mThree.setVisibility(visibility);
        mFour.setVisibility(visibility);
        mFive.setVisibility(visibility);
        mSix.setVisibility(visibility);
        mSeven.setVisibility(visibility);
        mEight.setVisibility(visibility);
        mNine.setVisibility(visibility);
        mDecimal.setVisibility(visibility);
        mZero.setVisibility(visibility);
        mDelete.setVisibility(visibility);

    }

    private void updateIncome(float value){
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        String money = numberFormat.format(value);
        mIncomeText.setText(String.format(Locale.US, "Income: %s", money));
    }

    private double parseArray(List<Integer> nums){
        double result = 0;
        for(int i = 0; i < nums.size(); i++){
            int power = nums.size() - (i +1);
            result += nums.get(i) * pow(10, power);
        }
        return result;
    }


    private void moveIncomeView(final int gravity){
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams)mIncomeText.getLayoutParams();
        mIncomeText.setGravity(gravity);
        mIncomeText.setLayoutParams(p);
    }
}
