package com.example.thegreatbudget.util;

import android.content.Context;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.example.thegreatbudget.R;

public class Common {
    private static final String TAG = "Common";
    
    // shared preferences
    public static final String SHARED_PREFERENCES = "com.example.thegreatbudget.util.shared.prefs";
    // don't ask again dialog
    public static final String IS_CHECKED = "checked";
    public static final String NOT_CHECKED = "not checked";
    // Calendar info
    public static final String RESET_DAY_EXTRA = "com.example.thegreatbudget.util.extra.reset.day";
    public static final int RESET_DAY_DEFAULT = 1;


    public static void themeSetter(Context context) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            context.setTheme(R.style.AppThemeDark);
        } else {
            context.setTheme(R.style.AppTheme);
        }
    }

    public static void themeSetterNoActionBar(Context context) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            context.setTheme(R.style.AppThemeNoActionDark);
        } else {
            context.setTheme(R.style.AppThemeNoAction);
        }
    }
}
