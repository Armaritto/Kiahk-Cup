package com.stgsporting.quiz_fut.activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz_fut_draft.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.stgsporting.quiz_fut.adapters.AddQuestionAdapter;
import com.stgsporting.quiz_fut.data.Question;
import com.stgsporting.quiz_fut.data.Quiz;
import com.stgsporting.quiz_fut.helpers.Http;
import com.stgsporting.quiz_fut.helpers.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AddQuestionsToQuizActivity extends AppCompatActivity {

    Quiz quiz;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_questions_to_quiz);

        LoadingDialog loadingDialog = new LoadingDialog(this);

        try {
            quiz = Quiz.fromJson(new JSONObject(getIntent().getStringExtra("quiz")));
        } catch (JSONException e) {
            finish();
            return;
        }

        Http.get(Uri.parse(Http.URL + "/quizzes/" + quiz.getId()))
                .sendAsync().thenApply((res) -> {
                    loadingDialog.dismiss();
                    try {
                        if (res.getCode() == 200) {
                            JSONObject data = res.getJson();
                            quiz = Quiz.fromJson(data.getJSONObject("data"));
                            runOnUiThread(this::rebuild);
                        }
                    } catch (JSONException e) {
                        finish();
                    }
                    return null;
                });

        FloatingActionButton addQuestion = findViewById(R.id.add_question_fab);
        FloatingActionButton saveQuestions = findViewById(R.id.save_questions_fab);

        addQuestion.setOnClickListener((v) -> {
            quiz.addEmptyQuestion();
            rebuild();
        });

        saveQuestions.setOnClickListener((v) -> {
            loadingDialog.show();
            JSONObject data = new JSONObject();
            try {
                JSONArray questions = new JSONArray();
                for (Question question : quiz.getQuestions()) {
                    questions.put(question.toJson());
                }
                data.put("questions", questions);
                data.put("_method", "PATCH");
            } catch (JSONException ignored) {}

            Http.post(Uri.parse(Http.URL + "/quizzes/" + quiz.getId() + "/questions"))
                    .expectsJson()
                    .addData(data)
                    .sendAsync().thenApply((res) -> {
                        runOnUiThread(() -> {
                            loadingDialog.dismiss();
                            if (res.getCode() == 200) {
                                Toast.makeText(this, "Questions saved", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(this, "Failed to save questions", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return null;
                    });
        });
    }

    private void rebuild() {
        TextView quizName = findViewById(R.id.quiz_name_title);
        quizName.setText(quiz.getName());

        if(quiz.getQuestions().isEmpty()) {
            quiz.addEmptyQuestion();
        }

        RecyclerView questions = findViewById(R.id.add_questions_list);
        RecyclerView.Adapter<AddQuestionAdapter.ViewHolder> questionsAdapter = new AddQuestionAdapter(this, quiz);
        questions.setAdapter(questionsAdapter);
    }
}
