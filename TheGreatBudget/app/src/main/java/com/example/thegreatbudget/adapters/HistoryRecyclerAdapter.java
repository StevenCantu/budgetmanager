package com.example.thegreatbudget.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.thegreatbudget.R;
import com.example.thegreatbudget.model.History;
import com.example.thegreatbudget.model.HistoryItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.ViewHolder> {

    private List<HistoryItem> mHistoryItems;
    private Context mContext;
    private OnClickListener mClickListener;

    public HistoryRecyclerAdapter(Context context, List<HistoryItem> historyItems) {
        mContext = context;
        mHistoryItems = historyItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_layout, viewGroup, false);
        return new HistoryRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final HistoryItem item = mHistoryItems.get(position);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        viewHolder.mAmount.setText(numberFormat.format(item.getAmount()));
        viewHolder.mDate.setText(item.getDate());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onClick(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mHistoryItems.size();
    }

    public void setOnClickListener(OnClickListener listener) {
        mClickListener = listener;
    }

    public interface OnClickListener {
        void onClick(HistoryItem item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mAmount;
        private TextView mDate;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            mAmount = itemView.findViewById(R.id.recycler_text);
            mDate = itemView.findViewById(R.id.recycler_edit);
        }
    }
}
