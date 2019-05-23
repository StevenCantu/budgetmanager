package com.example.thegreatbudget.fragments;

import android.content.Context;
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

import com.example.thegreatbudget.R;
import com.example.thegreatbudget.adapters.RecyclerAdapter;
import com.example.thegreatbudget.util.Expenses;

import java.util.ArrayList;
import java.util.List;

public class Insurance extends android.support.v4.app.Fragment{

    private static final String TAG = "Insurance";
    //interface
    private InsuranceListener mInsuranceListener;
    // general variables
    private float mTempTotal;
    private Context mContext;
    private List<String> mDataList = new ArrayList<>();
    private List<Float> mExpensesList = new ArrayList<>();
    private List<Expenses> mExpenses = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.housing_layout, container, false);
        Log.i(TAG, "onCreateView: " + mDataList);
        initRecyclerView(view);

        return view;
    }


    public interface InsuranceListener {
        void onInsuranceSent(float input);
    }

    public void updateInsurance(float input) {
        mTempTotal = input;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mDataList.add("Rent/Mortgage");
        mDataList.add("Electricity");
        for (String s: mDataList) {
            mExpensesList.add(0f);
        }
        mExpenses.add(new Expenses("Rent/Mortgage"));
        mExpenses.add(new Expenses("Electricity"));
        mExpenses.add(new Expenses("Gas"));
        if(context instanceof InsuranceListener){
            mInsuranceListener = (InsuranceListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement HousingListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mInsuranceListener = null;
    }

    /**
     * initialize recycler vew with mDataList as items
     * @param view view reference
     */
    private void initRecyclerView(View view){
        RecyclerView mRecyclerView = view.findViewById(R.id.housing_recycler);
        // recycler view
        RecyclerAdapter mRecyclerAdapter = new RecyclerAdapter(getContext(), mExpenses);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerAdapter.setOnRecyclerListener(new RecyclerAdapter.OnRecyclerListener() {
            @Override
            public void onItemClicked(int position, float data) {
                mExpensesList.set(position, data);
                float sum = 0f;
                for (float f: mExpensesList) {
                    sum += f;
                }
                mTempTotal = sum;
                Toast.makeText(mContext, "insurance total:" + mTempTotal, Toast.LENGTH_SHORT).show();
                mInsuranceListener.onInsuranceSent(mTempTotal);
            }
        });
    }
}
