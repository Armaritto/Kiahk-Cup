package com.stgsporting.cup.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stgsporting.cup.R;
import com.stgsporting.cup.data.Question;
import com.stgsporting.cup.data.Quiz;

import java.util.ArrayList;
import java.util.List;

public class AddQuestionAdapter extends RecyclerView.Adapter<AddQuestionAdapter.ViewHolder> {

    private final Quiz quiz;
    private final LayoutInflater mInflater;

    public AddQuestionAdapter(Context context, Quiz quiz) {
        this.mInflater = LayoutInflater.from(context);
        this.quiz = quiz;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.add_question_listitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Question q = quiz.getQuestions().get(position);

        holder.title.setText(q.getTitle());
        if(q.getPoints() >= 0) {
            holder.points.setText(String.format("%s", q.getPoints()));
        }

        for (int i = 0; i < Math.min(4, q.getOptions().size()); i++) {
            holder.options.get(i).setText(q.getOptions().get(i));
            holder.optionsCorrect.get(i).setChecked(i == q.getCorrectOption());
        }

        holder.title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                q.setTitle(s.toString());
            }
        });

        holder.points.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    q.setPoints(0);
                    return;
                }

                q.setPoints(Integer.parseInt(s.toString()));
            }
        });

        for (int i = 0; i < 4; i++) {
            int finalI = i;
            holder.options.get(i).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    if (finalI >= q.getOptions().size()) {
                        q.addOption(s.toString());
                    } else {
                        q.getOptions().set(finalI, s.toString());
                    }
                }
            });

            holder.optionsCorrect.get(i).setOnClickListener((v) -> {
                q.setCorrectOption(finalI);
            });
        }

        holder.delete.setOnClickListener((v) -> {
            quiz.removeQuestion(q);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return quiz.getQuestions().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        EditText title;
        RadioGroup correctOption;
        EditText points;

        List<EditText> options;
        List<RadioButton> optionsCorrect;

        ImageButton delete;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.question_title_editor);
            correctOption = itemView.findViewById(R.id.correct_option_radio);
            points = itemView.findViewById(R.id.points_editor);
            delete = itemView.findViewById(R.id.delete_question_button);

            options = new ArrayList<>();
            optionsCorrect = new ArrayList<>();

            options.add(itemView.findViewById(R.id.option1_text));
            options.add(itemView.findViewById(R.id.option2_text));
            options.add(itemView.findViewById(R.id.option3_text));
            options.add(itemView.findViewById(R.id.option4_text));

            optionsCorrect.add(itemView.findViewById(R.id.option1_radio));
            optionsCorrect.add(itemView.findViewById(R.id.option2_radio));
            optionsCorrect.add(itemView.findViewById(R.id.option3_radio));
            optionsCorrect.add(itemView.findViewById(R.id.option4_radio));
        }
    }
}
