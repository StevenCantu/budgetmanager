package com.example.thegreatbudget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class Housing extends android.support.v4.app.Fragment{

    private HousingListener mHousingListener;
    private EditText editText;
    private Button button;
    private float mTempTotal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.housing_layout, container, false);

        editText = view.findViewById(R.id.edit_housing);
        button = view.findViewById(R.id.button_housing);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float input = Float.parseFloat(editText.getText().toString());
                mHousingListener.onHousingSent(input);
            }
        });

        return view;
    }

    /**
     * interface to pass data to activity
     */
    public interface HousingListener{
        void onHousingSent(float input);
    }

    /**
     * update the state of Housing from outside class
     * @param input state of temporary total
     */
    void updateHousing(Float input){
        mTempTotal = input;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof HousingListener){
            mHousingListener = (HousingListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement HousingListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mHousingListener = null;
    }
}
