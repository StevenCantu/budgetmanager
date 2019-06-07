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
    private Button mOne;
    private float mIncome;
    private boolean isKeyPadUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        Intent intent = getIntent();
        mIncome = intent.getFloatExtra(MainActivity.INCOME_EXTRA, 0f);

        mOne = findViewById(R.id.button1);
        mIncomeText = findViewById(R.id.income_text);
        updateIncome(mIncome);
        mIncomeText.setOnClickListener(incomeTextClickListener);
        mOne.setOnClickListener(oneListener);
    }

    View.OnClickListener incomeTextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            moveIncomeView(Gravity.CENTER_HORIZONTAL);
            mOne.setVisibility(View.VISIBLE);
        }
    };

    View.OnClickListener oneListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mIncome += Float.parseFloat(mOne.getText().toString());
            updateIncome(mIncome);
            mOne.setVisibility(View.GONE);
            moveIncomeView(Gravity.CENTER);
        }
    };

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
