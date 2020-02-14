package com.flourish.budget.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.util.Log;

import com.flourish.budget.database.BudgetContract;
import com.flourish.budget.database.BudgetDbHelper;
import com.flourish.budget.model.BalanceItem;
import com.flourish.budget.model.Expenses;
import com.flourish.budget.model.HistoryItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PdfMaker {

    private static final String TAG = "PdfMaker";
    // width and height measured in 1/72 of an inch
    private static final float PAGE_WIDTH = 72f * 8.5f;
    private static final float PAGE_HEIGHT = 72f * 11f;
    private static final float LINE_SPACING = 15f;
    private static final float TOP_MARGIN = 30f;
    private static final float START_MARGIN = 30f;

    private double mIncome;
    private double mExpense;
    private double mTotal;
    private float mLinePosition = LINE_SPACING;
    private int mPageNumber = 1;

    private PdfDocument mDocument = new PdfDocument();
    private PdfDocument.PageInfo mPageInfo;
    private PdfDocument.Page mPage;
    private Canvas mCanvas;
    private Paint mPaint;
    private List<Expenses> mStatementList = new ArrayList<>();

    public PdfMaker(Context context) {
        initBalanceFields(context);
        initStatementList(context);
    }

    private void initBalanceFields(Context context) {
        Cursor balanceCursor = BudgetDbHelper.getInstance(context).getBalanceCursor();
        while (balanceCursor.moveToNext()) {
            String name = balanceCursor.getString(balanceCursor.getColumnIndex(BudgetContract.BalanceItemTable.ITEM_NAME));
            double amount = balanceCursor.getDouble(balanceCursor.getColumnIndex(BudgetContract.BalanceItemTable.AMOUNT));
            if (BalanceItem.INCOME.equals(name)) {
                mIncome = amount;
            } else if (BalanceItem.EXPENSE.equals(name)) {
                mExpense = amount;
            }
        }
        mTotal = mIncome - mExpense;
        balanceCursor.close();
    }

    private void initStatementList(Context context) {
        Cursor cursor = BudgetDbHelper.getInstance(context).getStatementCursor();
        while (cursor.moveToNext()) {
            final long id = cursor.getLong(cursor.getColumnIndex(BudgetContract.BudgetTable._ID));
            final String expense = cursor.getString(cursor.getColumnIndex(BudgetContract.BudgetTable.EXPENSE));
            final float amount = cursor.getFloat(cursor.getColumnIndex(BudgetContract.BudgetTable.AMOUNT));
            final int categoryId = cursor.getInt(cursor.getColumnIndex(BudgetContract.BudgetTable.CATEGORY_ID));
            final String historyJson = cursor.getString(cursor.getColumnIndex(BudgetContract.BudgetTable.HISTORY));
            Expenses expenses = new Expenses(id, expense, amount, categoryId, historyJson);
            mStatementList.add(expenses);
        }
        cursor.close();
    }

    private void goToNextLine() {
        mLinePosition += LINE_SPACING;
    }

    private void drawTitle(Canvas canvas, String title) {
        Paint paint = new Paint();
        paint.setTextSize(24);
        Rect bound = new Rect();
        paint.getTextBounds(title, 0, title.length(), bound);
        float xPos = (canvas.getWidth() / 2f) - (bound.width() / 2f);
        canvas.drawText(title, xPos, mLinePosition, paint);
    }

    private void drawBalanceLine(Canvas canvas, Paint paint, String title, String body) {
        canvas.drawText(title, START_MARGIN, mLinePosition, paint);
        Rect bound = new Rect();
        paint.getTextBounds(body, 0, body.length(), bound);
        float xPos = (canvas.getWidth() / 2f) - bound.width();
        canvas.drawText(body, xPos, mLinePosition, paint);
        goToNextLine();
    }

    private void drawIntro(Canvas canvas, Paint paint) {
        NumberFormat format = NumberFormat.getCurrencyInstance();

        mLinePosition += TOP_MARGIN;
        drawTitle(canvas, "Statement");
        mLinePosition += TOP_MARGIN;
        drawBalanceLine(canvas, paint, "Income", format.format(mIncome));
        drawBalanceLine(canvas, paint, "Expense", "- " + format.format(mExpense));
        mLinePosition -= 10f;
        canvas.drawLine(START_MARGIN, mLinePosition, canvas.getWidth() / 2, mLinePosition, paint);
        goToNextLine();
        drawBalanceLine(canvas, paint, "Total", format.format(mTotal));
    }

    private void drawBody() {
        mLinePosition += TOP_MARGIN;
        mCanvas.drawLine(START_MARGIN, mLinePosition, mCanvas.getWidth() - START_MARGIN, mLinePosition, mPaint);
        goToNextLine();

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        for (Expenses e : mStatementList) {
            String titleText = e.getTitle();
            String messageText = format.format(e.getAmount());

            checkForNewPage();
            mCanvas.drawText(titleText, START_MARGIN, mLinePosition, mPaint);
            Rect bound = new Rect();
            mPaint.getTextBounds(messageText, 0, messageText.length(), bound);
            float xPos = mCanvas.getWidth() - START_MARGIN - bound.width();
            mCanvas.drawText(messageText, xPos, mLinePosition, mPaint);
            goToNextLine();

            for (HistoryItem item : e.getHistory().getHistory()) {
                titleText = format.format(item.getAmount());
                messageText = item.getDate();

                checkForNewPage();
                mCanvas.drawText(titleText, mCanvas.getWidth() / 4, mLinePosition, mPaint);
                bound = new Rect();
                mPaint.getTextBounds(messageText, 0, messageText.length(), bound);
                xPos = mCanvas.getWidth() - START_MARGIN - bound.width();
                mCanvas.drawText(messageText, xPos, mLinePosition, mPaint);
                goToNextLine();
            }
        }
    }

    private void checkForNewPage() {
        if (mLinePosition >= mCanvas.getHeight() - TOP_MARGIN) {
            createNewPage();
        }
    }

    private void createNewPage() {
        drawPageNumber();
        mDocument.finishPage(mPage);
        mPageNumber++;
        mPageInfo = new PdfDocument.PageInfo.Builder((int)PAGE_WIDTH, (int)PAGE_HEIGHT, mPageNumber).create();
        mPage = mDocument.startPage(mPageInfo);
        mCanvas = mPage.getCanvas();
        mLinePosition = TOP_MARGIN;
    }

    private void drawPageNumber() {
        mCanvas.drawText(String.valueOf(mPageNumber), mCanvas.getWidth() - START_MARGIN,
                mCanvas.getHeight() - LINE_SPACING, mPaint);
    }

    private void createPdfDocument() {
        mDocument = new PdfDocument();
        mPageInfo = new PdfDocument.PageInfo.Builder((int) PAGE_WIDTH, (int) PAGE_HEIGHT, mPageNumber).create();
        mPage = mDocument.startPage(mPageInfo);
        mCanvas = mPage.getCanvas();
        mPaint = new Paint();
    }

    public void makePdf() {
        String extStorageDir = Environment.getExternalStorageDirectory().toString();
        File folder = new File(extStorageDir, "Download");
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        StringBuilder fileName = new StringBuilder("statement");
        fileName.append("_").append(month+1);
        fileName.append("_").append(year);
        fileName.append(".pdf");

        try {
            final File file = new File(folder, fileName.toString());
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);

            createPdfDocument();
            drawIntro(mCanvas, mPaint);
            drawBody();

            drawPageNumber();
            mDocument.finishPage(mPage);
            mDocument.writeTo(fOut);
            mDocument.close();
            fOut.close();

        } catch (IOException e) {
            Log.e(TAG, "makePdf: error", e);
        } finally {
            Log.d(TAG, "makePdf: success!");
        }
    }
}
