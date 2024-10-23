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
import com.stgsporting.quiz_fut.activities.AddQuestionsToQuizActivity;
import com.stgsporting.quiz_fut.data.Quiz;

import java.util.List;

public class AdminQuizAdapter extends RecyclerView.Adapter<AdminQuizAdapter.ViewHolder> {

    private final List<Quiz> quizzes;
    private final LayoutInflater mInflater;

    private final Context context;

    public AdminQuizAdapter(Context context, List<Quiz> quizzes) {
        this.mInflater = LayoutInflater.from(context);
        this.quizzes = quizzes;
        this.context = context;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.admin_quiz_listitem, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(quizzes.get(position).getName());
        String coins = quizzes.get(position).getCoins() + " Coins";
        holder.coins.setText(coins);
        holder.quiz.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddQuestionsToQuizActivity.class);
            intent.putExtra("quiz", quizzes.get(position).toJson().toString());
            context.startActivity(intent);
        });
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView coins;
        RelativeLayout quiz;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            coins = itemView.findViewById(R.id.coins);
            quiz = itemView.findViewById(R.id.quiz);
        }
    }
}
