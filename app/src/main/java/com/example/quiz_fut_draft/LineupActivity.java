package com.example.quiz_fut_draft;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LineupActivity extends AppCompatActivity {
    private String Name;
    private String ID;
    private String Position;
    private String OVR; //Points in database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lineup);
        Intent intent1 = getIntent();
        ID = intent1.getStringExtra("ID");
        Name = intent1.getStringExtra("Name");
        OVR = "0";

        ImageView gk = findViewById(R.id.gk);
        ImageView lb = findViewById(R.id.lb);
        ImageView rb = findViewById(R.id.rb);
        ImageView cb = findViewById(R.id.cb);
        ImageView lc = findViewById(R.id.lm);
        ImageView lcm = findViewById(R.id.lcm);
        ImageView rc = findViewById(R.id.rm);
        ImageView rcm = findViewById(R.id.rcm);
        ImageView LF = findViewById(R.id.ls);
        ImageView CF = findViewById(R.id.cs);
        ImageView RF = findViewById(R.id.rs);
        TextView points = findViewById(R.id.points);
        TextView highest = findViewById(R.id.highest);
        TextView average = findViewById(R.id.average);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Login").child(ID).exists()) {
                    Position = snapshot.child("Login").child(ID).child("Card").child("Position").getValue().toString();
                    OVR = snapshot.child("Login").child(ID).child("Points").getValue().toString();
                }
                points.setText(OVR);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LineupActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        gk.setOnClickListener(v-> {
            Intent int1;
            if(!Position.equals("GK")) {
                int1 = new Intent(LineupActivity.this, StoreActivity.class);
            }
            else{
                int1 = new Intent(LineupActivity.this, MyCardActivity.class);
            }
            int1.putExtra("ID", ID);
            int1.putExtra("Card", "GK");
            int1.putExtra("Score", OVR);
            int1.putExtra("Name", Name);
            startActivity(int1);
        });
        lb.setOnClickListener(v-> {
            Intent int1;
            if(!Position.equals("LB")) {
                int1 = new Intent(LineupActivity.this, StoreActivity.class);
            }
            else{
                int1 = new Intent(LineupActivity.this, MyCardActivity.class);
            }
            int1.putExtra("ID", ID);
            int1.putExtra("Card", "LB");
            int1.putExtra("Score", OVR);
            int1.putExtra("Name", Name);
            startActivity(int1);
        });
        rb.setOnClickListener(v-> {
            Intent int1;
            if(!Position.equals("RB")) {
                int1 = new Intent(LineupActivity.this, StoreActivity.class);
            }
            else{
                int1 = new Intent(LineupActivity.this, MyCardActivity.class);
            }
            int1.putExtra("ID", ID);
            int1.putExtra("Card", "RB");
            int1.putExtra("Score", OVR);
            int1.putExtra("Name", Name);
            startActivity(int1);
        });
        cb.setOnClickListener(v-> {
            Intent int1;
            if(!Position.equals("CB")) {
                int1 = new Intent(LineupActivity.this, StoreActivity.class);
            }
            else{
                int1 = new Intent(LineupActivity.this, MyCardActivity.class);
            }
            int1.putExtra("ID", ID);
            int1.putExtra("Card", "CB");
            int1.putExtra("Score", OVR);
            int1.putExtra("Name", Name);
            startActivity(int1);
        });
        lc.setOnClickListener(v-> {
            Intent int1;
            if(!Position.equals("LC")) {
                int1 = new Intent(LineupActivity.this, StoreActivity.class);
            }
            else{
                int1 = new Intent(LineupActivity.this, MyCardActivity.class);
            }
            int1.putExtra("ID", ID);
            int1.putExtra("Card", "LC");
            int1.putExtra("Score", OVR);
            int1.putExtra("Name", Name);
            startActivity(int1);
        });
        rc.setOnClickListener(v-> {
            Intent int1;
            if (!Position.equals("RC")) {
                int1 = new Intent(LineupActivity.this, StoreActivity.class);
            }
            else{
                int1 = new Intent(LineupActivity.this, MyCardActivity.class);
            }
            int1.putExtra("ID", ID);
            int1.putExtra("Card", "RC");
            int1.putExtra("Score", OVR);
            int1.putExtra("Name", Name);
            startActivity(int1);
        });
        lcm.setOnClickListener(v-> {
            Intent int1;
            if(!Position.equals("LCM")) {
                int1 = new Intent(LineupActivity.this, StoreActivity.class);
            }
            else{
                int1 = new Intent(LineupActivity.this, MyCardActivity.class);
            }
            int1.putExtra("ID", ID);
            int1.putExtra("Card", "LCM");
            int1.putExtra("Score", OVR);
            int1.putExtra("Name", Name);
            startActivity(int1);
        });
        rcm.setOnClickListener(v-> {
            Intent int1;
            if(!Position.equals("RCM")) {
                int1 = new Intent(LineupActivity.this, StoreActivity.class);
            }
            else{
                int1 = new Intent(LineupActivity.this, MyCardActivity.class);
            }
            int1.putExtra("ID", ID);
            int1.putExtra("Card", "RCM");
            int1.putExtra("Score", OVR);
            int1.putExtra("Name", Name);
            startActivity(int1);
        });
        LF.setOnClickListener(v-> {
            Intent int1;
            if(!Position.equals("LW")) {
                int1 = new Intent(LineupActivity.this, StoreActivity.class);
            }
            else{
                int1 = new Intent(LineupActivity.this, MyCardActivity.class);
            }
            int1.putExtra("ID", ID);
            int1.putExtra("Card", "LW");
            int1.putExtra("Score", OVR);
            int1.putExtra("Name", Name);
            startActivity(int1);
        });
        RF.setOnClickListener(v-> {
            Intent int1;
            if(!Position.equals("RW")) {
                int1 = new Intent(LineupActivity.this, StoreActivity.class);
            }
            else{
                int1 = new Intent(LineupActivity.this, MyCardActivity.class);
            }
            int1.putExtra("ID", ID);
            int1.putExtra("Card", "RW");
            int1.putExtra("Score", OVR);
            int1.putExtra("Name", Name);
            startActivity(int1);
        });
        CF.setOnClickListener(v-> {
            Intent int1;
            if(!Position.equals("ST")) {
                int1 = new Intent(LineupActivity.this, StoreActivity.class);
            }
            else{
                int1 = new Intent(LineupActivity.this, MyCardActivity.class);
            }
            int1.putExtra("ID", ID);
            int1.putExtra("Card", "CF");
            int1.putExtra("Score", OVR);
            int1.putExtra("Name", Name);
            startActivity(int1);
        });

        FirebaseDatabase database2 = FirebaseDatabase.getInstance();
        DatabaseReference ref1 = database2.getReference("/");
        ref1.child("Store").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Card> teamCards = new ArrayList<>();
                ArrayList<String> positions = new ArrayList<>();
                int totalPoints = 0;

                HashMap<String,Integer> teamsScores = new HashMap<>();

                for (int i = 0; i < 66; i++) { //---------------------------------------> if changed then will change this
                    String objectName = "Card " + (i + 1);
                    if (!snapshot.child(objectName).exists())
                        continue;
                    String price = snapshot.child(objectName).child("Price").getValue().toString();
                    String position = snapshot.child(objectName).child("Position").getValue().toString();
                    String image = snapshot.child(objectName).child("Image").getValue().toString();
                    String rating = snapshot.child(objectName).child("Rating").getValue().toString();
                    String owner = "";
                    if (snapshot.child(objectName).hasChild("Owner"))
                        owner = snapshot.child(objectName).child("Owner").getValue().toString();
                    Card card = new Card((i+1), Integer.parseInt(price), image, owner, rating, position);
                    if (owner.equals(ID)) {
                        teamCards.add(card);
                        positions.add(position);
                        totalPoints += Integer.parseInt(card.getRating());
                        if (!teamsScores.containsKey(owner)) {
                            teamsScores.put(owner, 0);
                        }
                    }
                    for (Map.Entry<String, Integer> entry : teamsScores.entrySet()) {
                        String key = entry.getKey();
                        Integer value = entry.getValue();
                        if(key.equals(owner)) {
                            value += Integer.parseInt(card.getRating());
                            teamsScores.put(key, value);
                        }
                    }
                }
                for (Map.Entry<String, Integer> entry : teamsScores.entrySet()) {
                    String key = entry.getKey();
                    Integer value = entry.getValue();
                    teamsScores.put(key, value/11);
                }
                totalPoints /=11;
                OVR = Integer.toString(totalPoints);
                points.setText(String.valueOf(totalPoints));

                setCardImage("GK", gk, positions, teamCards);
                setCardImage("LB", lb, positions, teamCards);
                setCardImage("RB", rb, positions, teamCards);
                setCardImage("CB", cb, positions, teamCards);
                setCardImage("LC", lc, positions, teamCards);
                setCardImage("LCM", lcm, positions, teamCards);
                setCardImage("RC", rc, positions, teamCards);
                setCardImage("RCM", rcm, positions, teamCards);
                setCardImage("LW", LF, positions, teamCards);
                setCardImage("ST", CF, positions, teamCards);
                setCardImage("RW", RF, positions, teamCards);


                int highestScore = -1;
                for (Map.Entry<String, Integer> entry : teamsScores.entrySet()) {
                    Integer value = entry.getValue();
                    highestScore = Math.max(highestScore, value);
                }
