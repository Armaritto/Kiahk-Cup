package com.example.quiz_fut_draft;

import android.content.Intent;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class CardStoreActivity extends AppCompatActivity {
    private String Name;
    private String ID;
    private String ownedCard = "icon1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent1 = getIntent();
        ID = intent1.getStringExtra("ID");
        Name = intent1.getStringExtra("Name");

        setupHeader(FirebaseDatabase.getInstance().getReference("/Login"));

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        DatabaseReference ref = database.getReference("/CardIcon");

        ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<String> links = new ArrayList<>();
                        ArrayList<Double> prices = new ArrayList<>();
                        for (DataSnapshot card : snapshot.getChildren()) {
                            String cardName = card.getKey();
                            if (!ownedCard.equals(cardName)) {
                                links.add(card.child("Link").getValue().toString());
                                prices.add(Double.parseDouble(card.child("Price").getValue().toString()));
                            }
                        }

                         CardStoreAdapter adapter = new CardStoreAdapter(
                             CardStoreActivity.this, finalList, coins, database, team, cardStoreCheck, ID);
                        recyclerView.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(CardStoreActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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