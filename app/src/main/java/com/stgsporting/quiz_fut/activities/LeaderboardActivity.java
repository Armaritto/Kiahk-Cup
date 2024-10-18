package com.stgsporting.quiz_fut.activities;

import android.os.Bundle;

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
import com.google.firebase.storage.FirebaseStorage;
import com.stgsporting.quiz_fut.adapters.LeaderboardAdapter;
import com.stgsporting.quiz_fut.data.Lineup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LeaderboardActivity extends AppCompatActivity {

    private String[] data;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        data = getIntent().getStringArrayExtra("Data");

        FirebaseDatabase database = FirebaseDatabase.getInstance(data[1]);
        storage = FirebaseStorage.getInstance(data[2]);
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
                // Display the sorted Lineups in the Leaderboard UI
                /*
                -------------------------------------------
                | 1. User1: 100 OVR          view lineup  |
                | 2. User2: 90 OVR           view lineup  |
                | 3. User3: 80 OVR           view lineup  |
                | 4. User4: 70 OVR           view lineup  |
                -------------------------------------------
                 */
                LeaderboardAdapter adapter = new LeaderboardAdapter(LeaderboardActivity.this, lineups,
                        data, userData, snapshot, storage);
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
                name.setText(data[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}