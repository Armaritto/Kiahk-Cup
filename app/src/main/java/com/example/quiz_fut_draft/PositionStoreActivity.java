package com.example.quiz_fut_draft;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class PositionStoreActivity extends AppCompatActivity {
    private String Name;
    private String ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_store);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent1 = getIntent();
        ID = intent1.getStringExtra("ID");
        Name = intent1.getStringExtra("Name");
        String dbURL = intent1.getStringExtra("Database");
        String storageURL = intent1.getStringExtra("Storage");
        setupHeader(FirebaseDatabase.getInstance(dbURL).getReference("/elmilad25/Users"));

        FirebaseDatabase database = FirebaseDatabase.getInstance(dbURL);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        DatabaseReference ref = database.getReference();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                DataSnapshot snapshot = data.child("elmilad25").child("CardPosition");
                ArrayList<Position> positions = new ArrayList<>();
                ArrayList<String> positionsIds = new ArrayList<>();
                for (DataSnapshot p : snapshot.getChildren()) {
                    String id = p.getKey();
                    Position position = new Position();
                    position.setId(id);
                    position.setPosition(p.child("Position").getValue().toString());
                    position.setPrice(Integer.parseInt(p.child("Price").getValue().toString()));
                    positions.add(position);
                    positionsIds.add(id);
                }

                DataSnapshot ownedPositions = data.child(
                        "/elmilad25/Users").child(ID).child("Owned Positions");
                for (DataSnapshot position : ownedPositions.getChildren()) {
                    if (Boolean.parseBoolean(position.child("Owned").getValue().toString())) {
                        String positionId = position.getKey();
                        if (positionsIds.contains(positionId)) {
                            positions.get(positionsIds.indexOf(positionId)).setOwned(true);
                        }
                    }
                }

               PositionStoreAdapter adapter = new PositionStoreAdapter(
                       PositionStoreActivity.this, positions, database, ID);
               recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(PositionStoreActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupHeader(DatabaseReference ref) {
        TextView stars = findViewById(R.id.rating);
        TextView coins = findViewById(R.id.coins);
        TextView name = findViewById(R.id.nametextview);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setText(Name);
                stars.setText(Objects.requireNonNull(snapshot.child(ID).child("Stars").getValue()).toString());
                coins.setText(Objects.requireNonNull(snapshot.child(ID).child("Coins").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}