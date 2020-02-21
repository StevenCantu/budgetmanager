package com.flourish.budget.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.flourish.budget.R;
import com.flourish.budget.adapters.ExpenseRecyclerAdapter;
import com.flourish.budget.database.BudgetContract;
import com.flourish.budget.database.BudgetDbHelper;
import com.flourish.budget.model.BalanceItem;
import com.flourish.budget.model.Expenses;
import com.flourish.budget.util.Common;
import com.flourish.budget.util.PdfMaker;

import java.text.NumberFormat;
import java.util.Locale;

public class StatementActivity extends AppCompatActivity {

    private static final String TAG = "StatementActivity";
    private static final double EPSILON = 0.001d;

    private TextView mIncomeText;
    private TextView mExpenseText;
    private TextView mTotalText;

    private boolean mCanSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Common.themeSetterNoActionBar(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);

        setupActionBar();

        mIncomeText = findViewById(R.id.statement_income_total);
        mExpenseText = findViewById(R.id.statement_expense_total);
        mTotalText = findViewById(R.id.statement_total);
        fillBalance();
        initRecyclerView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate((R.menu.statement_activity_menu), menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_statement) {
            if (!mCanSave) {
                Toast.makeText(this, "There is no information to save.", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (isStoragePermissionGranted()) {
                savePdf();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Common.WRITE_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            savePdf();
        }
    }

    private void setupActionBar() {
        setTitle("Statement");
        Toolbar toolbar = findViewById(R.id.statement_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void fillBalance() {
        double income = 0d;
        double expense = 0d;
        double total;

        BudgetDbHelper db = BudgetDbHelper.getInstance(this);
        Cursor balanceCursor = db.getBalanceCursor();
        while (balanceCursor.moveToNext()) {
            String name = balanceCursor.getString(balanceCursor.getColumnIndex(BudgetContract.BalanceItemTable.ITEM_NAME));
            double amount = balanceCursor.getDouble(balanceCursor.getColumnIndex(BudgetContract.BalanceItemTable.AMOUNT));

            if (BalanceItem.INCOME.equals(name)) {
                income = amount;
            } else if (BalanceItem.EXPENSE.equals(name)) {
                expense = amount;
            }
        }
        balanceCursor.close();
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        total = income - expense;
        mIncomeText.setText(format.format(income));
        mExpenseText.setText(format.format(expense));
        mTotalText.setText(format.format(total));

        if (Math.abs(income) <= EPSILON && Math.abs(expense) <= EPSILON) {
            mCanSave = false;
        } else {
            mCanSave = true;
        }
    }

    private void initRecyclerView() {
        BudgetDbHelper db = BudgetDbHelper.getInstance(this);
        Cursor statementCursor = db.getStatementCursor();
        RecyclerView recyclerView = findViewById(R.id.statement_recycler_view);
        ExpenseRecyclerAdapter adapter = new ExpenseRecyclerAdapter(this, statementCursor);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnItemClickedListener(new ExpenseRecyclerAdapter.OnItemClickedListener() {
            @Override
            public void amountClicked(Expenses expense) {
            }

            @Override
            public void itemClicked(Expenses expense) {
                Intent intent = new Intent(StatementActivity.this, DetailsActivity.class);
                intent.putExtra(DetailsActivity.EXPENSE_EXTRA, expense);
                intent.putExtra(DetailsActivity.READ_ONLY_EXTRA, true);
                startActivity(intent);
            }
        });
    }

    private void savePdf() {
        PdfMaker pdfMaker = new PdfMaker(StatementActivity.this);
        pdfMaker.makePdf();
        Toast.makeText(this, "Statement saved to \"Downloads\"", Toast.LENGTH_SHORT).show();
    }

    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Common.WRITE_REQUEST_CODE);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }
}
