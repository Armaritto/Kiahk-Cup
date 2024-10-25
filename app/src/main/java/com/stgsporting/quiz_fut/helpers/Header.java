package com.stgsporting.quiz_fut.helpers;

import android.app.Activity;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stgsporting.quiz_fut.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class Header {
    public static void render(Activity activity, String[] data) {
        FirebaseDatabase database = FirebaseDatabase.getInstance(data[1]);
        DatabaseReference ref = database.getReference();

        TextView stars = activity.findViewById(R.id.rating);
        TextView coins = activity.findViewById(R.id.coins);
        TextView name = activity.findViewById(R.id.nametextview);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String new_name = Arrays.stream(data[0].split("\\s+"))
                        .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                        .collect(Collectors.joining(" "));
                name.setText(new_name);
                snapshot = snapshot.child("/elmilad25/Users");
                stars.setText(Objects.requireNonNull(snapshot.child(data[0]).child("Stars").getValue()).toString());
                coins.setText(Objects.requireNonNull(snapshot.child(data[0]).child("Coins").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
