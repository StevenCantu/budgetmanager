package com.example.thegreatbudget.database;

import android.provider.BaseColumns;

public final class BudgetContract {

    private BudgetContract() {
    }

    static class CategoriesTable implements BaseColumns {
        static final String TABLE_NAME = "budget_categories";
        static final String CATEGORY_NAME = "category_name";
    }

    public static class BudgetTable implements BaseColumns {
        static final String TABLE_NAME = "budget_expenses";
        public static final String EXPENSE = "expense";
        public static final String AMOUNT = "amount";
        public static final String HISTORY = "history";
        public static final String CATEGORY_ID = "category_id";
    }
}
