package com.example.thegreatbudget.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.thegreatbudget.database.BudgetContract.BudgetTable;
import com.example.thegreatbudget.database.BudgetContract.CategoriesTable;
import com.example.thegreatbudget.database.BudgetContract.BalanceItemTable;
import com.example.thegreatbudget.database.BudgetContract.StatementTable;
import com.example.thegreatbudget.model.BalanceItem;
import com.example.thegreatbudget.model.Category;
import com.example.thegreatbudget.model.Expenses;
import com.example.thegreatbudget.model.History;

public class BudgetDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    private static final String DATABASE_NAME = "TheGreatBudget.db";
    private static final int DATABASE_VERSION = 1;

    private static BudgetDbHelper mInstance;

    private SQLiteDatabase mDatabase;

    private BudgetDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized BudgetDbHelper getInstance(@NonNull Context context) {

        if (mInstance == null) {
            mInstance = new BudgetDbHelper(context.getApplicationContext());
        }

        return mInstance;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mDatabase = db;

        final String CREATE_CATEGORY_TABLE =
                "CREATE TABLE " + CategoriesTable.TABLE_NAME + " ( " +
                        CategoriesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        CategoriesTable.CATEGORY_NAME + " TEXT " +
                        ")";

        final String CREATE_BUDGET_TABLE =
                "CREATE TABLE " + BudgetTable.TABLE_NAME + " ( " +
                        BudgetTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        BudgetTable.EXPENSE + " TEXT, " +
                        BudgetTable.AMOUNT + " REAL, " +
                        BudgetTable.HISTORY + " TEXT," +
                        BudgetTable.CATEGORY_ID + " INTEGER, " +
                        "FOREIGN KEY (" + BudgetTable.CATEGORY_ID + ") REFERENCES " +
                        CategoriesTable.TABLE_NAME + "(" + CategoriesTable._ID + ") ON DELETE CASCADE" +
                        ")";

        final String CREATE_STATEMENT_INFO_TABLE =
                "CREATE TABLE " + BalanceItemTable.TABLE_NAME + " ( " +
                        BalanceItemTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        BalanceItemTable.ITEM_NAME + " TEXT, " +
                        BalanceItemTable.AMOUNT + " REAL " +
                        ")";

        final String CREATE_STATEMENT_TABLE =
                "CREATE TABLE " + StatementTable.TABLE_NAME + " ( " +
                        StatementTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        StatementTable.EXPENSE + " TEXT, " +
                        StatementTable.AMOUNT + " REAL, " +
                        StatementTable.HISTORY + " TEXT," +
                        StatementTable.CATEGORY_ID + " INTEGER, " +
                        "FOREIGN KEY (" + StatementTable.CATEGORY_ID + ") REFERENCES " +
                        CategoriesTable.TABLE_NAME + "(" + CategoriesTable._ID + ") ON DELETE CASCADE" +
                        ")";

        mDatabase.execSQL(CREATE_CATEGORY_TABLE);
        mDatabase.execSQL(CREATE_BUDGET_TABLE);
        mDatabase.execSQL(CREATE_STATEMENT_INFO_TABLE);
        mDatabase.execSQL(CREATE_STATEMENT_TABLE);
        fillCategoryTable();
        fillBudgetTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mDatabase.execSQL("DROP TABLE IF EXISTS " + CategoriesTable.TABLE_NAME);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + BudgetTable.TABLE_NAME);
        onCreate(mDatabase);
    }

    public void addExpense(Expenses expense) {
        mDatabase = getWritableDatabase();
        insertToBudgetTable(expense);
    }

    public void deleteExpense(long id) {
        mDatabase = getWritableDatabase();
        deleteFromBudgetTable(id);
    }

    public void editExpense(Expenses expense) {
        mDatabase = getWritableDatabase();
        editFromBudgetTable(expense);
    }

    public Cursor getExpensesCursor(int category) {
        mDatabase = getReadableDatabase();

        final String selection = BudgetTable.CATEGORY_ID + " = ?";
        final String[] selectionArgs = new String[]{String.valueOf(category)};

        return mDatabase.query(
                BudgetTable.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                BudgetTable.EXPENSE + " ASC"
        );
    }

    public Cursor getBalanceCursor() {
        mDatabase = getReadableDatabase();

        return mDatabase.query(
                BalanceItemTable.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                BalanceItemTable._ID + " ASC"
        );
    }

    public Cursor getStatementCursor() {
        mDatabase = getReadableDatabase();

        return mDatabase.query(
                StatementTable.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                StatementTable._ID + " ASC"
        );
    }

    public double totalExpenses() {
        double total = 0;
        mDatabase = getReadableDatabase();

        final String SUM_AMOUNT = "SELECT SUM(" + BudgetTable.AMOUNT + ") FROM " + BudgetTable.TABLE_NAME;

        Cursor cursor = mDatabase.rawQuery(SUM_AMOUNT, null);
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    public void addBalanceItem(BalanceItem item) {
        mDatabase = getWritableDatabase();
        insertBalanceItem(item);
    }

    public void resetBD() {
        mDatabase = getWritableDatabase();
        resetBalanceItemTable();
        resetStatementTable();

        try (Cursor cursor = mDatabase.query(
                BudgetTable.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                BudgetTable.CATEGORY_ID + " ASC"
        )) {
            while (cursor.moveToNext()) {
                final long id = cursor.getLong(cursor.getColumnIndex(BudgetTable._ID));
                final String expense = cursor.getString(cursor.getColumnIndex(BudgetTable.EXPENSE));
                final float amount = cursor.getFloat(cursor.getColumnIndex(BudgetTable.AMOUNT));
                final int categoryId = cursor.getInt(cursor.getColumnIndex(BudgetTable.CATEGORY_ID));
                final String historyJson = cursor.getString(cursor.getColumnIndex(BudgetTable.HISTORY));
                Expenses expenses = new Expenses(id, expense, amount, categoryId, historyJson);

                if (amount > 0f) {
                    insertToStatementTable(expenses);
                }

                // TODO: 12/30/2019 uncomment 
//                if (categoryId == Category.MISC) {
//                    deleteFromBudgetTable(id);
//                } else {
//                    expenses.setAmount(0f);
//                    expenses.setHistory(new History());
//                    editFromBudgetTable(expenses);
//                }
            }
        }
    }

    private void resetBalanceItemTable() {
        try (Cursor cursor = mDatabase.query(
                BalanceItemTable.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                BalanceItemTable._ID + " DESC"
        )) {
            while (cursor.moveToNext()) {
                final long id = cursor.getLong(cursor.getColumnIndex(BalanceItemTable._ID));
                deleteFromBalanceItemTable(id);
            }
        }
    }

    private void resetStatementTable() {
        try (Cursor cursor = mDatabase.query(
                StatementTable.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                StatementTable._ID + " DESC"
        )) {
            while (cursor.moveToNext()) {
                final long id = cursor.getLong(cursor.getColumnIndex(StatementTable._ID));
                deleteFromStatementTable(id);
            }
        }
    }

    private void insertBalanceItem(BalanceItem item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BalanceItemTable.ITEM_NAME, item.getName());
        contentValues.put(BalanceItemTable.AMOUNT, item.getAmount());
        mDatabase.insert(BalanceItemTable.TABLE_NAME, null, contentValues);
    }

    private void insertToStatementTable(Expenses expense) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(StatementTable.EXPENSE, expense.getTitle());
        contentValues.put(StatementTable.AMOUNT, expense.getAmount());
        contentValues.put(StatementTable.HISTORY, expense.getHistoryJson());
        contentValues.put(StatementTable.CATEGORY_ID, expense.getCategoryId());
        mDatabase.insert(StatementTable.TABLE_NAME, null, contentValues);
    }

    private void insertCategory(Category category) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CategoriesTable.CATEGORY_NAME, category.getCategoryName());
        mDatabase.insert(CategoriesTable.TABLE_NAME, null, contentValues);
    }

    private void insertToBudgetTable(Expenses expense) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BudgetTable.EXPENSE, expense.getTitle());
        contentValues.put(BudgetTable.AMOUNT, expense.getAmount());
        contentValues.put(BudgetTable.HISTORY, expense.getHistoryJson());
        contentValues.put(BudgetTable.CATEGORY_ID, expense.getCategoryId());
        mDatabase.insert(BudgetTable.TABLE_NAME, null, contentValues);
    }

    private void fillCategoryTable() {
        Category housing = new Category("housing");
        Category insurance = new Category("insurance");
        Category personal = new Category("personal");
        Category wants = new Category("wants");
        Category misc = new Category("misc");
        insertCategory(housing);
        insertCategory(insurance);
        insertCategory(personal);
        insertCategory(wants);
        insertCategory(misc);
    }

    private void fillBudgetTable() {
        insertToBudgetTable(new Expenses("Rent/Mortgage", Category.HOUSING));
        insertToBudgetTable(new Expenses("Electricity", Category.HOUSING));
        insertToBudgetTable(new Expenses("Gas", Category.HOUSING));
        insertToBudgetTable(new Expenses("Internet/Cable", Category.HOUSING));
        insertToBudgetTable(new Expenses("Water/Sewage", Category.HOUSING));

        insertToBudgetTable(new Expenses("Car loan", Category.PERSONAL));
        insertToBudgetTable(new Expenses("Groceries", Category.PERSONAL));
        insertToBudgetTable(new Expenses("Toiletries", Category.PERSONAL));
        insertToBudgetTable(new Expenses("Gasoline/Transportation", Category.PERSONAL));
        insertToBudgetTable(new Expenses("Cell Phone", Category.PERSONAL));

        insertToBudgetTable(new Expenses("Auto", Category.INSURANCE));
        insertToBudgetTable(new Expenses("Health", Category.INSURANCE));
        insertToBudgetTable(new Expenses("Life", Category.INSURANCE));
        insertToBudgetTable(new Expenses("Renters/Home Owners", Category.INSURANCE));

        insertToBudgetTable(new Expenses("Clothes", Category.WANTS));
        insertToBudgetTable(new Expenses("Dining Out", Category.WANTS));
        insertToBudgetTable(new Expenses("Events", Category.WANTS));
        insertToBudgetTable(new Expenses("Gym/Clubs", Category.WANTS));
        insertToBudgetTable(new Expenses("Travel", Category.WANTS));
        insertToBudgetTable(new Expenses("Home Decor", Category.WANTS));
        insertToBudgetTable(new Expenses("Streaming Services", Category.WANTS));
    }

    private void deleteFromBudgetTable(long id) {
        mDatabase.delete(BudgetTable.TABLE_NAME, BudgetTable._ID + "=" + id, null);
    }

    private void deleteFromBalanceItemTable(long id) {
        mDatabase.delete(BalanceItemTable.TABLE_NAME, BalanceItemTable._ID + "=" + id, null);
    }

    private void deleteFromStatementTable(long id) {
        mDatabase.delete(StatementTable.TABLE_NAME, StatementTable._ID + "=" + id, null);
    }

    private void editFromBudgetTable(Expenses expense) {
        ContentValues values = new ContentValues();

        values.put(BudgetTable.AMOUNT, expense.getAmount());
        values.put(BudgetTable.HISTORY, expense.getHistoryJson());

        mDatabase.update(
                BudgetTable.TABLE_NAME,
                values,
                BudgetTable._ID + "=" + expense.getId(),
                null
        );
    }
}
