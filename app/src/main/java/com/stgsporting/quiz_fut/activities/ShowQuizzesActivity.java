package com.stgsporting.quiz_fut.activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz_fut_draft.R;
import com.stgsporting.quiz_fut.adapters.QuizzesAdapter;
import com.stgsporting.quiz_fut.data.Quiz;
import com.stgsporting.quiz_fut.helpers.Header;
import com.stgsporting.quiz_fut.helpers.Http;
import com.stgsporting.quiz_fut.helpers.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ShowQuizzesActivity extends AppCompatActivity {

    RecyclerView quizzesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_quizzes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LoadingDialog loadingDialog = new LoadingDialog(this);

        String[] data = getIntent().getStringArrayExtra("Data");
        Header.render(this, Objects.requireNonNull(data));

        quizzesListView = findViewById(R.id.quizzes_list);
        int numberOfColumns = 2;
        quizzesListView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        Http.get(Uri.parse(Http.URL + "/quizzes"), Map.of("school_year_id", data[3], "user", data[0]))
                .expectsJson()
                .sendAsync().thenApply(res -> {
                    runOnUiThread(() -> handleResponse(res, loadingDialog));
                    return null;
                });
    }

    private void handleResponse(Http.Response res, LoadingDialog loadingDialog) {
        loadingDialog.dismiss();

        if (res.getCode() != 200) {
            Toast.makeText(ShowQuizzesActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject responseData = res.getJson();
        List<Quiz> quizzes = new ArrayList<>();
        try {
            JSONArray quizzesJSON = responseData.getJSONArray("data");
            for (int i = 0; i < quizzesJSON.length(); i++) {
                quizzes.add(Quiz.fromJson(quizzesJSON.getJSONObject(i)));
            }
        }catch (JSONException ignored) {}

        RecyclerView.Adapter<QuizzesAdapter.ViewHolder> adapter = new QuizzesAdapter(this, quizzes);
        quizzesListView.setAdapter(adapter);
        loadingDialog.dismiss();
    }
}