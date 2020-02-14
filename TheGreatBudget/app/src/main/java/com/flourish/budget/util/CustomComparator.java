package com.flourish.budget.util;

import com.flourish.budget.model.HistoryItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class CustomComparator implements Comparator<HistoryItem> {
    @Override
    public int compare(HistoryItem o1, HistoryItem o2) {
        DateFormat format = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.US);
        Date d1 = new Date();
        Date d2 = new Date();
        try {
            d1 = format.parse(o1.getDate());
            d2 = format.parse(o2.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d1.compareTo(d2);
    }
}
