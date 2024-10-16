package com.stgsporting.quiz_fut.activities;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stgsporting.quiz_fut.data.Card;
import com.stgsporting.quiz_fut.adapters.StoreAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class StoreActivity extends AppCompatActivity {

    private String[] data;
    private static int coins;
    private StoreAdapter adapter;
    private String selectedPosition;
    private String cardPosition;
    private int imagesToLoad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        data = getIntent().getStringArrayExtra("Data");
        selectedPosition = getIntent().getStringExtra("Card");
        cardPosition = selectedPosition;

        if (selectedPosition.equals("LCM") || selectedPosition.equals("RCM"))
            selectedPosition = "CM";
        else if (selectedPosition.equals("LCB") || selectedPosition.equals("RCB"))
            selectedPosition = "CB";


        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        FirebaseDatabase database = FirebaseDatabase.getInstance(data[1]);
        FirebaseStorage storage = FirebaseStorage.getInstance(data[2]);
        DatabaseReference ref = database.getReference();

        setupHeader(database.getReference("/elmilad25/Users"));

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DataSnapshot storeData = snapshot.child("elmilad25").child("Store");
                DataSnapshot ownedData = snapshot.child("/elmilad25/Users").child(data[0]).child("Owned Cards");
                DataSnapshot lineupData = snapshot.child("/elmilad25/Users").child(data[0]).child("Lineup");

                ArrayList<Card> cards = new ArrayList<>();
                for (DataSnapshot cardData : storeData.getChildren()) {
                    String cardID = cardData.getKey();
                    String position = cardData.child("Position").getValue().toString();
                    if (!position.equals(selectedPosition)) continue;
                    Card card = new Card();
                    card.setID(cardID);
                    card.setPrice(Integer.parseInt(cardData.child("Price").getValue().toString()));
                    card.setPosition(position);
                    String imagePath = cardData.child("Image").getValue().toString();
                    card.setImagePath(imagePath);
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

                imagesToLoad = cards.size();

                for (int i=0;i<cards.size();i++) {
                    Card c = cards.get(i);
                    StorageReference storageRef = storage.getReference().child(c.getImagePath());
                    final int j = i;
                    storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String downloadUrl = uri.toString();
                                cards.get(j).setImageLink(downloadUrl);
                                imagesToLoad--;
                                if (imagesToLoad==0) {
                                    adapter = new StoreAdapter(
                                            StoreActivity.this, cards, coins, database, data[0], cardPosition);
                                    recyclerView.setAdapter(adapter);
                                }
                            })
                            .addOnFailureListener(e -> Toast.makeText(StoreActivity.this,
                                    "Failed to get download URL", Toast.LENGTH_SHORT).show());
                }

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
                name.setText(data[0]);
                stars.setText(Objects.requireNonNull(snapshot.child(data[0]).child("Stars").getValue()).toString());
                StoreActivity.coins = Integer.parseInt(Objects.requireNonNull(snapshot.child(data[0]).child("Coins").getValue()).toString());
                coins.setText(Objects.requireNonNull(snapshot.child(data[0]).child("Coins").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}