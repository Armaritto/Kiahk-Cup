package com.stgsporting.quiz_fut.activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz_fut_draft.R;
import com.stgsporting.quiz_fut.adapters.QuestionsQuizAdapter;
import com.stgsporting.quiz_fut.data.Quiz;
import com.stgsporting.quiz_fut.helpers.Header;
import com.stgsporting.quiz_fut.helpers.Http;
import com.stgsporting.quiz_fut.helpers.LoadingDialog;

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
        }catch (JSONException ignored) {finish();}

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
                    } catch (JSONException e) {finish();}
                    return null;
                });
    }

    private void rebuild() {
        RecyclerView.Adapter<QuestionsQuizAdapter.ViewHolder> adapter = new QuestionsQuizAdapter(this, quiz);
        RecyclerView quizQuestions = findViewById(R.id.quiz_questions);

        quizQuestions.setAdapter(adapter);
    }
}
