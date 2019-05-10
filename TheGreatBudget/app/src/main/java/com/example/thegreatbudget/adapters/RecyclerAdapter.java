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

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private static final String TAG = "RecyclerAdapter";
    private List<String> mDataList;
    private Context mContext;

    private static View mView;

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        TextView editText;
        RelativeLayout relativeLayout;
        String m_Text;

        ViewHolder(View itemView){
            super(itemView);

            mView = itemView;
            textView = itemView.findViewById(R.id.recycler_text);
            editText = itemView.findViewById(R.id.recycler_edit);
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
    public void onBindViewHolder(@NonNull final RecyclerAdapter.ViewHolder holder, final int position) {
        holder.textView.setText(mDataList.get(position));
        holder.editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Amount");

                // Set up the input
                final EditText input = new EditText(mContext);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        float num = Float.parseFloat(input.getText().toString());
                        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
                        holder.editText.setText(numberFormat.format(num));
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
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }
}
