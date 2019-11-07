package com.example.thegreatbudget.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HistoryItem {

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
}
