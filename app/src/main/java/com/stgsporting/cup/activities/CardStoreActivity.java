package com.stgsporting.cup.activities;

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
import com.stgsporting.cup.data.CardIcon;
import com.stgsporting.cup.adapters.CardStoreAdapter;
import com.stgsporting.cup.helpers.Header;
import com.stgsporting.cup.helpers.LoadingDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class CardStoreActivity extends AppCompatActivity {

    private String[] data;
    private int imgsToLoad;

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
        Header.render(this, Objects.requireNonNull(data));

        FirebaseDatabase database = FirebaseDatabase.getInstance(data[1]);
        FirebaseStorage storage = FirebaseStorage.getInstance(data[2]);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        DatabaseReference ref = database.getReference();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot snapshot = dataSnapshot.child("elmilad25").child("CardIcon");
                ArrayList<CardIcon> cards = new ArrayList<>();
                ArrayList<String> cardsNames = new ArrayList<>();
                for (DataSnapshot card : snapshot.getChildren()) {
                    String cardName = card.getKey();
                    CardIcon c = new CardIcon();
                    c.setCard(cardName);
                    String imagePath = card.child("Image").getValue().toString();
                    c.setImagePath(imagePath);
                    c.setPrice(Double.parseDouble(card.child("Price").getValue().toString()));
                    if (card.hasChild("Available"))
                        c.setAvailable(Boolean.parseBoolean(card.child("Available").getValue().toString()));
                    else
                        c.setAvailable(true);
                    cards.add(c);
                    cardsNames.add(c.getCard());
                }

                DataSnapshot ownedIconsData = dataSnapshot.child(
                        "/elmilad25/Users").child(data[0]).child("Owned Card Icons");
                for (DataSnapshot icon : ownedIconsData.getChildren()) {
                    if (!icon.getKey().equals("Selected")) {
                        if (Boolean.parseBoolean(icon.child("Owned").getValue().toString())) {
                            String card = icon.getKey();
                            if (cardsNames.contains(card))
                                cards.get(cardsNames.indexOf(card)).setOwned(true);
                        }
                    }
                }

                for (int i=0;i<cards.size();i++) {
                    if (!cards.get(i).isOwned() && !cards.get(i).isAvailable()) {
                        cards.remove(i);
                        cardsNames.remove(i);
                    }
                }

                Collections.sort(cards, Comparator.comparingDouble(CardIcon::getPrice));

                imgsToLoad = cards.size();
                for (int i=0;i<cards.size();i++) {
                    CardIcon c = cards.get(i);
                    StorageReference storageRef = storage.getReference().child(c.getImagePath());
                    final int j = i;
                    storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String downloadUrl = uri.toString();
                                cards.get(j).setImageLink(downloadUrl);
                                imgsToLoad--;
                                if (imgsToLoad==0) {
                                    CardStoreAdapter adapter = new CardStoreAdapter(
                                            CardStoreActivity.this, cards, database, data[0]);
                                    recyclerView.setAdapter(adapter);
                                    loadingDialog.dismiss();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(CardStoreActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                                imgsToLoad--;
                                if (imgsToLoad==0) {
                                    CardStoreAdapter adapter = new CardStoreAdapter(
                                            CardStoreActivity.this, cards, database, data[0]);
                                    recyclerView.setAdapter(adapter);
                                    loadingDialog.dismiss();
                                }
                            });
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(CardStoreActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}