package com.example.thegreatbudget.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class History implements Parcelable {

    public static final String SEPARATOR = "-";
    @SerializedName("H")
    private List<HistoryItem> mHistoryItems;

    public History() {
        mHistoryItems = new ArrayList<>();
    }

    public void addItem(double item) {
        mHistoryItems.add(new HistoryItem(item));
    }

    public List<HistoryItem> getHistory() {
        return mHistoryItems;
    }

    public float getTotal() {
        float total = 0f;
        for (HistoryItem item : getHistory()) {
            total += item.getAmount();
        }
        return total;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (HistoryItem item : getHistory()) {
            builder.append(item.toString()).append("\n");
        }
        return builder.toString();
    }

    protected History(Parcel in) {
        mHistoryItems = in.createTypedArrayList(HistoryItem.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mHistoryItems);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<History> CREATOR = new Creator<History>() {
        @Override
        public History createFromParcel(Parcel in) {
            return new History(in);
        }

        @Override
        public History[] newArray(int size) {
            return new History[size];
        }
    };
}
