package com.stgsporting.cup.activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.stgsporting.cup.R;
import com.stgsporting.cup.adapters.QuestionsQuizAdapter;
import com.stgsporting.cup.data.Question;
import com.stgsporting.cup.data.Quiz;
import com.stgsporting.cup.helpers.Header;
import com.stgsporting.cup.helpers.Http;
import com.stgsporting.cup.helpers.LoadingDialog;
import com.stgsporting.cup.helpers.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;

public class QuizActivity extends AppCompatActivity {

    Quiz quiz;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        LoadingDialog loadingDialog = new LoadingDialog(this);

        try {
            quiz = Quiz.fromJson(new JSONObject(getIntent().getStringExtra("quiz")));
        } catch (JSONException ignored) {
            finish();
        }

        String[] data = getIntent().getStringArrayExtra("Data");
        Header.render(this, Objects.requireNonNull(data));

        Http.get(Uri.parse(Http.URL + "/quizzes/" + quiz.getId()), Map.of("user", data[0]))
                .expectsJson()
                .sendAsync().thenApply((res) -> {
                    loadingDialog.dismiss();
                    try {
                        if (res.getCode() == 200) {
                            JSONObject responseData = res.getJson();
                            quiz = Quiz.fromJson(responseData.getJSONObject("data"));
                            runOnUiThread(this::rebuild);
                        } else {
                            Toast.makeText(this, "حليت المسابقة قبل كده", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } catch (JSONException e) {
                        finish();
                    }
                    return null;
                });

        FloatingActionButton submit = findViewById(R.id.submit_quiz);

        submit.setOnClickListener((__) -> {
            if (!NetworkUtils.isOnline(this)) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
            JSONObject requestData = getJsonObject(data);

            loadingDialog.show();
            Http.post(Uri.parse(Http.URL + "/quizzes/" + quiz.getId() + "/submit"))
                    .expectsJson()
                    .addData(requestData)
                    .sendAsync().thenApply((res) -> {
                        runOnUiThread(() -> {
                            loadingDialog.dismiss();

                            Toast.makeText(
                                    this,
                                    res.getCode() == 200 ? "تم ارسال الاجابات" : "حليت المسابقة قبل كده",
                                    Toast.LENGTH_LONG
                            ).show();

                            finish();
                        });
                        return null;
                    });
        });
    }

    @NonNull
    private JSONObject getJsonObject(String[] data) {
        JSONObject requestData = new JSONObject();
        JSONArray answers = new JSONArray();

        try {
            for (Question question : quiz.getQuestions()) {
                JSONObject answer = new JSONObject();
                answer.put("question_id", question.getId());
                answer.put("option", question.getSelectedOption());
                answers.put(answer);
            }

            requestData.put("user", data[0]);
            requestData.put("answers", answers);
        } catch (JSONException ignored) {
        }
        return requestData;
    }

    private void rebuild() {
        RecyclerView.Adapter<QuestionsQuizAdapter.ViewHolder> adapter = new QuestionsQuizAdapter(this, quiz);
        RecyclerView quizQuestions = findViewById(R.id.quiz_questions);

        quizQuestions.setAdapter(adapter);
    }
}
