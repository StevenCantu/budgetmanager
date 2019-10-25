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
import com.example.thegreatbudget.model.Category;
import com.example.thegreatbudget.model.Expenses;

import java.util.ArrayList;

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
                BudgetTable.CATEGORY_ID + " INTEGER, " +
                "FOREIGN KEY (" + BudgetTable.CATEGORY_ID + ") REFERENCES " +
                CategoriesTable.TABLE_NAME + "(" + CategoriesTable._ID + ") ON DELETE CASCADE" +
                ")";

        mDatabase.execSQL(CREATE_CATEGORY_TABLE);
        mDatabase.execSQL(CREATE_BUDGET_TABLE);
        fillCategoryTable();
        fillBudgetTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mDatabase.execSQL("DROP TABLE IF EXISTS " + CategoriesTable.TABLE_NAME);
        mDatabase.execSQL("DROP TABLE IF EXISTS " + BudgetTable.TABLE_NAME);
        onCreate(mDatabase);
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

    private void insertCategory(Category category) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CategoriesTable.CATEGORY_NAME, category.getCategoryName());
        mDatabase.insert(CategoriesTable.TABLE_NAME, null, contentValues);
    }

    private void fillBudgetTable() {
        Expenses rent = new Expenses("Rent/Mortgage", Category.HOUSING);
        Expenses electricity = new Expenses("Electricity", Category.HOUSING);
        Expenses gas = new Expenses("Gas", Category.HOUSING);
        Expenses cable = new Expenses("Internet/Cable", Category.HOUSING);
        Expenses water = new Expenses("Water/Sewage", Category.HOUSING);
        insertExpense(rent);
        insertExpense(electricity);
        insertExpense(gas);
        insertExpense(cable);
        insertExpense(water);

        Expenses car = new Expenses("Car loan", Category.PERSONAL);
        Expenses groceries = new Expenses("Groceries", Category.PERSONAL);
        Expenses toiletries = new Expenses("Toiletries", Category.PERSONAL);
        Expenses transportation = new Expenses("Gasoline/Transportation", Category.PERSONAL);
        Expenses cell = new Expenses("Cell Phone", Category.PERSONAL);
        insertExpense(car);
        insertExpense(groceries);
        insertExpense(toiletries);
        insertExpense(transportation);
        insertExpense(cell);

        Expenses auto = new Expenses("Auto", Category.INSURANCE);
        Expenses health = new Expenses("Health", Category.INSURANCE);
        Expenses life = new Expenses("Life", Category.INSURANCE);
        Expenses home = new Expenses("Renters/Home Owners", Category.INSURANCE);
        insertExpense(auto);
        insertExpense(health);
        insertExpense(life);
        insertExpense(home);

        Expenses clothes = new Expenses("Clothes", Category.WANTS);
        Expenses dinning = new Expenses("Dining Out", Category.WANTS);
        Expenses events = new Expenses("Events", Category.WANTS);
        Expenses gym = new Expenses("Gym/Clubs", Category.WANTS);
        Expenses travel = new Expenses("Travel", Category.WANTS);
        Expenses decor = new Expenses("Home Decor", Category.WANTS);
        Expenses streaming = new Expenses("Streaming Services", Category.WANTS);
        insertExpense(clothes);
        insertExpense(dinning);
        insertExpense(events);
        insertExpense(gym);
        insertExpense(travel);
        insertExpense(decor);
        insertExpense(streaming);
    }

    private void insertExpense(Expenses expense) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BudgetTable.EXPENSE, expense.getTitle());
        contentValues.put(BudgetTable.AMOUNT, expense.getAmount());
        contentValues.put(BudgetTable.CATEGORY_ID, expense.getCategoryId());
        mDatabase.insert(BudgetTable.TABLE_NAME, null, contentValues);
    }

    public void addExpense(Expenses expense) {
        mDatabase = getWritableDatabase();
        insertExpense(expense);
    }

    public void editExpense(Expenses expense) {
        mDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(BudgetTable.AMOUNT, expense.getAmount());

        mDatabase.update(
                BudgetTable.TABLE_NAME,
                values,
                BudgetTable._ID + "=" + expense.getId(),
                null
        );
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

    public int totalExpenses() {
        int total = 0;
        mDatabase = getReadableDatabase();

        final String SUM_AMOUNT = "SELECT SUM(" + BudgetTable.AMOUNT + ") FROM " + BudgetTable.TABLE_NAME;

        Cursor cursor = mDatabase.rawQuery(SUM_AMOUNT, null);
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    // TODO: 10/23/2019 add delete method 
}
