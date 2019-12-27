package com.example.thegreatbudget.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;

class PdfMaker {

    private static final String TAG = "PdfMaker";
    // width and height measured in 1/72 of an inch
    private static final float PAGE_WIDTH = 72f * 8.5f;
    private static final float PAGE_HEIGHT = 72f * 11f;
    private static final float LINE_SPACING = 15f;
    private static final float TOP_MARGIN = 30f;
    private static final float START_MARGIN = 30f;

    private float mIncome;
    private float mExpense;
    private float mTotal;
    private float mLinePosition = LINE_SPACING;
    private int mPageNumber = 1;

    private PdfDocument mDocument = new PdfDocument();
    private PdfDocument.PageInfo mPageInfo;
    private PdfDocument.Page mPage;
    private Canvas mCanvas;
    private Paint mPaint;

    PdfMaker() {
        mIncome = 100000f;
        mExpense = 52f;
        mTotal = mIncome - mExpense;
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

        for (int i = 0; i < 100; i++) {
            checkForNewPage();
            mCanvas.drawText("sample* " + i, START_MARGIN, mLinePosition, mPaint);
            Rect bound = new Rect();
            mPaint.getTextBounds("text", 0, "text".length(), bound);
            float xPos = mCanvas.getWidth() - START_MARGIN - bound.width();
            mCanvas.drawText("text", xPos, mLinePosition, mPaint);
            goToNextLine();
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

    void makePdf() {
        String extStorageDir = Environment.getExternalStorageDirectory().toString();
        File folder = new File(extStorageDir, "Download");

        try {
            final File file = new File(folder, "sample.pdf");
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

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == WRITE_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            Log.d(TAG, "onRequestPermissionsResult: granted");
//            PdfMaker pdfMaker = new PdfMaker();
//            pdfMaker.makePdf();
//        }
//    }
//
//    private boolean isStoragePermissionGranted() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    == PackageManager.PERMISSION_GRANTED) {
//                Log.v(TAG,"Permission is granted");
//                return true;
//            } else {
//
//                Log.v(TAG,"Permission is revoked");
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_REQUEST_CODE);
//                return false;
//            }
//        }
//        else { //permission is automatically granted on sdk<23 upon installation
//            Log.v(TAG,"Permission is granted");
//            return true;
//        }
//    }
}
