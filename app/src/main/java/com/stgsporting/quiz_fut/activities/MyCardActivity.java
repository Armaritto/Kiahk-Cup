package com.stgsporting.quiz_fut.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quiz_fut_draft.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.stgsporting.quiz_fut.data.TextColor;

import java.util.Objects;

public class MyCardActivity extends AppCompatActivity {

    private String[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_card);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        data = getIntent().getStringArrayExtra("Data");
        setupHeader(FirebaseDatabase.getInstance(data[1]).getReference("/elmilad25/Users"));

        Button positionBtn = findViewById(R.id.position_btn);
        Button cardBtn = findViewById(R.id.card_btn);
        Button ratingBtn = findViewById(R.id.rating_btn);

        ImageView cardIcon = findViewById(R.id.card_icon);
        ImageView img = findViewById(R.id.img);
        TextView name = findViewById(R.id.name);
        TextView position = findViewById(R.id.position);
        TextView card_rating = findViewById(R.id.card_rating);

        FirebaseDatabase database = FirebaseDatabase.getInstance(data[1]);
        DatabaseReference ref = database.getReference();
        FirebaseStorage storage = FirebaseStorage.getInstance(data[2]);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataS) {
                if (dataS.child("/elmilad25/Users").child(data[0])
                        .child("Owned Card Icons").hasChild("Selected")) {
                    String selected = dataS.child("/elmilad25/Users").child(data[0]).child("Owned Card Icons").child("Selected").getValue().toString();

                    DataSnapshot cardRef = dataS.child("elmilad25").child("CardIcon").child(selected);

                    if (cardRef.hasChild("Image")) {
                        String cardIconName = cardRef.child("Image").getValue().toString();
                        StorageReference storageRef = storage.getReference().child(cardIconName);
                        storageRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    String downloadUrl = uri.toString();
                                    Picasso.get().load(downloadUrl).into(cardIcon, new com.squareup.picasso.Callback() {
                                        @Override
                                        public void onSuccess() {
                                            TextColor.setColor(cardIcon, name, position, card_rating);
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Toast.makeText(MyCardActivity.this, "Picasso Error", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                })
                                .addOnFailureListener(e -> Toast.makeText(MyCardActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show());
                    }

                } else {
                    cardIcon.setImageDrawable(getResources().getDrawable(R.drawable.empty));
                }

                DataSnapshot snapshot = dataS.child("/elmilad25/Users").child(data[0]);
                DatabaseReference userRef = ref.child("/elmilad25/Users").child(data[0]);
                if (snapshot.hasChild("ImageLink")) {
                    String imgLink = snapshot.child("ImageLink").getValue().toString();
                    Picasso.get().load(imgLink).into(img);
                }
                name.setText(snapshot.getKey());

                if (snapshot.child("Card").hasChild("Position")) {
                    position.setText(snapshot.child("Card").child("Position").getValue().toString());
                }
                else {
                    userRef.child("Owned Positions").child("position1").child("Owned").setValue(true);
                    userRef.child("Card").child("Position").setValue("GK");
                }

                if (snapshot.child("Card").hasChild("Rating"))
                    card_rating.setText(snapshot.child("Card").child("Rating").getValue().toString());
                else
                    userRef.child("Card").child("Rating").setValue(50);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyCardActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        cardBtn.setOnClickListener(v-> {
            Intent intent = new Intent(MyCardActivity.this, CardStoreActivity.class);
            intent.putExtra("Data",data);
            startActivity(intent);
        });

        positionBtn.setOnClickListener(v-> {
            Intent intent = new Intent(MyCardActivity.this, PositionStoreActivity.class);
            intent.putExtra("Data",data);
            startActivity(intent);
        });

        ratingBtn.setOnClickListener(v-> {
            Intent intent = new Intent(MyCardActivity.this, RatingStoreActivity.class);
            intent.putExtra("Data",data);
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
                name.setText(data[0]);
                stars.setText(Objects.requireNonNull(snapshot.child(data[0]).child("Stars").getValue()).toString());
                coins.setText(Objects.requireNonNull(snapshot.child(data[0]).child("Coins").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}