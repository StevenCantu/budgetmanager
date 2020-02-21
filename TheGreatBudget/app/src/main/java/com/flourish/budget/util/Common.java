package com.flourish.budget.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.flourish.budget.R;

public class Common {
    private static final String TAG = "Common";
    
    // shared preferences
    public static final String SHARED_PREFERENCES = "com.example.thegreatbudget.util.shared.prefs";
    // don't ask again dialog
    public static final String IS_CHECKED = "checked";
    public static final String NOT_CHECKED = "not checked";
    // Calendar info
    public static final String CALCULATED_RESET_DAY_EXTRA = "com.example.thegreatbudget.util.extra.calculated.reset.day";
    public static final String RESET_DAY_EXTRA = "com.example.thegreatbudget.util.extra.reset.day";
    public static final String RESET_ONCE = "com.example.thegreatbudget.util.extra.reset.once";
    public static final int RESET_DAY_DEFAULT = 1;
    // Dark mode info
    public static final String DARK_MODE_EXTRA = "com.example.thegreatbudget.util.extra.dark.mode";
    // permission requests
    public static final int WRITE_REQUEST_CODE = 1;

    public static void themeSetterNoActionBar(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        boolean darkModeActive = sp.getBoolean(DARK_MODE_EXTRA, true);
        if (darkModeActive) {
            context.setTheme(R.style.AppThemeNoActionDark);
        } else {
            context.setTheme(R.style.AppThemeNoAction);
        }
    }
}
