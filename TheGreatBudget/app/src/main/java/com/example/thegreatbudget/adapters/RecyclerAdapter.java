package com.example.thegreatbudget.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.thegreatbudget.R;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private static final String TAG = "RecyclerAdapter";
    private List<String> mDataList;
    private Context mContext;

    private static View mView;

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        ImageButton trash;
        ImageButton edit;
        RelativeLayout relativeLayout;

        ViewHolder(View itemView){
            super(itemView);

            mView = itemView;
            textView = itemView.findViewById(R.id.recycler_text);
            trash = itemView.findViewById(R.id.recycler_trash);
            edit = itemView.findViewById(R.id.edit_recycler);
            relativeLayout = itemView.findViewById(R.id.recycler_parent_layout);
        }
    }

    /**
     * constructor
     * @param context activity context
     * @param dataList list of items to populate
     */
    public RecyclerAdapter(Context context, List<String> dataList){
        mDataList = dataList;
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, final int position) {
        holder.textView.setText(mDataList.get(position));
        holder.trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDataList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mDataList.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }
}
