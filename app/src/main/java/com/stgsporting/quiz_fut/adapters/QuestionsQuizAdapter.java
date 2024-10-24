package com.stgsporting.quiz_fut.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz_fut_draft.R;
import com.stgsporting.quiz_fut.data.Question;
import com.stgsporting.quiz_fut.data.Quiz;

public class QuestionsQuizAdapter extends RecyclerView.Adapter<QuestionsQuizAdapter.ViewHolder> {

    private final Quiz quiz;
    private final LayoutInflater mInflater;

    public QuestionsQuizAdapter(Context context, Quiz quiz) {
        this.mInflater = LayoutInflater.from(context);
        this.quiz = quiz;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.quiz_listitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Question q = this.quiz.getQuestions().get(position);

        holder.title.setText(q.getTitle());
    }

    @Override
    public int getItemCount() {
        return quiz.getQuestions().size();
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
