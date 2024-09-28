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
    private String Name;
    private String ID;
    private static int coins;
    private static String team;
    private MyRecyclerViewAdapter adapter;
    private FirebaseDatabase database;
    private ArrayList<Card> cards;
    private String cardStoreCheck = "";
    private String OVR;
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
        Name = intent1.getStringExtra("Name");
        OVR = intent1.getStringExtra("Score");
        team = intent1.getStringExtra("Team");
        grade = intent1.getStringExtra("Grade");

        try{
            cardStoreCheck = getIntent().getStringExtra("Card");
        }
        catch (Exception ignored){}

        database = FirebaseDatabase.getInstance();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("/elmilad25/Store");

        setupHeader(database.getReference(Users_Path.getPath(grade)));

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cards = new ArrayList<>();
                ArrayList<String> ownedPositions = new ArrayList<>();
                for (int i = 0; i < 66; i++) {
                    String objectName = "Card " + (i + 1);
                    if (!snapshot.child(objectName).exists())
                        continue;
                    if (cardStoreCheck != null)
                        if (!snapshot.child(objectName).child("Position").getValue().toString().equals(cardStoreCheck))
                            continue;
                    String price = snapshot.child(objectName).child("Price").getValue().toString();
                    String position = snapshot.child(objectName).child("Position").getValue().toString();
                    String image = snapshot.child(objectName).child("Image").getValue().toString();
                    String owner = "";
                    if (snapshot.child(objectName).hasChild("Owner"))
                        owner = snapshot.child(objectName).child("Owner").getValue().toString();
                    if (owner.equals(team) && !ownedPositions.contains(position)) ownedPositions.add(position);
                    String rating = snapshot.child(objectName).child("Rating").getValue().toString();
                    Card card = new Card((i+1), Integer.parseInt(price), image, owner, rating, position);
                    if (owner.equals("")) cards.add(card);
                }

                ArrayList<Card> finalList = new ArrayList<>();
                for (Card c : cards) {
                    if (!ownedPositions.contains(c.getPosition())) finalList.add(c);
                }
                adapter = new MyRecyclerViewAdapter(StoreActivity.this, finalList, coins, database, team, cardStoreCheck, ID, grade);
//                adapter.setClickListener(StoreActivity.this);
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
                name.setText(Name);
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