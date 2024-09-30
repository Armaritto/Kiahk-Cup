package com.example.quiz_fut_draft;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
    private String grade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lineup);
        Intent intent1 = getIntent();
        ID = intent1.getStringExtra("ID");
        Name = intent1.getStringExtra("Name");
        grade = intent1.getStringExtra("Grade");
        OVR = "0";

        ImageView gk = findViewById(R.id.gk);
        ImageView lb = findViewById(R.id.lb);
        ImageView rb = findViewById(R.id.rb);
        ImageView lcb = findViewById(R.id.lcb);
        ImageView rcb = findViewById(R.id.rcb);
        ImageView lcm = findViewById(R.id.lcm);
        ImageView cam = findViewById(R.id.cam);
        ImageView rcm = findViewById(R.id.rcm);
        ImageView LW = findViewById(R.id.lw);
        ImageView ST = findViewById(R.id.st);
        ImageView RW = findViewById(R.id.rw);
        TextView points = findViewById(R.id.points);
        TextView highest = findViewById(R.id.highest);
        TextView average = findViewById(R.id.average);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(Users_Path.getPath(grade)).child(ID).exists()) {
                    Position = snapshot.child(Users_Path.getPath(grade)).child(ID).child("Card").child("Position").getValue().toString();
                    OVR = snapshot.child(Users_Path.getPath(grade)).child(ID).child("Points").getValue().toString();
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
            int1.putExtra("Grade", grade);
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
            int1.putExtra("Grade", grade);
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
            int1.putExtra("Grade", grade);
            startActivity(int1);
        });
        lcb.setOnClickListener(v-> {
            Intent int1;
            if(!Position.equals("LCB")) {
                int1 = new Intent(LineupActivity.this, StoreActivity.class);
            }
            else{
                int1 = new Intent(LineupActivity.this, MyCardActivity.class);
            }
            int1.putExtra("ID", ID);
            int1.putExtra("Card", "LCB");
            int1.putExtra("Score", OVR);
            int1.putExtra("Name", Name);
            int1.putExtra("Grade", grade);
            startActivity(int1);
        });
        rcb.setOnClickListener(v-> {
            Intent int1;
            if(!Position.equals("RCB")) {
                int1 = new Intent(LineupActivity.this, StoreActivity.class);
            }
            else{
                int1 = new Intent(LineupActivity.this, MyCardActivity.class);
            }
            int1.putExtra("ID", ID);
            int1.putExtra("Card", "RCB");
            int1.putExtra("Score", OVR);
            int1.putExtra("Name", Name);
            int1.putExtra("Grade", grade);
            startActivity(int1);
        });
        cam.setOnClickListener(v-> {
            Intent int1;
            if (!Position.equals("CAM")) {
                int1 = new Intent(LineupActivity.this, StoreActivity.class);
            }
            else{
                int1 = new Intent(LineupActivity.this, MyCardActivity.class);
            }
            int1.putExtra("ID", ID);
            int1.putExtra("Card", "CAM");
            int1.putExtra("Score", OVR);
            int1.putExtra("Name", Name);
            int1.putExtra("Grade", grade);
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
            int1.putExtra("Grade", grade);
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
            int1.putExtra("Grade", grade);
            startActivity(int1);
        });
        LW.setOnClickListener(v-> {
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
            int1.putExtra("Grade", grade);
            startActivity(int1);
        });
        RW.setOnClickListener(v-> {
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
            int1.putExtra("Grade", grade);
            startActivity(int1);
        });
        ST.setOnClickListener(v-> {
            Intent int1;
            if(!Position.equals("ST")) {
                int1 = new Intent(LineupActivity.this, StoreActivity.class);
            }
            else{
                int1 = new Intent(LineupActivity.this, MyCardActivity.class);
            }
            int1.putExtra("ID", ID);
            int1.putExtra("Card", "ST");
            int1.putExtra("Score", OVR);
            int1.putExtra("Name", Name);
            int1.putExtra("Grade", grade);
            startActivity(int1);
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Card> teamCards = new ArrayList<>();
                ArrayList<String> positions = new ArrayList<>();
                int totalPoints = 0;

                HashMap<String,Integer> teamsScores = new HashMap<>();

                ArrayList<Card> lineup = new ArrayList<>(11);
                if (snapshot.child(Users_Path.getPath(grade)).child(ID).child("Lineup").hasChildren()) {
                    for (DataSnapshot card : snapshot.child(Users_Path.getPath(grade)).child(ID).child("Lineup").getChildren()) {

                        lineup.add(new Card(Integer.parseInt(card.getValue().toString()), 0, "", "", card.getKey()));
                    }
                }
                for(Card card : lineup) {
                    card.setRating(snapshot.child("elmilad25").child("Store").child("Card "+card.getID()).child("Rating").getValue().toString());
                    card.setPosition(snapshot.child("elmilad25").child("Store").child("Card "+card.getID()).child("Position").getValue().toString());
                    card.setPrice(Integer.parseInt(snapshot.child("elmilad25").child("Store").child("Card "+card.getID()).child("Price").getValue().toString()));
                    card.setImage(snapshot.child("elmilad25").child("Store").child("Card "+card.getID()).child("Image").getValue().toString());
                    positions.add(card.getPosition());
                    totalPoints += Integer.parseInt(card.getRating())/11;
                }

                totalPoints += snapshot.child(Users_Path.getPath(grade)).child(ID).child("Card").child("Rating").getValue(Integer.class)/11;
                teamsScores.put(ID, totalPoints);

                OVR = Integer.toString(totalPoints);
                points.setText(String.valueOf(totalPoints));

                setCardImage("GK", gk, positions, lineup);
                setCardImage("LB", lb, positions, lineup);
                setCardImage("RB", rb, positions, lineup);
                setCardImage("LCB", lcb, positions, lineup);
                setCardImage("RCB", rcb, positions, lineup);
                setCardImage("LCM", lcm, positions, lineup);
                setCardImage("CAM", cam, positions, lineup);
                setCardImage("RCM", rcm, positions, lineup);
                setCardImage("LW", LW, positions, lineup);
                setCardImage("ST", ST, positions, lineup);
                setCardImage("RW", RW, positions, lineup);


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

            RelativeLayout v = findViewById(R.id.main);
            ImageView icon = findViewById(R.id.card_icon);
            ImageView img = findViewById(R.id.img);
            TextView name = findViewById(R.id.name);
            TextView rating = findViewById(R.id.card_rating);
            TextView position = findViewById(R.id.position);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference(Users_Path.getPath(grade)).child(ID);
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
                                TextColor.setColor(icon, name, position, rating);
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
        ref.child(Users_Path.getPath(grade)).child(ID).child("Points").setValue(OVR);
    }
}