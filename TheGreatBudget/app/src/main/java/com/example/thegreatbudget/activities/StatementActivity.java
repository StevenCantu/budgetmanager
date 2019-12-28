package com.example.thegreatbudget.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.thegreatbudget.R;
import com.example.thegreatbudget.util.Common;
import com.example.thegreatbudget.util.PdfMaker;

public class StatementActivity extends AppCompatActivity {

    private static final String TAG = "StatementActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Common.themeSetter(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);
        setTitle("Statement");
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
            Log.d(TAG, "onOptionsItemSelected: trying to save pdf");
            if (isStoragePermissionGranted()) {
                PdfMaker pdfMaker = new PdfMaker();
                pdfMaker.makePdf();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Common.WRITE_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onRequestPermissionsResult: granted");
            PdfMaker pdfMaker = new PdfMaker();
            pdfMaker.makePdf();
        }
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
