package com.example.thegreatbudget.model;

import android.support.annotation.NonNull;

public class Expenses {

    private static final String TAG = "Expenses";

    private float mAmount = 0f;
    private int mCategoryId = 5; //Category Misc
    private long mId;
    private String mTitle;

    public Expenses() {
    }

    public Expenses(String title, int categoryId) {
        mTitle = title;
        mCategoryId = categoryId;
    }

    public Expenses(long id, String title, float amount, int categoryId) {
        mId = id;
        mTitle = title;
        mAmount = amount;
        mCategoryId = categoryId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public int getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(int mCategoryId) {
        this.mCategoryId = mCategoryId;
    }

    public long getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public float getAmount() {
        return mAmount;
    }

    public void setAmount(float amount) {
        this.mAmount = amount;
    }

    @NonNull
    @Override
    public String toString() {
        return "ID: \t" + getId() +
                "\ntitle: \t" + getTitle() +
                "\namount: \t" + getAmount();
    }
}
