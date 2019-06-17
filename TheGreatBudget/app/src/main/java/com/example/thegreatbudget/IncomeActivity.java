package com.example.thegreatbudget;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

public class IncomeActivity extends AppCompatActivity {
    private static final String TAG = "IncomeActivity";

    private TextView mIncomeText;
    private Button mOne, mTwo, mThree, mFour, mFive, mSix, mSeven, mEight, mNine, mZero, mDelete,
    mEnter;
    private float mIncome;
    private boolean isKeyPadUp;

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
        mDelete = findViewById(R.id.buttonDelete);
        mZero = findViewById(R.id.button0);
        mEnter = findViewById(R.id.buttonEnter);
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
        mDelete.setOnClickListener(oneListener);
        mZero.setOnClickListener(oneListener);
        mEnter.setOnClickListener(oneListener);

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
                mIncome += Float.parseFloat(mOne.getText().toString());
                updateIncome(mIncome);
                Log.i(TAG, "buttonPicker: 1");
                break;
            case R.id.button2:
                mIncome += Float.parseFloat(mTwo.getText().toString());
                updateIncome(mIncome);
                Log.i(TAG, "buttonPicker: 2");
                break;
            case R.id.button3:
                mIncome += Float.parseFloat(mTwo.getText().toString());
                updateIncome(mIncome);
                Log.i(TAG, "buttonPicker: 3");
                break;
            case R.id.button4:
                mIncome += Float.parseFloat(mTwo.getText().toString());
                updateIncome(mIncome);
                Log.i(TAG, "buttonPicker: 4");
                break;
            case R.id.button5:
                mIncome += Float.parseFloat(mTwo.getText().toString());
                updateIncome(mIncome);
                Log.i(TAG, "buttonPicker: 5");
                break;
            case R.id.button6:
                mIncome += Float.parseFloat(mTwo.getText().toString());
                updateIncome(mIncome);
                Log.i(TAG, "buttonPicker: 6");
                break;
            case R.id.button7:
                mIncome += Float.parseFloat(mTwo.getText().toString());
                updateIncome(mIncome);
                Log.i(TAG, "buttonPicker: 7");
                break;
            case R.id.button8:
                mIncome += Float.parseFloat(mTwo.getText().toString());
                updateIncome(mIncome);
                Log.i(TAG, "buttonPicker: 8");
                break;
            case R.id.button9:
                mIncome += Float.parseFloat(mTwo.getText().toString());
                updateIncome(mIncome);
                Log.i(TAG, "buttonPicker: 9");
                break;
            case R.id.buttonDelete:
                break;
            case R.id.button0:
                mIncome += Float.parseFloat(mTwo.getText().toString());
                updateIncome(mIncome);
                Log.i(TAG, "buttonPicker: 0");
                break;
            case R.id.buttonEnter:
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
        mDelete.setVisibility(visibility);
        mZero.setVisibility(visibility);
        mEnter.setVisibility(visibility);

    }

    private void updateIncome(float value){
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        String money = numberFormat.format(value);
        mIncomeText.setText(String.format(Locale.US, "Income: %s", money));
    }

    private void moveIncomeView(final int gravity){
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams)mIncomeText.getLayoutParams();
        mIncomeText.setGravity(gravity);
        mIncomeText.setLayoutParams(p);
    }
}
