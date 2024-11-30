package com.stgsporting.cup.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stgsporting.cup.R;
import com.stgsporting.cup.data.Question;
import com.stgsporting.cup.data.Quiz;
import com.stgsporting.cup.helpers.ItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class QuestionsQuizAdapter extends RecyclerView.Adapter<QuestionsQuizAdapter.ViewHolder> {

    private final Quiz quiz;
    private final LayoutInflater mInflater;
    private final Context context;
    private ItemClickListener[] optionSelected;
    private OptionsAdapter[] adapter;
//    private final List<ItemClickListener> optionSelected;
//    private final List<OptionsAdapter> adapter;

    public QuestionsQuizAdapter(Context context, Quiz quiz) {
        this.mInflater = LayoutInflater.from(context);
        this.quiz = quiz;
        this.context = context;

        optionSelected = new ItemClickListener[quiz.getQuestions().size()];
        adapter = new OptionsAdapter[quiz.getQuestions().size()];
//        optionSelected = new ArrayList<>(quiz.getQuestions().size());
//        adapter = new ArrayList<>(quiz.getQuestions().size());
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

//        optionSelected.add((optionPosition) -> {
//            holder.options.post(() -> adapter.get(position).notifyDataSetChanged());
//            q.setSelectedOption(optionPosition);
//        });
//
//        adapter.add(new OptionsAdapter(context, q.getOptions(), optionSelected.get(position)));

        if (optionSelected[position] == null) {
            optionSelected[position] = (optionPosition) -> {
                holder.options.post(() -> adapter[position].notifyDataSetChanged());
                q.setSelectedOption(optionPosition);
            };
        }

        if (adapter[position] == null)
            adapter[position] = new OptionsAdapter(context, q.getOptions(), optionSelected[position]);

        holder.options.setAdapter(adapter[position]);
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
