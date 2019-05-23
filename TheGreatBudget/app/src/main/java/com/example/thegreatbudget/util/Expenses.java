package com.example.thegreatbudget.util;

import android.util.Log;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Expenses {
    private static final String TAG = "Expenses";

    private String mTitle;
    private String mExpense;
    private NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);

    public Expenses(String title){
        mTitle = title;
        mExpense = "Enter amount";
    }

    public String getTitle() {
        return mTitle;
    }

    public String getExpense() {
        if(isNumeric(mExpense)){
            mExpense = numberFormat.format(Float.parseFloat(mExpense));
            return mExpense;
        } else {
            return mExpense;
        }
    }

    public void setExpense(String expense) {
        mExpense = expense;
    }

    public float sumExpenses(List<Expenses> expenses){
        Log.i(TAG, "sumExpenses: ");
        float sum = 0f;
        for(Expenses e: expenses){
            String expense = e.getExpense();
            Log.i(TAG, "sumExpenses: " + expense);
            Number item = 0;
            try {
                item = numberFormat.parse(expense);
            } catch (ParseException e1) {
                Log.i(TAG, "sumExpenses: " + e);
            }
            sum += item.floatValue();
        }
        return sum;
    }

    public List<String> getTitles(List<Expenses> expenses){
        List<String> result = new ArrayList<>();
        for(Expenses e : expenses){
            result.add(e.getTitle());
        }
        return result;
    }

    public List<String> getExpenses(List<Expenses> expenses){
        List<String> result = new ArrayList<>();
        for(Expenses e : expenses){
            result.add(e.getExpense());
        }
        return result;
    }

    private boolean isNumeric(String s){
        try{
            float f = Float.parseFloat(s);
        } catch (NumberFormatException | NullPointerException e){
            return false;
        }
        return true;
    }
}
