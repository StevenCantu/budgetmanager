package com.example.thegreatbudget;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;

public class IncomeActivity extends AppCompatActivity {
    private static final String TAG = "IncomeActivity";

    public static final String EXTRA_INCOME = "com.example.thegreatbudget.incomeactivity.extra.income";
    public static final int KEYPAD_INDEX = 11;
    public static final int INCOME_LIMIT = 10;
    public static final int MAX_SIZE = 10;
    public static final String PREFS = "com.example.thegreatbudget.shared.prefs";
    public static final String CHECK_KEY = "skipMessage";
    public static final String ISCHECKED = "checked";
    public static final String NOTCHECKED = "not checked";

    private CheckBox mIgnore;
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
    private double mIncomeTemp;
    private String mDecimalInput;
    private boolean mEditing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

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
        switch (item.getItemId()) {
            case R.id.menu_edit:
                mEditing = true;
                addIncome();
                return true;
            case R.id.menu_undo:
                showUndoDialog();
                undoIncome();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    View.OnClickListener undoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!mTemps.isEmpty()) {
                mIncome = mTemps.removeLast();
                updateIncome(mIncome);
            }
        }
    };

    View.OnClickListener enterButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!mDecimalInput.isEmpty()) {
                addToDeque(mIncome);
                Log.d("DEBUG", "onClick: " + mTemps);
                mIncomeTemp = mIncome;
                if (mEditing) {
                    mIncome = Double.parseDouble(mDecimalInput);
                    mEditing = false;
                } else {
                    mIncome += Double.parseDouble(mDecimalInput);
                }
                updateIncome(mIncome);
                mDecimalInput = "";
                mIncomeInput.setText("");
            }
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    moveIncomeView(Gravity.CENTER);
                    setButtonsVisibility(View.INVISIBLE);
                    mIncomeMenu.findItem(R.id.menu_undo).setVisible(true);
                    mIncomeMenu.findItem(R.id.menu_edit).setVisible(true);
                    mIncomeMenu.findItem(R.id.menu_cancel).setVisible(false);
                    mAddButton.show();
                    mDecimalInput = "";
                    mIncomeInput.setText("");
                    Intent intent = new Intent(IncomeActivity.this, MainActivity.class);
                    intent.putExtra(EXTRA_INCOME, mIncome);
                    setResult(RESULT_OK, intent);
                }
            }, 350);
        }
    };

    View.OnClickListener editClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mEditing = true;
            addIncome();
        }
    };

    /**
     *
     */
    View.OnClickListener incomeTextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addIncome();
        }
    };

    View.OnClickListener keypadListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            buttonPicker(v);
        }
    };

    private void undoIncome() {
        if (!mTemps.isEmpty()) {
            mIncome = mTemps.removeLast();
            updateIncome(mIncome);
        }
    }

    private void addIncome() {
        moveIncomeView(Gravity.CENTER_HORIZONTAL);
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
        Log.d(TAG, "deleteFromDecimal: \"" + t + "\"");
        Log.d(TAG, "deleteFromDecimal: original " + mDecimalInput);
        if (!mDecimalInput.isEmpty()) {
            if (mDecimalInput.contains(".") && mDecimalInput.length() - 1 == mDecimalInput.indexOf(".")) {
                if (mDecimalInput.length() == 1) {
                    mDecimalInput = mDecimalInput.substring(0, mDecimalInput.length() - 1);
                } else {
                    mDecimalInput = mDecimalInput.substring(0, mDecimalInput.length() - 2);
                }
                Log.d(TAG, "deleteFromDecimal: contains \"" + mDecimalInput + "\"");
            } else {
                mDecimalInput = mDecimalInput.substring(0, mDecimalInput.length() - 1);
                Log.d(TAG, "deleteFromDecimal: \"" + mDecimalInput + "\"");
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
        mIncomeText.setText(String.format(Locale.US, "Income: %s", money));
    }

    private void moveIncomeView(final int gravity) {
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) mIncomeText.getLayoutParams();
        mIncomeText.setGravity(gravity);
        mIncomeText.setLayoutParams(p);
    }

    private void showUndoDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater adbInflater = LayoutInflater.from(this);
        View eulaLayout = adbInflater.inflate(R.layout.check_layout, null);
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        String skipMessage = settings.getString(CHECK_KEY, NOTCHECKED);

        mIgnore = eulaLayout.findViewById(R.id.skip_check);
        dialog.setView(eulaLayout);
        dialog.setTitle("Attention");
        dialog.setMessage("Are you sure you want to undo your previous income change?");

        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                saveChecked();

            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                saveChecked();

            }
        });

        if (!ISCHECKED.equals(skipMessage)) {
            dialog.show();
        }
    }

    private void saveChecked() {
        String checkBoxResult = NOTCHECKED;

        if (mIgnore.isChecked()) {
            checkBoxResult = ISCHECKED;
        }

        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(CHECK_KEY, checkBoxResult);
        editor.apply();
    }

    // TODO: 8/15/2019 make input bigger and better
    // TODO: 8/15/2019 set default colors
    // TODO: 8/15/2019 add snackbar for undo
    // TODO: 9/2/2019 menu dialog "dont show again" for edit
    // TODO: 9/2/2019 logic for dialogs 
    // TODO: 9/2/2019 cancel menu button
}
