package com.stgsporting.quiz_fut.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quiz_fut_draft.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.stgsporting.quiz_fut.data.Card;
import com.stgsporting.quiz_fut.data.TextColor;

import java.util.ArrayList;
import java.util.Collections;

public class LineupActivity extends AppCompatActivity {

    private String[] data;
    private String userPos;
    private String userRating = "0"; //Points in database
    private ImageView[] lineupCards;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lineup);

        data = getIntent().getStringArrayExtra("Data");

        // All cards IDs
        int[] lineupViewIds = {
                R.id.ST,
                R.id.LW,
                R.id.RW,
                R.id.CAM,
                R.id.LCM,
                R.id.RCM,
                R.id.LB,
                R.id.LCB,
                R.id.RCB,
                R.id.RB,
                R.id.GK
        };

        lineupCards = new ImageView[lineupViewIds.length];

        TextView points = findViewById(R.id.points);
        TextView highest = findViewById(R.id.highest);
        TextView average = findViewById(R.id.average);

        for (int i=0;i<lineupViewIds.length;i++) {
            int id = lineupViewIds[i];
            lineupCards[i] = findViewById(id);
            String cardPos = getResources().getResourceEntryName(id);
            lineupCards[i].setOnClickListener(v-> {
                String usedPosition = userPos;
                if (usedPosition.equals("CB")) usedPosition = "LCB";
                if (usedPosition.equals("CM")) usedPosition = "LCM";
                Intent int1;
                if (!usedPosition.equals(cardPos))
                    int1 = new Intent(LineupActivity.this, StoreActivity.class);
                else
                    int1 = new Intent(LineupActivity.this, MyCardActivity.class);
                int1.putExtra("Data", data);
                int1.putExtra("Card", cardPos);
                startActivity(int1);
            });
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance(data[1]);
        storage = FirebaseStorage.getInstance(data[2]);
        DatabaseReference ref = database.getReference();
        DatabaseReference userRef = ref.child("/elmilad25/Users").child(data[0]);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DataSnapshot userData = snapshot.child("/elmilad25/Users").child(data[0]);
                DataSnapshot storeData = snapshot.child("elmilad25").child("Store");

                // check user position || make it default (GK)
                if (userData.child("Card").hasChild("Position"))
                    userPos = userData.child("Card").child("Position").getValue().toString();
                else {
                    userPos = "GK";
                    userRef.child("Owned Positions").child("position1").child("Owned").setValue(true);
                    userRef.child("Card").child("Position").setValue("GK");
                }
                if (!userData.child("Card").hasChild("Rating"))
                    userRef.child("Card").child("Rating").setValue(50);

                resetImages();

                // read lineup
                double totalRating = 0;
                for (ImageView cardImage : lineupCards) {
                    String cardPos = getResources().getResourceEntryName(cardImage.getId());
                    String usedPosition = userPos;
                    if (usedPosition.equals("CB")) usedPosition = "LCB";
                    if (usedPosition.equals("CM")) usedPosition = "LCM";
                    if (cardPos.equals(usedPosition)) {
                        setUserCardImage(cardImage, userData, snapshot);
                        if (userData.child("Card").hasChild("Rating"))
                            totalRating += (double) Integer.parseInt(userData.child("Card").child("Rating").getValue().toString())/11;
                        else
                            totalRating+=50;
                    } else if (userData.child("Lineup").hasChild(cardPos)) {
                        String cardID = userData.child("Lineup").child(cardPos).getValue().toString();
                        Card c = new Card();
                        c.setID(cardID);
                        DataSnapshot cardData = storeData.child(cardID);
                        c.setRating(cardData.child("Rating").getValue().toString());
                        c.setPosition(cardData.child("Position").getValue().toString());
                        c.setPrice(Integer.parseInt(cardData.child("Price").getValue().toString()));
                        String imagePath = cardData.child("Image").getValue().toString();
                        StorageReference storageRef = storage.getReference().child(imagePath);
                        storageRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    String downloadUrl = uri.toString();
                                    Picasso.get().load(downloadUrl).into(cardImage);
                                })
                                .addOnFailureListener(e -> Toast.makeText(LineupActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show());
                        totalRating += (double) Integer.parseInt(c.getRating()) / 11;
                    }

                }

                // set Rating value & store it into database
                int userRatingInt = (int) Math.round(totalRating);
                userRating = String.valueOf(userRatingInt);
                points.setText(String.valueOf(userRatingInt));
                userRef.child("Points").setValue(userRatingInt);

                // assign average & highest
                ArrayList<Integer> allUsersRatings = new ArrayList<>();
                int sum = 0;
                int numOfUsers = 0;
                for (DataSnapshot aUserData : snapshot.child("/elmilad25/Users").getChildren()) {
                    numOfUsers++;
                    if (aUserData.hasChild("Points")) {
                        int aUserPoints = Integer.parseInt(aUserData.child("Points").getValue().toString());
                        allUsersRatings.add(aUserPoints);
                        sum+=aUserPoints;
                    }
                }

                highest.setText(String.valueOf(Collections.max(allUsersRatings)));
                double avg = (double) sum /numOfUsers;
                average.setText(String.valueOf((int) Math.round(avg)));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LineupActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setUserCardImage(ImageView imageView, DataSnapshot userData, DataSnapshot allData) {
        RelativeLayout v = findViewById(R.id.main);
        ImageView icon = findViewById(R.id.card_icon);
        ImageView img = findViewById(R.id.img);
        TextView name = findViewById(R.id.name);
        TextView rating = findViewById(R.id.card_rating);
        TextView position = findViewById(R.id.position);

        imagesToLoad = 2;

        if (userData.child("Owned Card Icons").hasChild("Selected")) {
            String selected = userData.child("Owned Card Icons").child("Selected").getValue().toString();

            DataSnapshot cardRef = allData.child("elmilad25").child("CardIcon").child(selected);

            if (cardRef.hasChild("Image")) {
                String cardIconName = cardRef.child("Image").getValue().toString();
                StorageReference storageRef = storage.getReference().child(cardIconName);
                storageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            Picasso.get().load(downloadUrl).into(icon, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    imagesToLoad--;
                                    TextColor.setColor(icon, name, position, rating);
                                    checkIfAllImagesLoaded(v, imageView);
                                }

                                @Override
                                public void onError(Exception e) {
                                    imagesToLoad--;
                                    checkIfAllImagesLoaded(v, imageView);                                }
                            });
                        })
                        .addOnFailureListener(e -> Toast.makeText(LineupActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show());
            }

        } else {
            icon.setImageDrawable(getResources().getDrawable(R.drawable.empty));
            imagesToLoad--;
            checkIfAllImagesLoaded(v, imageView);
        }
        if (userData.hasChild("ImageLink")) {
            String imgLink = userData.child("ImageLink").getValue().toString();
            Picasso.get().load(imgLink).into(img, new Callback() {
                @Override
                public void onSuccess() {
                    imagesToLoad--;
                    checkIfAllImagesLoaded(v, imageView);
                }

                @Override
                public void onError(Exception e) {
                    imagesToLoad--;
                    checkIfAllImagesLoaded(v, imageView);
                }
            });
        } else {
            imagesToLoad--;
            checkIfAllImagesLoaded(v, imageView);
        }
        name.setText(data[0]);
        position.setText(userPos);
        if (userData.child("Card").hasChild("Rating")) {
            rating.setText(userData.child("Card").child("Rating").getValue().toString());
        }
    }

    private int imagesToLoad = 2;

    private void checkIfAllImagesLoaded(View v, ImageView imageView) {
        if (imagesToLoad==0) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                RelativeLayout layout = v.findViewById(R.id.main);

                layout.measure(View.MeasureSpec.makeMeasureSpec(
                                layout.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(
                                layout.getHeight(), View.MeasureSpec.EXACTLY));

                layout.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());

                int totalHeight = v.getMeasuredHeight();
                int totalWidth  = v.getMeasuredWidth();

                Bitmap bitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                v.draw(canvas);
                imageView.setImageBitmap(bitmap);
                imageView.setScaleX(1.05F);
            }, 100);
        }
    }

    private void resetImages() {
        for (ImageView img : lineupCards) {
            img.setImageDrawable(getResources().getDrawable(R.drawable.empty));
        }
    }

}