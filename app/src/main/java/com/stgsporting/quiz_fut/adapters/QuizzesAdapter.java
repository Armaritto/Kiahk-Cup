package com.stgsporting.quiz_fut.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.quiz_fut_draft.R;
import com.stgsporting.quiz_fut.data.Quiz;

import java.util.List;

public class QuizzesAdapter extends RecyclerView.Adapter<QuizzesAdapter.ViewHolder> {

    private final List<Quiz> quiz;
    private final LayoutInflater mInflater;
    private final Context context;

    public QuizzesAdapter(Context context, List<Quiz> quiz) {
        this.mInflater = LayoutInflater.from(context);
        this.quiz = quiz;
        this.context = context;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.mosab2a_listitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Quiz q = this.quiz.get(position);

        holder.title.setText(q.getName());

        String coins = q.getCoins() + " Coins";
        holder.coins.setText(coins);

        holder.quiz.setOnClickListener(v-> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return quiz.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView coins;
        RelativeLayout quiz;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            coins = itemView.findViewById(R.id.coins);
            quiz = itemView.findViewById(R.id.mosab2a);
        }

    }

}
