package com.example.thegreatbudget.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HistoryItem implements Parcelable {

    @SerializedName("a")
    private double mAmount;
    @SerializedName("d")
    private String mDate;

    public HistoryItem(double amount) {
        Date date = Calendar.getInstance().getTime();
        mDate = SimpleDateFormat.getDateTimeInstance().format(date);
        mAmount = amount;
    }

    public double getAmount() {
        return mAmount;
    }

    public String getDate() {
        return mDate;
    }

    @NonNull
    @Override
    public String toString() {
        return getAmount() + " - " + getDate();
    }

    protected HistoryItem(Parcel in) {
        mAmount = in.readDouble();
        mDate = in.readString();
    }

    public static final Creator<HistoryItem> CREATOR = new Creator<HistoryItem>() {
        @Override
        public HistoryItem createFromParcel(Parcel in) {
            return new HistoryItem(in);
        }

        @Override
        public HistoryItem[] newArray(int size) {
            return new HistoryItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(mAmount);
        dest.writeString(mDate);
    }
}
