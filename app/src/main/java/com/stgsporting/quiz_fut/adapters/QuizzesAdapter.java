package com.stgsporting.quiz_fut.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.stgsporting.quiz_fut.R;
import com.stgsporting.quiz_fut.activities.QuizActivity;
import com.stgsporting.quiz_fut.data.Quiz;

import java.util.List;

public class QuizzesAdapter extends RecyclerView.Adapter<QuizzesAdapter.ViewHolder> {

    private final List<Quiz> quizzes;
    private final LayoutInflater mInflater;
    private final Activity activity;

    public QuizzesAdapter(Activity activity, List<Quiz> quizzes) {
        this.mInflater = LayoutInflater.from(activity);
        this.quizzes = quizzes;
        this.activity = activity;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.quiz_listitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Quiz q = this.quizzes.get(position);

        holder.title.setText(q.getName());

        String coins = q.getCoins() + " Coins";
        holder.coins.setText(coins);

        holder.quiz.setOnClickListener(v-> {
            if(q.isSolved()) {
                Toast.makeText(activity, "حليت المسابقة قبل كده", Toast.LENGTH_LONG).show();

                return;
            }

            Intent intent = new Intent(activity, QuizActivity.class);
            intent.putExtra("quiz", q.toJson().toString());
            intent.putExtra("Data", activity.getIntent().getStringArrayExtra("Data"));
            activity.startActivity(intent);
        });

        if(q.isSolved()) {
            holder.isSolvedIcon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView coins;
        RelativeLayout quiz;
        View isSolvedIcon;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            coins = itemView.findViewById(R.id.coins);
            quiz = itemView.findViewById(R.id.mosab2a);
            isSolvedIcon = itemView.findViewById(R.id.is_solved_icon);
        }
    }
}
