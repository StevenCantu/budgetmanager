package com.example.thegreatbudget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Personal extends android.support.v4.app.Fragment{

    private PersonalListener mPersonalListener;
    private float mTempTotal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.personal_layout, container, false);

        return view;
    }

    /**
     * interface to pass data to activity
     */
    public interface PersonalListener{
        void onPersonalSent(float input);
    }

    /**
     * update the state of Personal from outside class
     * @param input state of temporary total
     */
    void updatePersonal(Float input){
        mTempTotal = input;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof PersonalListener){
            mPersonalListener = (PersonalListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement PersonalListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPersonalListener = null;
    }
}