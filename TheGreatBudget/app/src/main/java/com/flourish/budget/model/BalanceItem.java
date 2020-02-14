package com.flourish.budget.model;

import android.support.annotation.NonNull;

public class BalanceItem {

    public static final String INCOME = "Income";
    public static final String EXPENSE = "Expense";

    private int mId;
    private String mName;
    private double mAmount;

    public BalanceItem(){}

    public BalanceItem(String name, double amount) {
        mName = name;
        mAmount = amount;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public double getAmount() {
        return mAmount;
    }

    public void setAmount(double mAmount) {
        this.mAmount = mAmount;
    }

    @NonNull
    @Override
    public String toString() {
        return getName() + " : " + getAmount();
    }
}
