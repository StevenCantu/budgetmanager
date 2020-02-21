package com.flourish.budget.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import com.google.gson.Gson;

public class Expenses implements Parcelable {

    private static final String TAG = "Expenses";

    private float mAmount = 0f;
    private int mCategoryId = 5; //Category Misc
    private long mId;
    private String mTitle = "";
    private History mHistory = new History();

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

    public Expenses(long id, String title, float amount, int categoryId, String history) {
        mId = id;
        mTitle = title;
        mAmount = amount;
        mCategoryId = categoryId;
        Gson gson = new Gson();
        mHistory = gson.fromJson(history, History.class);
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public int getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(int categoryId) {
        this.mCategoryId = categoryId;
    }

    public long getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public float getAmount() {
        return mAmount;
    }

    public void setAmount(float amount) {
        this.mAmount = amount;
    }

    public History getHistory() {
        return mHistory;
    }

    public void setHistory(History history) {
        this.mHistory = history;
    }

    public String getHistoryJson() {
        Gson gson = new Gson();
        return gson.toJson(getHistory());
    }

    @NonNull
    @Override
    public String toString() {
        return "ID: \t" + getId() +
                "\ntitle: \t" + getTitle() +
                "\namount: \t" + getAmount();
    }

    protected Expenses(Parcel in) {
        mAmount = in.readFloat();
        mCategoryId = in.readInt();
        mId = in.readLong();
        mTitle = in.readString();
        mHistory = in.readParcelable(History.class.getClassLoader());
    }

    public static final Creator<Expenses> CREATOR = new Creator<Expenses>() {
        @Override
        public Expenses createFromParcel(Parcel in) {
            return new Expenses(in);
        }

        @Override
        public Expenses[] newArray(int size) {
            return new Expenses[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(mAmount);
        dest.writeInt(mCategoryId);
        dest.writeLong(mId);
        dest.writeString(mTitle);
        dest.writeParcelable(mHistory, flags);
    }
}
