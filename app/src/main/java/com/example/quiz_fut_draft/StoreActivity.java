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

public class StoreActivity extends AppCompatActivity {
    private String name;
    private String ID;
    private static int coins;
    private StoreAdapter adapter;
    private String selectedPosition;
    private String cardPosition;
    private String grade;
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
        name = intent1.getStringExtra("Name");
        grade = intent1.getStringExtra("Grade");
        selectedPosition = getIntent().getStringExtra("Card");
        cardPosition = selectedPosition;

        if (selectedPosition.equals("LCM") || selectedPosition.equals("RCM"))
            selectedPosition = "CM";
        else if (selectedPosition.equals("LCB") || selectedPosition.equals("RCB"))
            selectedPosition = "CB";


        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        setupHeader(database.getReference(Users_Path.getPath(grade)));

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DataSnapshot storeData = snapshot.child("elmilad25").child("Store");
                DataSnapshot ownedData = snapshot.child(Users_Path.getPath(grade)).child(ID).child("Owned Cards");
                DataSnapshot lineupData = snapshot.child(Users_Path.getPath(grade)).child(ID).child("Lineup");

                ArrayList<Card> cards = new ArrayList<>();
                for (DataSnapshot cardData : storeData.getChildren()) {
                    String cardID = cardData.getKey();
                    String position = cardData.child("Position").getValue().toString();
                    if (!position.equals(selectedPosition)) continue;
                    Card card = new Card();
                    card.setID(cardID);
                    card.setPrice(Integer.parseInt(cardData.child("Price").getValue().toString()));
                    card.setPosition(position);
                    card.setImageLink(cardData.child("Image").getValue().toString());
                    card.setRating(cardData.child("Rating").getValue().toString());
                    if (ownedData.hasChild(cardID) &&
                            Boolean.parseBoolean(ownedData.child(cardID).getValue().toString()))
                        card.setOwned(true);
                    else
                        card.setOwned(false);
                    boolean flag = false;
                    for (DataSnapshot usedCard : lineupData.getChildren()) {
                        if (card.getID().equals(usedCard.getValue().toString())) flag = true;
                    }

                    if (!flag) cards.add(card);
                }
                adapter = new StoreAdapter(StoreActivity.this, cards, coins, database, ID, grade, cardPosition);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(StoreActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                name.setText(StoreActivity.this.name);
                stars.setText(Objects.requireNonNull(snapshot.child(ID).child("Stars").getValue()).toString());
                StoreActivity.coins = Integer.parseInt(Objects.requireNonNull(snapshot.child(ID).child("Coins").getValue()).toString());
                coins.setText(Objects.requireNonNull(snapshot.child(ID).child("Coins").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}