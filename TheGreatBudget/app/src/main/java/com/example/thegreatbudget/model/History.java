package com.example.thegreatbudget.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class History {

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
}
