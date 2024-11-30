package com.stgsporting.cup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.Callback;
import com.stgsporting.cup.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stgsporting.cup.data.TextColor;
import com.stgsporting.cup.helpers.Header;
import com.stgsporting.cup.helpers.ImageLoader;
import com.stgsporting.cup.helpers.NetworkUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class MyCardActivity extends AppCompatActivity {

    private String[] data;
    private ImageView cardIcon;
    private ImageView img;
    private TextView name;
    private TextView position;
    private TextView card_rating;
    private DatabaseReference ref;
    private FirebaseStorage storage;
    private ImageLoader imageLoader;

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

        data = getIntent().getStringArrayExtra("Data");
        Header.render(this, Objects.requireNonNull(data));

        Button positionBtn = findViewById(R.id.position_btn);
        Button cardBtn = findViewById(R.id.card_btn);
        Button ratingBtn = findViewById(R.id.rating_btn);

        imageLoader = new ImageLoader(MyCardActivity.this);

        cardIcon = findViewById(R.id.card_icon);
        img = findViewById(R.id.img);
        name = findViewById(R.id.name);
        position = findViewById(R.id.position);
        card_rating = findViewById(R.id.card_rating);

        FirebaseDatabase database = FirebaseDatabase.getInstance(data[1]);
        ref = database.getReference();
        storage = FirebaseStorage.getInstance(data[2]);

        refreshData();

        cardBtn.setOnClickListener(v-> {
            if (!NetworkUtils.isOnline(this)) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MyCardActivity.this, CardStoreActivity.class);
            intent.putExtra("Data",data);
            startActivity(intent);
        });

        positionBtn.setOnClickListener(v-> {
            if (!NetworkUtils.isOnline(this)) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MyCardActivity.this, PositionStoreActivity.class);
            intent.putExtra("Data",data);
            startActivity(intent);
        });

        ratingBtn.setOnClickListener(v-> {
            if (!NetworkUtils.isOnline(this)) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MyCardActivity.this, RatingStoreActivity.class);
            intent.putExtra("Data",data);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
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
                                    loadImage(downloadUrl, cardIcon);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(MyCardActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                                    refreshColors();
                                });
                    } else {
                        refreshColors();
                    }

                } else {
                    cardIcon.setImageDrawable(getResources().getDrawable(R.drawable.empty));
                    refreshColors();
                }

                DataSnapshot snapshot = dataS.child("/elmilad25/Users").child(data[0]);
                DatabaseReference userRef = ref.child("/elmilad25/Users").child(data[0]);
                if (snapshot.hasChild("ImageLink")) {
                    String imgLink = snapshot.child("ImageLink").getValue().toString();
                    loadImage(imgLink, img);
                } else {
                    refreshColors();
                }
                String new_name = Arrays.stream(snapshot.getKey().split("\\s+"))
                        .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                        .collect(Collectors.joining(" "));
                name.setText(new_name);

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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyCardActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadImage(String url, ImageView imageView) {
        Callback onlineCallback = new Callback() {
            public void onSuccess() {refreshColors();}
            public void onError() {
                refreshColors();
                Toast.makeText(MyCardActivity.this, "Could not fetch image", Toast.LENGTH_SHORT).show();
            }
        };

        Callback offlineCallback = new Callback() {
            public void onSuccess() {refreshColors();}
            public void onError() {imageLoader.loadImageOnline(url, imageView, onlineCallback);}
        };

        imageLoader.loadImageOffline(url, imageView, offlineCallback);
    }

    private void refreshColors() {
        TextColor.setColor(cardIcon, name, card_rating, position);
    }

}