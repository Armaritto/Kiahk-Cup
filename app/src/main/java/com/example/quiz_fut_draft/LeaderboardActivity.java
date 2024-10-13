package com.example.quiz_fut_draft;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;

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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LeaderboardActivity extends AppCompatActivity {

    private String[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        data = getIntent().getStringArrayExtra("Data");

        FirebaseDatabase database = FirebaseDatabase.getInstance(data[1]);
        DatabaseReference ref = database.getReference();

        setupHeader(ref.child("/elmilad25/Users"));

        RecyclerView recyclerView = findViewById(R.id.recycler_view_lineups);
        int numberOfColumns = 1;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot userData = snapshot.child("/elmilad25/Users");
                HashMap<String,Integer> allUsersRatings = new HashMap<>();
                for (DataSnapshot aUserData : snapshot.child("/elmilad25/Users").getChildren()) {
                    if (aUserData.getKey().equals("Admin")) continue;
                    if (aUserData.hasChild("Points")) {
                        int aUserPoints = Integer.parseInt(aUserData.child("Points").getValue().toString());
                        allUsersRatings.put(aUserData.getKey(),aUserPoints);
                    }
                }
                List<Map.Entry<String, Integer>> list = new ArrayList<>(allUsersRatings.entrySet());
                list.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
                ArrayList<Lineup> lineups = getLineups(list,snapshot);
                // Display the sorted Lineups in the Leaderboard UI
                /*
                -------------------------------------------
                | 1. User1: 100 OVR          view lineup  |
                | 2. User2: 90 OVR           view lineup  |
                | 3. User3: 80 OVR           view lineup  |
                | 4. User4: 70 OVR           view lineup  |
                -------------------------------------------
                 */
                LeaderboardAdapter adapter = new LeaderboardAdapter(LeaderboardActivity.this, lineups,
                        data, userData, snapshot);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(LeaderboardActivity.this, "Failed to read value.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private @NonNull ArrayList<Lineup> getLineups(List<Map.Entry<String, Integer>> list, DataSnapshot snapshot) {
        ArrayList<Lineup> lineups = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : list) {
            if(entry.getValue() > 0){
                Lineup lineup = new Lineup();
                lineup.setID(entry.getKey());
                lineup.setOVR(entry.getValue().toString());
                lineups.add(lineup);
            }
        }
        return lineups;
    }

    private void setUserCardImage(ImageView imageView, DataSnapshot userData, DataSnapshot allData) {
        RelativeLayout v = findViewById(R.id.main);
        ImageView icon = findViewById(R.id.card_icon);
        ImageView img = findViewById(R.id.img);
        TextView nameView = findViewById(R.id.name);
        TextView rating = findViewById(R.id.card_rating);
        TextView position = findViewById(R.id.position);

        imagesToLoad = 2;

        if (userData.child("Owned Card Icons").hasChild("Selected")) {
            String selected = userData.child("Owned Card Icons").child("Selected").getValue().toString();

            DataSnapshot cardRef = allData.child("elmilad25").child("CardIcon").child(selected);

            if (cardRef.hasChild("Link")) {
                String cardIconLink = cardRef.child("Link").getValue().toString();
                Picasso.get().load(cardIconLink).into(icon, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        imagesToLoad--;
                        TextColor.setColor(icon, nameView, position, rating);
                        checkIfAllImagesLoaded(v, imageView);
                    }

                    @Override
                    public void onError(Exception e) {
                        imagesToLoad--;
                        checkIfAllImagesLoaded(v, imageView);
                    }
                });
            }

        } else {
            icon.setImageDrawable(getResources().getDrawable(R.drawable.empty));
            imagesToLoad--;
            checkIfAllImagesLoaded(v, imageView);
        }
        if (userData.hasChild("Pic")) {
            String imgLink = userData.child("Pic").getValue().toString();
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
        nameView.setText(data[0]);
//        position.setText(userPos);
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

    private void setupHeader(DatabaseReference ref) {
        TextView stars = findViewById(R.id.rating);
        TextView coins = findViewById(R.id.coins);
        TextView name = findViewById(R.id.nametextview);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stars.setText(Objects.requireNonNull(snapshot.child(data[0]).child("Stars").getValue()).toString());
                coins.setText(Objects.requireNonNull(snapshot.child(data[0]).child("Coins").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}