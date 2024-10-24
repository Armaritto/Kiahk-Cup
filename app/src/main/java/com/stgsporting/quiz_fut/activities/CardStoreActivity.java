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

import com.stgsporting.quiz_fut.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stgsporting.quiz_fut.data.CardIcon;
import com.stgsporting.quiz_fut.adapters.CardStoreAdapter;
import com.stgsporting.quiz_fut.helpers.LoadingDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class CardStoreActivity extends AppCompatActivity {

    private String[] data;

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
        setupHeader(FirebaseDatabase.getInstance(data[1]).getReference("/elmilad25/Users"));

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
                    cards.add(c);
                    cardsNames.add(c.getCard());
                }

                DataSnapshot ownedIconsData = dataSnapshot.child(
                        "/elmilad25/Users").child(data[0]).child("Owned Card Icons");
                for (DataSnapshot icon : ownedIconsData.getChildren()) {
                    if (!icon.getKey().equals("Selected")) {
                        if (Boolean.parseBoolean(icon.child("Owned").getValue().toString())) {
                            String card = icon.getKey();
                            if (cardsNames.contains(card)) {
                                cards.get(cardsNames.indexOf(card)).setOwned(true);
                            }
                        }
                    }
                }

                for (int i=0;i<cards.size();i++) {
                    CardIcon c = cards.get(i);
                    StorageReference storageRef = storage.getReference().child(c.getImagePath());
                    final int j = i;
                    storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String downloadUrl = uri.toString();
                                cards.get(j).setImageLink(downloadUrl);
                                if (j==cards.size()-1) {
                                    CardStoreAdapter adapter = new CardStoreAdapter(
                                            CardStoreActivity.this, cards, database, data[0]);
                                    recyclerView.setAdapter(adapter);
                                    loadingDialog.dismiss();
                                }
                            })
                            .addOnFailureListener(e -> Toast.makeText(CardStoreActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show());
                }

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
                String new_name = Arrays.stream(data[0].split("\\s+"))
                        .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                        .collect(Collectors.joining(" "));
                name.setText(new_name);
                stars.setText(Objects.requireNonNull(snapshot.child(data[0]).child("Stars").getValue()).toString());
                coins.setText(Objects.requireNonNull(snapshot.child(data[0]).child("Coins").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
         });
    }
}