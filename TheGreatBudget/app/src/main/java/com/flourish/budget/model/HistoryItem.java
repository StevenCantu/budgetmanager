package com.flourish.budget.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

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
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        return numberFormat.format(getAmount());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HistoryItem)) return false;
        HistoryItem item = (HistoryItem) o;
        return Double.compare(item.mAmount, mAmount) == 0 &&
                mDate.equals(item.mDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mAmount, mDate);
    }
}
