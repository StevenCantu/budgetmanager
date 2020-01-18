package com.example.thegreatbudget.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thegreatbudget.R;
import com.example.thegreatbudget.fragments.NeverAskAgainDialog;
import com.example.thegreatbudget.util.Common;

import java.text.NumberFormat;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;

import static com.example.thegreatbudget.util.Common.IS_CHECKED;
import static com.example.thegreatbudget.util.Common.NOT_CHECKED;
import static com.example.thegreatbudget.util.Common.SHARED_PREFERENCES;

public class IncomeActivity extends AppCompatActivity {
    private static final String TAG = "IncomeActivity";

    public static final String EXTRA_INCOME = "com.example.thegreatbudget.incomeactivity.extra.income";
    public static final int KEYPAD_INDEX = 11;
    public static final int INCOME_LIMIT = 6;
    public static final int MAX_SIZE = 10;
    public static final String CHECK_UNDO_KEY = "skipMessage.undo";
    public static final String CHECK_EDIT_KEY = "skipMessage.edit";

    private FloatingActionButton mAddButton;
    private ImageView mDivider;
    private Menu mIncomeMenu;
    private GridLayout mGrid;
    private TextView mIncomeInput;
    private TextView mIncomeText;
    private Button mEnter;
    private Button[] inputs = new Button[11];
    private ImageButton mDelete;
    private Handler handler;

    private Deque<Double> mTemps = new ArrayDeque<>();
    private double mIncome;
    private double mTempIncome;
    private double mTempUndo;
    private String mDecimalInput;
    private boolean mEditing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Common.themeSetter(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        setTitle("Income");
        handler = new Handler();

        Intent intent = getIntent();
        mIncome = intent.getDoubleExtra(MainActivity.INCOME_EXTRA, -111f);
        mDecimalInput = "";

        for (int i = 0; i < KEYPAD_INDEX; i++) {
            String buttonID = "button" + i;
            if (i == 10) {
                buttonID = "buttonDecimal";
            }
            int resourceID = getResources().getIdentifier(buttonID, "id", getPackageName());
            inputs[i] = findViewById(resourceID);
            inputs[i].setOnClickListener(keypadListener);

        }
        mAddButton = findViewById(R.id.add_input);
        mIncomeInput = findViewById(R.id.income_input);
        mDivider = findViewById(R.id.income_divider);
        mGrid = findViewById(R.id.keypadLayout);
        mDelete = findViewById(R.id.buttonDelete);
        mEnter = findViewById(R.id.buttonEnter);
        mEnter.setOnClickListener(enterButtonListener);
        mIncomeText = findViewById(R.id.income_text);
        updateIncome(mIncome);
        mAddButton.setOnClickListener(incomeTextClickListener);
        mDelete.setOnClickListener(keypadListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.income_activity_menu, menu);
        mIncomeMenu = menu;
        mIncomeMenu.findItem(R.id.menu_cancel).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences sp = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        String checked;
        switch (item.getItemId()) {
            case R.id.menu_edit:
                checked = sp.getString(CHECK_EDIT_KEY, NOT_CHECKED);
                if (IS_CHECKED.equals(checked)) {
                    mEditing = true;
                    keypadView();
                }
                showEditDialog();
                return true;
            case R.id.menu_undo:
                checked = sp.getString(CHECK_UNDO_KEY, NOT_CHECKED);
                if (IS_CHECKED.equals(checked)) {
                    undoIncome();
                }
                showUndoDialog();
                return true;
            case R.id.menu_cancel:
                mEditing = false;
                resetViews();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void resetViews() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                moveIncomeView(Gravity.CENTER);
                mIncomeText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 64);
                setTitle("Income");
                setButtonsVisibility(View.INVISIBLE);
                mIncomeMenu.findItem(R.id.menu_undo).setVisible(true);
                mIncomeMenu.findItem(R.id.menu_edit).setVisible(true);
                mIncomeMenu.findItem(R.id.menu_cancel).setVisible(false);
                mAddButton.show();
                mDecimalInput = "";
                mIncomeInput.setText("");
                Intent intent = new Intent();
                intent.putExtra(EXTRA_INCOME, mIncome);
                setResult(RESULT_OK, intent);
            }
        }, 350);
    }

    private void undoIncome() {
        if (!mTemps.isEmpty()) {
            mTempIncome = mIncome;
            mIncome = mTemps.removeLast();
            mTempUndo = mIncome;
            Log.d(TAG, "undoIncome: " + mTemps + " " + mTempIncome);
            updateIncome(mIncome);
            showSnackBar();
        }
    }

    private void keypadView() {
        moveIncomeView(Gravity.TOP, Gravity.LEFT);
        mIncomeText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        setTitle(mEditing ? "Edit Income" : "Add Income");
        setButtonsVisibility(View.VISIBLE);
        mIncomeMenu.findItem(R.id.menu_undo).setVisible(false);
        mIncomeMenu.findItem(R.id.menu_edit).setVisible(false);
        mIncomeMenu.findItem(R.id.menu_cancel).setVisible(true);
        mAddButton.hide();
    }

    private void addToDeque(double item) {
        if (mTemps.size() < MAX_SIZE) {
            mTemps.add(item);
        } else {
            mTemps.removeFirst();
            mTemps.add(item);
        }
    }

    private void buttonPicker(View v) {

        switch (v.getId()) {
            case R.id.buttonDecimal:
                if (!mDecimalInput.contains(".")) {
                    String s = ((Button) v).getText().toString();
                    addToDecimal(s);
                }
                break;
            case R.id.button0:
                String s = ((Button) v).getText().toString();
                addToDecimal(s);
                break;
            case R.id.buttonDelete:
                deleteFromDecimal();
                break;
            default:
                s = ((Button) v).getText().toString();
                addToDecimal(s);
                break;
        }
    }

    private void addToDecimal(String s) {
        if (s.equals(".")) {
            mDecimalInput = mDecimalInput + s;
        } else if (mDecimalInput.contains(".")) {
            int index = mDecimalInput.indexOf(".");
            if (mDecimalInput.length() <= index + 2) {
                if (mDecimalInput.length() == index + 2) {
                    if (!s.equals("0")) {
                        mDecimalInput = mDecimalInput + s;
                    }
                } else {
                    mDecimalInput = mDecimalInput + s;
                }

            }
        } else {
            if (mDecimalInput.length() < INCOME_LIMIT) {
                mDecimalInput = mDecimalInput + s;
            }
        }

        updateInputBox(mDecimalInput);
    }

    private void deleteFromDecimal() {
        String test = "2.";
        String t = test.substring(0, 0);
        if (!mDecimalInput.isEmpty()) {
            if (mDecimalInput.contains(".") && mDecimalInput.length() - 1 == mDecimalInput.indexOf(".")) {
                if (mDecimalInput.length() == 1) {
                    mDecimalInput = mDecimalInput.substring(0, mDecimalInput.length() - 1);
                } else {
                    mDecimalInput = mDecimalInput.substring(0, mDecimalInput.length() - 2);
                }
            } else {
                mDecimalInput = mDecimalInput.substring(0, mDecimalInput.length() - 1);
            }
            updateInputBox(mDecimalInput);
        }
    }


    private void setButtonsVisibility(final int visibility) {
        for (int i = 0; i < KEYPAD_INDEX; i++) {
            inputs[i].setVisibility(visibility);
        }
        mIncomeInput.setVisibility(visibility);
        mDivider.setVisibility(visibility);
        mEnter.setVisibility(visibility);
        mGrid.setVisibility(visibility);
        mDelete.setVisibility(visibility);

    }

    private void updateInputBox(String s) {
        if (s.isEmpty() || s.equals(".")) {
            s = "0";
        }
        double currency = Double.parseDouble(s);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        String money = numberFormat.format(currency);
        mIncomeInput.setText(money);
    }

    private void updateIncome(double value) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        String money = numberFormat.format(value);
