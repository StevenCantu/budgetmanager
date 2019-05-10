package com.example.thegreatbudget.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thegreatbudget.R;

import java.util.ArrayList;
import java.util.List;

public class Insurance extends android.support.v4.app.Fragment{

    private static final String TAG = "Insurance";

    private InsuranceListener mInsuranceListener;

    // general variables
    private float mTempTotal;
    private Context mContext;
    private List<String> mDataList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.insurance_layout, container, false);
        Log.i(TAG, "onCreateView: " + mDataList);

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
}
