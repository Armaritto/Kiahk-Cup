package com.stgsporting.quiz_fut.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stgsporting.quiz_fut.R;
import com.stgsporting.quiz_fut.data.Question;
import com.stgsporting.quiz_fut.data.Quiz;
import com.stgsporting.quiz_fut.helpers.ItemClickListener;

import java.util.List;

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder> {

    private final List<String> options;
    private final LayoutInflater mInflater;
    ItemClickListener itemClickListener;
    int selectedPosition = -1;

    public OptionsAdapter(Context context, List<String> options, ItemClickListener itemClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.options = options;
        this.itemClickListener = itemClickListener;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.options_listitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String option = this.options.get(position);

        holder.option.setChecked(position == selectedPosition);

        holder.option.setText(option);

        holder.option.setOnCheckedChangeListener((__, isChecked) -> {
                    if (isChecked) {
                        selectedPosition = holder.getAdapterPosition();
                        itemClickListener.onClick(selectedPosition);
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return this.options.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton option;

        ViewHolder(View itemView) {
            super(itemView);
            option = itemView.findViewById(R.id.option);
        }
    }
}