//        mIncomeText.setText(String.format(Locale.US, "Income: %s", money));
        mIncomeText.setText(String.format(Locale.US, "%s", money));
    }

    private void moveIncomeView(final int gravity) {
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) mIncomeText.getLayoutParams();
        mIncomeText.setGravity(gravity);
        mIncomeText.setLayoutParams(p);
    }

    private void moveIncomeView(final int gravity, final int gravity2) {
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) mIncomeText.getLayoutParams();
        mIncomeText.setGravity(gravity | gravity2);
        mIncomeText.setLayoutParams(p);
    }

    private void showEditDialog() {
        NeverAskAgainDialog dialog = new NeverAskAgainDialog();
        dialog.setArguments(new Bundle());
        if (getSupportFragmentManager() != null && isNotChecked(CHECK_EDIT_KEY)) {
            dialog.show(getSupportFragmentManager(), "showEditDialog");
        }

        dialog.setOnClickListener(new NeverAskAgainDialog.OnClickListener() {
            @Override
            public void positiveClick() {
                mEditing = true;
                keypadView();
            }

            @Override
            public void negativeClick() {

            }
        });
    }

    private void showUndoDialog() {
        NeverAskAgainDialog dialog = new NeverAskAgainDialog();

        Bundle bundle = new Bundle();
        bundle.putString(NeverAskAgainDialog.MESSAGE, "Are you sure you want to undo your previous income change?");
        bundle.putString(NeverAskAgainDialog.KEY, CHECK_UNDO_KEY);

        dialog.setArguments(bundle);
        if (getSupportFragmentManager() != null && isNotChecked(CHECK_UNDO_KEY)) {
            dialog.show(getSupportFragmentManager(), "showUndoDialog");
        }

        dialog.setOnClickListener(new NeverAskAgainDialog.OnClickListener() {
            @Override
            public void positiveClick() {
                undoIncome();
            }

            @Override
            public void negativeClick() {

            }
        });
    }

    private boolean isNotChecked(String key) {
        SharedPreferences settings = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        String skipMessage = settings.getString(key, NOT_CHECKED);

        return !IS_CHECKED.equals(skipMessage);
    }

    private void showSnackBar() {
        String msg = String.format(Locale.getDefault(), "You have removed $%.2f.", mTempIncome);
        Snackbar.make(findViewById(R.id.activity_income), msg, Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mIncome = mTempIncome;
                        mTemps.add(mTempUndo);
                        updateIncome(mIncome);
                        Log.d(TAG, "onClick: " + mTemps + " " + mTempIncome);
                    }
                }).show();
    }

    View.OnClickListener enterButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!mDecimalInput.isEmpty()) {
                addToDeque(mIncome);
                if (mEditing) {
                    mIncome = Double.parseDouble(mDecimalInput);
                    mEditing = false;
                } else {
                    if (Double.parseDouble(mDecimalInput) + mIncome > 999999.99) {
                        Toast.makeText(IncomeActivity.this, "You have exceeded the limit.", Toast.LENGTH_SHORT).show();
                    } else {
                        mIncome += Double.parseDouble(mDecimalInput);
                    }
                }
                updateIncome(mIncome);
                mDecimalInput = "";
                mIncomeInput.setText("");
            }
            resetViews();
        }
    };

    View.OnClickListener incomeTextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            keypadView();
        }
    };

    View.OnClickListener keypadListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            buttonPicker(v);
        }
    };
}
