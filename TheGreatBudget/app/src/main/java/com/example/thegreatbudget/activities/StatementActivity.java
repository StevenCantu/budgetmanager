package com.example.thegreatbudget.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thegreatbudget.R;
import com.example.thegreatbudget.adapters.ExpenseRecyclerAdapter;
import com.example.thegreatbudget.database.BudgetContract;
import com.example.thegreatbudget.database.BudgetDbHelper;
import com.example.thegreatbudget.model.BalanceItem;
import com.example.thegreatbudget.model.Expenses;
import com.example.thegreatbudget.util.Common;
import com.example.thegreatbudget.util.PdfMaker;

import java.text.NumberFormat;
import java.util.Locale;

public class StatementActivity extends AppCompatActivity {

    private static final String TAG = "StatementActivity";
    public static final int DETAILS_ACTIVITY_REQUEST = 21;

    private TextView mIncomeText;
    private TextView mExpenseText;
    private TextView mTotalText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Common.themeSetter(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);
        setTitle("Statement");

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

    private void fillBalance() {
        double income = 0;
        double expense = 0;
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
