package com.stgsporting.quiz_fut.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz_fut_draft.R;
import com.stgsporting.quiz_fut.adapters.AdminQuizAdapter;
import com.stgsporting.quiz_fut.data.Quiz;
import com.stgsporting.quiz_fut.helpers.Http;
import com.stgsporting.quiz_fut.helpers.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class AddQuizActivity extends AppCompatActivity {

    private String[] data;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quiz);

        data = getIntent().getStringArrayExtra("Data");

        LoadingDialog loadingDialog = new LoadingDialog(this);

        Http.get(
                Uri.parse(Http.URL + "/quizzes"),
                        Map.of("school_year_id", data[3], "user", data[0], "admin", "1")
                ).expectsJson()
                .sendAsync()
                .thenApply(res -> {
                    runOnUiThread(() -> handleResponse(res, loadingDialog));
                    return null;
                });


        AppCompatEditText quizName = findViewById(R.id.quiz_name_text);
        TextView startedAtdate = findViewById(R.id.started_at_date);
        startedAtdate.setText("-- Select Date --");

        startedAtdate.setOnClickListener((v) -> {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        mHour = c.get(Calendar.HOUR_OF_DAY);
                        mMinute = c.get(Calendar.MINUTE);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                                (view1, hourOfDay, minute) -> {
                                    String minuteStr = String.format("%2s", minute).replace(' ', '0');
                                    String hourStr = String.format("%2s", hourOfDay).replace(' ', '0');
                                    startedAtdate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + " " + hourStr + ":" + minuteStr);
                                }, mHour, mMinute, false);

                        timePickerDialog.show();
                    }, mYear, mMonth, mDay);

            datePickerDialog.show();
        });

        Button submit = findViewById(R.id.create_quiz_btn);

        submit.setOnClickListener((v) -> {
            if (quizName.getText() == null || startedAtdate.getText() == null) {
                return;
            }

            loadingDialog.show();

            Http request = Http
                    .post(Uri.parse(Http.URL + "/quizzes"))
                    .expectsJson();
            JSONObject body = new JSONObject();
            try {
                body.put("name", quizName.getText().toString());
                body.put("started_at", startedAtdate.getText().toString());
                body.put("school_year_id", data[3]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            request
                    .addData(body)
                    .sendAsync()
                    .thenApply((res) -> {
                        runOnUiThread(() -> {
                            if (res.getCode() == 200) {
                                JSONObject responseData = res.getJson();
                                try {
                                    Intent intent = new Intent(this, AddQuestionsToQuizActivity.class);
                                    intent.putExtra("quiz", responseData.getJSONObject("data").toString());
                                    intent.putExtra("Data", data);
                                    this.startActivity(intent);
                                }catch (JSONException e) {e.printStackTrace();}
                            }

                            loadingDialog.dismiss();
                        });
                        return null;
                    });
        });
    }

    private void handleResponse(Http.Response res, LoadingDialog loadingDialog) {
        if (res.getCode() != 200) {
            return;
        }

        JSONObject responseData = res.getJson();
        List<Quiz> quizzes = new ArrayList<>();
        try {
            JSONArray quizzesArray = responseData.getJSONArray("data");
            for (int i = 0; i < quizzesArray.length(); i++) {
                JSONObject quiz = quizzesArray.getJSONObject(i);
                quizzes.add(Quiz.fromJson(quiz));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RecyclerView quizzesList = findViewById(R.id.quizzes_list);

        AdminQuizAdapter adapter = new AdminQuizAdapter(this, quizzes);
        quizzesList.setAdapter(adapter);

        loadingDialog.dismiss();
    }
}
