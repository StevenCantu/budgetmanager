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

import com.example.thegreatbudget.MainActivity;
import com.example.thegreatbudget.R;
import com.example.thegreatbudget.adapters.RecyclerAdapter;
import com.example.thegreatbudget.util.ObjectSerializer;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Housing extends android.support.v4.app.Fragment{
    // constants
    private static final String TAG = "Housing";
    public static final String HOUSING_LIST = "thegreatbudget.fragments.housing.list";
    // interface listener
    private HousingListener mHousingListener;
    // general variables
    private float mTempTotal;
    private Context mContext;
    private List<String> mDataList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.housing_layout, container, false);
        Log.i(TAG, "onCreateView: " + mDataList);
        initRecyclerView(view);

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
    public void updateHousing(float input){
        mTempTotal = input;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        //loadData();

        mDataList.add("Rent/Mortgage");
        mDataList.add("Electricity");
        mDataList.add("Gas");
        mDataList.add("Internet/Cable");
        mDataList.add("Water/Sewage");

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
        saveData();
    }

    /**
     * initialize recycler vew with mDataList as items
     * @param view view reference
     */
    private void initRecyclerView(View view){
        RecyclerView mRecyclerView = view.findViewById(R.id.housing_recycler);
        // recycler view
        RecyclerAdapter mRecyclerAdapter = new RecyclerAdapter(getContext(), mDataList);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    /**
     * save the state of the fragment from shared preferences
     */
    private void saveData(){
        SharedPreferences sp = mContext.getSharedPreferences(MainActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        try {
            editor.putString(HOUSING_LIST, ObjectSerializer.serialize((Serializable) mDataList));
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
        try {
            mDataList = (ArrayList<String>) ObjectSerializer.deserialize(sp.getString(HOUSING_LIST, ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
