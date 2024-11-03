package com.stgsporting.cup.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.stgsporting.cup.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stgsporting.cup.helpers.Header;
import com.stgsporting.cup.helpers.LoadingDialog;

import java.util.Objects;

public class RatingStoreActivity extends AppCompatActivity {

    private int rating;
    private int oldRating;
    private String[] data;
    private int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rating_store);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LoadingDialog loadingDialog = new LoadingDialog(this);

        Button purchase = findViewById(R.id.purchase);
        LinearLayout plus = findViewById(R.id.plus);
        LinearLayout minus = findViewById(R.id.minus);
        TextView new_rating = findViewById(R.id.new_rating);
        TextView price = findViewById(R.id.price);

        data = getIntent().getStringArrayExtra("Data");
        new_rating.setText(String.valueOf(rating));
        FirebaseDatabase database = FirebaseDatabase.getInstance(data[1]);
        Header.render(this, Objects.requireNonNull(data));
        DatabaseReference ref = database.getReference();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataS) {

                DataSnapshot userData = dataS.child("/elmilad25/Users").child(data[0]);
                rating = Integer.parseInt(userData.child("Card").child("Rating").getValue().toString());
                new_rating.setText(String.valueOf(rating));
                oldRating = rating;
                int units;
                if (dataS.hasChild("/elmilad25/Rating Price")) {
                    DataSnapshot snapshot = dataS.child("/elmilad25/Rating Price");
                    units = Integer.parseInt(snapshot.getValue().toString());
                }
                else
                    units = 5;

                plus.setOnClickListener(v -> {
                    if(rating >= 99) return;
                    rating += 1;
                    total+=units;
                    new_rating.setText(String.valueOf(rating));
                    price.setText(String.valueOf(total));
                });
                minus.setOnClickListener(v -> {
                    if(rating <= oldRating) return;
                    rating -= 1;
                    total-=units;
                    new_rating.setText(String.valueOf(rating));
                    price.setText(String.valueOf(total));
                });
                purchase.setOnClickListener(v -> {
                    DatabaseReference userRef = database.getReference("/elmilad25/Users").child(data[0]);
                    int stars = Integer.parseInt(userData.child("Stars").getValue().toString());
                    if (stars>=total) {
                        stars-=total;
                        userRef.child("Stars").setValue(stars);
                        userRef.child("Card").child("Rating").setValue(rating);
                        Toast.makeText(RatingStoreActivity.this, "Rating purchased successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                        Toast.makeText(RatingStoreActivity.this, "Not enough Stars", Toast.LENGTH_SHORT).show();
                });

                loadingDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss();
                Toast.makeText(RatingStoreActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}