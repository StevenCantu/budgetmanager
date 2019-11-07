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
//        Date date = Calendar.getInstance().getTime();
//        String stringDate = SimpleDateFormat.getDateTimeInstance().format(date);
//        mHistoryItems.add(item + SEPARATOR + stringDate);
        mHistoryItems.add(new HistoryItem(item));
    }

    public List<HistoryItem> getHistory() {
        return mHistoryItems;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (HistoryItem item : mHistoryItems) {
            builder.append(item.toString()).append("\t");
        }
        return builder.toString();
    }

    protected History(Parcel in) {
        mHistoryItems = in.createTypedArrayList(HistoryItem.CREATOR);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mHistoryItems);
    }
}
