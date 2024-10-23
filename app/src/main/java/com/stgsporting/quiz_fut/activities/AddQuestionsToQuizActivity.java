package com.stgsporting.quiz_fut.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quiz_fut_draft.R;
import com.stgsporting.quiz_fut.data.Quiz;

import org.json.JSONException;
import org.json.JSONObject;

public class AddQuestionsToQuizActivity extends AppCompatActivity {

    Quiz quiz;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_questions_to_quiz);

        try {
            quiz = Quiz.fromJson(new JSONObject(getIntent().getStringExtra("quiz")));
        }catch (JSONException ignored) {
            finish();
        }

        TextView quizName = findViewById(R.id.quiz_name_title);
        quizName.setText(quiz.getName());
    }
}
