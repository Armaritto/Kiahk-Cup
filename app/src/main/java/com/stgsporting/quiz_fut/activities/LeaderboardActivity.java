package com.stgsporting.quiz_fut.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stgsporting.quiz_fut.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.stgsporting.quiz_fut.adapters.LeaderboardAdapter;
import com.stgsporting.quiz_fut.data.Lineup;
import com.stgsporting.quiz_fut.helpers.LoadingDialog;

import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class LeaderboardActivity extends AppCompatActivity {

    private String[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LoadingDialog loadingDialog = new LoadingDialog(this);

        data = getIntent().getStringArrayExtra("Data");

        FirebaseDatabase database = FirebaseDatabase.getInstance(data[1]);
        FirebaseStorage storage = FirebaseStorage.getInstance(data[2]);
        DatabaseReference ref = database.getReference();

        setupHeader(ref.child("/elmilad25/Users"));

        RecyclerView recyclerView = findViewById(R.id.recycler_view_lineups);
        int numberOfColumns = 1;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot userData = snapshot.child("/elmilad25/Users");
                HashMap<String,Integer> allUsersRatings = new HashMap<>();
                for (DataSnapshot aUserData : snapshot.child("/elmilad25/Users").getChildren()) {
                    if (aUserData.getKey().equals("Admin")) continue;
                    if (aUserData.hasChild("Points")) {
                        int aUserPoints = Integer.parseInt(aUserData.child("Points").getValue().toString());
                        allUsersRatings.put(aUserData.getKey(),aUserPoints);
                    }
                }
                List<Map.Entry<String, Integer>> list = new ArrayList<>(allUsersRatings.entrySet());
                list.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
                ArrayList<Lineup> lineups = getLineups(list);
                LeaderboardAdapter adapter = new LeaderboardAdapter(LeaderboardActivity.this, lineups,
                        data, userData, snapshot, storage, loadingDialog);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(LeaderboardActivity.this, "Failed to read value.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private @NonNull ArrayList<Lineup> getLineups(List<Map.Entry<String, Integer>> list) {
        ArrayList<Lineup> lineups = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : list) {
            if(entry.getValue() > 0){
                Lineup lineup = new Lineup();
                lineup.setID(entry.getKey());
                lineup.setOVR(entry.getValue().toString());
                lineups.add(lineup);
            }
        }
        return lineups;
    }

    private void setupHeader(DatabaseReference ref) {
        TextView stars = findViewById(R.id.rating);
        TextView coins = findViewById(R.id.coins);
        TextView name = findViewById(R.id.nametextview);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stars.setText(Objects.requireNonNull(snapshot.child(data[0]).child("Stars").getValue()).toString());
                coins.setText(Objects.requireNonNull(snapshot.child(data[0]).child("Coins").getValue()).toString());
                String new_name = Arrays.stream(data[0].split("\\s+"))
                        .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                        .collect(Collectors.joining(" "));
                name.setText(new_name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}