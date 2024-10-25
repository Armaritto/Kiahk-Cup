package com.stgsporting.quiz_fut.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stgsporting.quiz_fut.R;
import com.stgsporting.quiz_fut.data.Question;
import com.stgsporting.quiz_fut.data.Quiz;
import com.stgsporting.quiz_fut.helpers.ItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class QuestionsQuizAdapter extends RecyclerView.Adapter<QuestionsQuizAdapter.ViewHolder> {

    private final Quiz quiz;
    private final LayoutInflater mInflater;
    private final Context context;
    private final List<ItemClickListener> optionSelected;
    private final List<OptionsAdapter> adapter;

    public QuestionsQuizAdapter(Context context, Quiz quiz) {
        this.mInflater = LayoutInflater.from(context);
        this.quiz = quiz;
        this.context = context;

        optionSelected = new ArrayList<>();
        adapter = new ArrayList<>();
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.question_listitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Question q = this.quiz.getQuestions().get(position);

        holder.title.setText(q.getTitle());

        optionSelected.add((optionPosition) -> {
            holder.options.post(() -> adapter.get(position).notifyDataSetChanged());
            q.setSelectedOption(optionPosition);
        });

        adapter.add(new OptionsAdapter(context, q.getOptions(), optionSelected.get(position)));

        holder.options.setAdapter(adapter.get(position));
    }

    @Override
    public int getItemCount() {
        return quiz.getQuestions().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        RecyclerView options;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.question_title);
            options = itemView.findViewById(R.id.options);
        }
    }
}
