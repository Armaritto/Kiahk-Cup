package com.example.quiz_fut_draft;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.content.Intent;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MyCardActivity extends AppCompatActivity {
    private String Name;
    private String ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_card);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent1 = getIntent();
        ID = intent1.getStringExtra("ID");
        Name = intent1.getStringExtra("Name");
        String grade = intent1.getStringExtra("Grade");

        assert grade != null;
        setupHeader(FirebaseDatabase.getInstance().getReference(Users_Path.getPath(grade)));

        Button positionBtn = findViewById(R.id.position_btn);
        Button cardBtn = findViewById(R.id.card_btn);
        Button ratingBtn = findViewById(R.id.rating_btn);

        ImageView cardIcon = findViewById(R.id.card_icon);
        ImageView img = findViewById(R.id.img);
        TextView name = findViewById(R.id.name);
        TextView position = findViewById(R.id.position);
        TextView card_rating = findViewById(R.id.card_rating);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                if (data.child(Users_Path.getPath(grade)).child(ID)
                        .child("Owned Card Icons").hasChild("Selected")) {
                    String selected = data.child(Users_Path.getPath(grade)).child(ID).child("Owned Card Icons").child("Selected").getValue().toString();

                    DataSnapshot cardRef = data.child("elmilad25").child("CardIcon").child(selected);

                    if (cardRef.hasChild("Link")) {
                        String cardIconLink = cardRef.child("Link").getValue().toString();
                        Picasso.get().load(cardIconLink).into(cardIcon, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                TextColor.setColor(cardIcon, name, position, card_rating);
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e("Picasso", e.getMessage());
                            }
                        });
                    }

                }

                DataSnapshot snapshot = data.child(Users_Path.getPath(grade)).child(ID);
                if (snapshot.hasChild("Pic")) {
                    String imgLink = snapshot.child("Pic").getValue().toString();
                    Picasso.get().load(imgLink).into(img);
                }
                if (snapshot.hasChild("Name")) {
                    name.setText(snapshot.child("Name").getValue().toString());
                }
                if (snapshot.child("Card").hasChild("Position")) {
                    position.setText(snapshot.child("Card").child("Position").getValue().toString());
                }
                if (snapshot.child("Card").hasChild("Rating")) {
                    card_rating.setText(snapshot.child("Card").child("Rating").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyCardActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        cardBtn.setOnClickListener(v-> {
            Intent intent = new Intent(MyCardActivity.this, CardStoreActivity.class);
            intent.putExtra("ID",ID);
            intent.putExtra("Name",Name);
            intent.putExtra("Grade",grade);
            startActivity(intent);
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