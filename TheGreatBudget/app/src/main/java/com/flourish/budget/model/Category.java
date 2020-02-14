package com.flourish.budget.model;

import android.support.annotation.NonNull;

public class Category {

    public static final int HOUSING = 1;
    public static final int INSURANCE = 2;
    public static final int PERSONAL = 3;
    public static final int WANTS = 4;
    public static final int MISC = 5;


    private int mId;
    private String mCategoryName;

    public Category() {
    }

    public Category(String categoryName) {
        this.mCategoryName = categoryName;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public void setCategoryName(String categoryName) {
        this.mCategoryName = categoryName;
    }

    @NonNull
    @Override
    public String toString() {
        return getCategoryName();
    }
}
