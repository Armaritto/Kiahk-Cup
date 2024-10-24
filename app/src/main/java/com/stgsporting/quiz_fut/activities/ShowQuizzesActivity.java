package com.stgsporting.quiz_fut.activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz_fut_draft.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stgsporting.quiz_fut.adapters.QuizzesAdapter;
import com.stgsporting.quiz_fut.data.Quiz;
import com.stgsporting.quiz_fut.helpers.Http;
import com.stgsporting.quiz_fut.helpers.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ShowQuizzesActivity extends AppCompatActivity {

    private String[] data;

    RecyclerView quizzesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mosab2a);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LoadingDialog loadingDialog = new LoadingDialog(this);

        data = getIntent().getStringArrayExtra("Data");

        quizzesListView = findViewById(R.id.mosab2at_list);
        FirebaseDatabase database = FirebaseDatabase.getInstance(data[1]);
        DatabaseReference ref = database.getReference();
        setupHeader(ref);
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

        RecyclerView.Adapter<QuizzesAdapter.ViewHolder> adapter = new QuizzesAdapter(ShowQuizzesActivity.this, quizzes);
        quizzesListView.setAdapter(adapter);
        loadingDialog.dismiss();
    }

    private void setupHeader(DatabaseReference ref) {
        TextView stars = findViewById(R.id.rating);
        TextView coins = findViewById(R.id.coins);
        TextView name = findViewById(R.id.nametextview);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String new_name = Arrays.stream(data[0].split("\\s+"))
                        .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                        .collect(Collectors.joining(" "));
                name.setText(new_name);
                snapshot = snapshot.child("/elmilad25/Users");
                stars.setText(Objects.requireNonNull(snapshot.child(data[0]).child("Stars").getValue()).toString());
                coins.setText(Objects.requireNonNull(snapshot.child(data[0]).child("Coins").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}