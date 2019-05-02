package com.example.thegreatbudget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Miscellaneous extends android.support.v4.app.Fragment{

    private MiscListener mMiscListener;
    private float mTempTotal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.miscellaneous, container, false);

        return view;
    }

    /**
     * interface to pass data to activity
     */
    public interface MiscListener{
        void onMiscSent(float input);
    }

    /**
     * update the state of Housing from outside class
     * @param input state of temporary total
     */
    void updateMisc(Float input){
        mTempTotal = input;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof MiscListener){
            mMiscListener = (MiscListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement HousingListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMiscListener = null;
    }
}