//                if(highestScore == -1)    ----------------------------------------> uncomment when finish debugging
//                    highestScore = 0;
                highest.setText(String.valueOf(highestScore));

                int averageScore = 0;
                int sum = 0;
                for (Map.Entry<String, Integer> entry : teamsScores.entrySet()) {
                    Integer value = entry.getValue();
                    sum += value;
                }
                if(!teamsScores.isEmpty())
                    averageScore = sum / teamsScores.size();
                average.setText(String.valueOf(averageScore));
                updateOVR();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    int imagesToLoad = 2;

    private void setCardImage(String p, ImageView imageView,
                              ArrayList<String> pos, ArrayList<Card> cards) {
        if (pos.contains(p)) {
            Card card = cards.get(pos.indexOf(p));
            Drawable drawable = getResources().getDrawable(getResources()
                    .getIdentifier(card.getImage(), "drawable", getPackageName()));
            imageView.setImageDrawable(drawable);
        }
        else if (Objects.equals(Position, p)) {

//            View v = getLayoutInflater().inflate(R.layout.activity_card_all_in_one, null);
            RelativeLayout v = findViewById(R.id.main);
            ImageView icon = findViewById(R.id.card_icon);
            ImageView img = findViewById(R.id.img);
            TextView name = findViewById(R.id.name);
            TextView rating = findViewById(R.id.card_rating);
            TextView position = findViewById(R.id.position);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("Login").child(ID);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    imagesToLoad = 2;

                    if (snapshot.child("Card").hasChild("CardIcon")) {
                        String cardIconLink = snapshot.child("Card").child("CardIcon").getValue().toString();
                        Picasso.get().load(cardIconLink).into(icon, new Callback() {
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
                    if (snapshot.hasChild("Pic")) {
                        String imgLink = snapshot.child("Pic").getValue().toString();
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
                    if (snapshot.hasChild("Name")) {
                        name.setText(snapshot.child("Name").getValue().toString());
                    }
                    if (snapshot.child("Card").hasChild("Position")) {
                        position.setText(snapshot.child("Card").child("Position").getValue().toString());
                    }
                    if (snapshot.child("Card").hasChild("Rating")) {
                        rating.setText(snapshot.child("Card").child("Rating").getValue().toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });



        }
    }

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

                Log.d("D", "Height: " + totalHeight);
                Log.d("D", "Width: " + totalWidth);
                Bitmap bitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                v.draw(canvas);
                imageView.setImageBitmap(bitmap);
                imageView.setScaleX(1.05F);
//                imageView.setScaleY(1.05F);
            }, 1000);
        }
    }

    private void updateOVR() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("/");
        ref.child("Login").child(ID).child("Points").setValue(OVR);
    }
}