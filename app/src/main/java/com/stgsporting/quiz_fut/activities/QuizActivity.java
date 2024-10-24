package com.stgsporting.quiz_fut.activities;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quiz_fut_draft.R;
import com.stgsporting.quiz_fut.data.Quiz;
import com.stgsporting.quiz_fut.helpers.Http;
import com.stgsporting.quiz_fut.helpers.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class QuizActivity extends AppCompatActivity {

    Quiz quiz;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        LoadingDialog loadingDialog = new LoadingDialog(this);

        try {
            quiz = Quiz.fromJson(new JSONObject(getIntent().getStringExtra("quiz")));
        }catch (JSONException ignored) {finish();}

        Http.get(Uri.parse(Http.URL + "/quizzes/" + quiz.getId()))
                .expectsJson()
                .sendAsync().thenApply((res) -> {
                    loadingDialog.dismiss();
                    try {
                        if (res.getCode() == 200) {
                            JSONObject data = res.getJson();
                            quiz = Quiz.fromJson(data.getJSONObject("data"));
                            runOnUiThread(this::rebuild);
                        }
                    } catch (JSONException e) {finish();}
                    return null;
                });
    }

    private void rebuild() {
        System.out.println(quiz.getName());
    }
}
