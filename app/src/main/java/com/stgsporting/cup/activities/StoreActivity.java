package com.stgsporting.cup.activities;
import androidx.activity.OnBackPressedCallback;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stgsporting.cup.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stgsporting.cup.data.Card;
import com.stgsporting.cup.adapters.StoreAdapter;
import com.stgsporting.cup.helpers.Header;
import com.stgsporting.cup.helpers.LoadingDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

        LoadingDialog loadingDialog = new LoadingDialog(this);

        data = getIntent().getStringArrayExtra("Data");
        selectedPosition = getIntent().getStringExtra("Card");
        cardPosition = selectedPosition;

        if (selectedPosition.equals("LCM") || selectedPosition.equals("RCM"))
            selectedPosition = "CM";
        else if (selectedPosition.equals("LCB") || selectedPosition.equals("RCB"))
            selectedPosition = "CB";

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(StoreActivity.this, LineupActivity.class);
                intent.putExtra("Data",data);
                intent.putExtra("OtherLineup",false);
                startActivity(intent);
                finish();
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        FirebaseDatabase database = FirebaseDatabase.getInstance(data[1]);
        FirebaseStorage storage = FirebaseStorage.getInstance(data[2]);
        DatabaseReference ref = database.getReference();

        Header.render(this, Objects.requireNonNull(data));

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                coins = Integer.parseInt(Objects.requireNonNull(snapshot.child("elmilad25").child("Users").child(data[0]).child("Coins").getValue()).toString());
                DataSnapshot storeData = snapshot.child("elmilad25").child("Store");
                DataSnapshot ownedData = snapshot.child("/elmilad25/Users").child(data[0]).child("Owned Cards");
                DataSnapshot userData = snapshot.child("/elmilad25/Users").child(data[0]);
                ArrayList<Card> cards = new ArrayList<>();
                for (DataSnapshot cardData : storeData.getChildren()) {
                    String cardID = cardData.getKey();
                    if (!cardData.hasChild("Position")) continue;
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
                    else {
                        card.setOwned(false);
                        if (cardData.hasChild("Available") && !Boolean.parseBoolean(cardData.child("Available").getValue().toString()))
                            continue;
                    }
                    if(userData.child("Lineup").child(position).getValue() != null &&
                        userData.child("Lineup").child(position).getValue().toString().equals(cardID))
                        card.setInLineup(true);
                    cards.add(card);
                }

                cards.sort(Comparator.comparingInt(Card::getPrice));

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
                                            StoreActivity.this, cards, coins, database, data[0], cardPosition, storage);
                                    recyclerView.setAdapter(adapter);
                                    loadingDialog.dismiss();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(StoreActivity.this,
                                        "Failed to get download URL", Toast.LENGTH_SHORT).show();
                                imagesToLoad--;
                                if (imagesToLoad==0) {
                                    adapter = new StoreAdapter(
                                            StoreActivity.this, cards, coins, database, data[0], cardPosition, storage);
                                    recyclerView.setAdapter(adapter);
                                    loadingDialog.dismiss();
                                }
                            });
                }

                if (cards.size()==0) loadingDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(StoreActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        });
    }
}