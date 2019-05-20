package com.example.thegreatbudget.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thegreatbudget.R;

public class Savings extends android.support.v4.app.Fragment{

    private SavingsListener mSavingsListener;
    private float mTempTotal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.saving_layout, container, false);

        return view;
    }

    /**
     * interface to pass data to activity
     */
    public interface SavingsListener{
        void onSavingsSent(float input);
    }

    /**
     * update the state of Savings from outside class
     * @param input state of temporary total
     */
    public void updateSavings(Float input){
        mTempTotal = input;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof SavingsListener){
            mSavingsListener = (SavingsListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement HousingListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSavingsListener = null;
    }
}
