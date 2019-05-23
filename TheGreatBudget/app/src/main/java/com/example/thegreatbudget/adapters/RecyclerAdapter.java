package com.example.thegreatbudget.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thegreatbudget.R;
import com.example.thegreatbudget.util.Expenses;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private static final String TAG = "RecyclerAdapter";
    private List<String> mDataList;
    private List<Expenses> mExpenses;
    private Context mContext;
    private OnRecyclerListener mOnRecyclerListener;


    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        TextView editText;
        RelativeLayout relativeLayout;
        OnRecyclerListener onRecyclerListener;
        Context context;

        ViewHolder(View itemView, OnRecyclerListener listener, Context c){
            super(itemView);

            context = c;
            textView = itemView.findViewById(R.id.recycler_text);
            editText = itemView.findViewById(R.id.recycler_edit);
            relativeLayout = itemView.findViewById(R.id.recycler_parent_layout);
            onRecyclerListener = listener;

            editText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        buildDialog(position);
                    }
                }
            });
        }

        private void buildDialog(final int position){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Amount");

            // Set up the input
            final EditText input = new EditText(context);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    float num = Float.parseFloat(input.getText().toString());
                    NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
                    editText.setText(numberFormat.format(num));

                    if(onRecyclerListener != null) {
                        onRecyclerListener.onItemClicked(position, num);
                    }

                }
            });
            builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
    }

    /**
     * constructor
     * @param context activity context
     * @param expenses list of items to populate
     */
    public RecyclerAdapter(Context context, List<Expenses> expenses){
        mContext = context;
        mExpenses = expenses;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_layout, parent, false);
        return new ViewHolder(view, mOnRecyclerListener, mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerAdapter.ViewHolder holder, final int position) {
        //holder.textView.setText(mDataList.get(position));
        holder.textView.setText(mExpenses.get(position).getTitle());
        holder.editText.setText(mExpenses.get(position).getExpense());
    }

    @Override
    public int getItemCount() {
        return mExpenses.size();
    }

    public interface OnRecyclerListener{
        void onItemClicked(int position, float data);
    }

    public void setOnRecyclerListener(OnRecyclerListener onRecyclerListener){
        mOnRecyclerListener = onRecyclerListener;
    }
}
