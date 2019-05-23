package com.example.thegreatbudget.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.thegreatbudget.MainActivity;
import com.example.thegreatbudget.R;
import com.example.thegreatbudget.adapters.RecyclerAdapter;
import com.example.thegreatbudget.util.Expenses;
import com.example.thegreatbudget.util.ObjectSerializer;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Housing extends android.support.v4.app.Fragment{
    // constants
    private static final String TAG = "Housing";
    public static final String HOUSING_LIST_TITLE = "thegreatbudget.fragments.housing.list.title";
    public static final String HOUSING_LIST_EXPENSE = "thegreatbudget.fragments.housing.list.expense";
    private String mHousingListTitle;
    private String mHousingListExpense;
    // interface listener
    private HousingListener mHousingListener;
    // general variables
    private float mTempTotal;
    private Context mContext;
    private List<Expenses> mExpenses = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.housing_layout, container, false);
        initRecyclerView(view);
        return view;
    }


    /**
     * interface to pass data to activity
     */
    public interface HousingListener{
        void onHousingSent(float input);
    }

    public void setHousingListener(HousingListener housingListener){
        mHousingListener = housingListener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

        Bundle bundle = getArguments();
        if (bundle != null) {
            String[] titles = bundle.getStringArray(MainActivity.HOUSING_TITLES);
            mHousingListTitle = bundle.getString(MainActivity.HOUSING_TITLE_SP);
            mHousingListExpense = bundle.getString(MainActivity.HOUSING_EXPENSE_SP);
            loadData();
            if (mExpenses.isEmpty()) {
                for (String s : titles) {
                    mExpenses.add(new Expenses(s));
                }
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mHousingListener = null;
        saveData();
    }

    /**
     * initialize recycler vew with mDataList as items
     * @param view view reference
     */
    private void initRecyclerView(View view){
        Log.i(TAG, "initRecyclerView: ");
        RecyclerView mRecyclerView = view.findViewById(R.id.housing_recycler);
        // recycler view
        RecyclerAdapter mRecyclerAdapter = new RecyclerAdapter(getContext(), mExpenses);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerAdapter.setOnRecyclerListener(new RecyclerAdapter.OnRecyclerListener() {
            @Override
            public void onItemClicked(int position, float data) {

                Expenses expenses = mExpenses.get(position);
                expenses.setExpense(String.valueOf(data));
                mExpenses.set(position, expenses);

                mTempTotal = mExpenses.get(position).sumExpenses(mExpenses);
                Toast.makeText(mContext, "housing total:" + mTempTotal, Toast.LENGTH_SHORT).show();
                mHousingListener.onHousingSent(mTempTotal);
            }
        });
    }

    /**
     * save the state of the fragment from shared preferences
     */
    private void saveData(){
        SharedPreferences sp = mContext.getSharedPreferences(MainActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        List<String> titles = mExpenses.get(0).getTitles(mExpenses);
        List<String> expenses = mExpenses.get(0).getExpenses(mExpenses);
        try {
            editor.putString(mHousingListTitle, ObjectSerializer.serialize((Serializable) titles));
            editor.putString(mHousingListExpense, ObjectSerializer.serialize((Serializable) expenses));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.apply();
    }

    /**
     * load state of fragment from shared preferences
     */
    private void loadData(){
        SharedPreferences sp = mContext.getSharedPreferences(MainActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        List<String> titles = new ArrayList<>();
        List<String> expenses = new ArrayList<>();
        try {
            titles = (ArrayList<String>) ObjectSerializer.deserialize(sp.getString(mHousingListTitle, ObjectSerializer.serialize(new ArrayList<String>())));
            expenses = (ArrayList<String>) ObjectSerializer.deserialize(sp.getString(mHousingListExpense, ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        List<Expenses> expensesList = new ArrayList<>();
        if(!titles.isEmpty()){
            for(int i = 0; i < titles.size(); i++){
                expensesList.add(new Expenses(titles.get(i)));
                expensesList.get(i).setExpense(expenses.get(i));
            }
        }
        mExpenses = expensesList;
    }
}
