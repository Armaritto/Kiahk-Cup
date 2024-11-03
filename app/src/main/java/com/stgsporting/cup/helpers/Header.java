package com.stgsporting.cup.helpers;

import android.app.Activity;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stgsporting.cup.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stgsporting.cup.data.User;

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
                User user = new User();
                user.setName(data[0]);
                name.setText(user.getDisplayName());

                snapshot = snapshot.child("/elmilad25/Users");
                Object starsObj = snapshot.child(data[0]).child("Stars").getValue();
                Object coinsObj = snapshot.child(data[0]).child("Coins").getValue();
                if (starsObj == null) {
                    starsObj = 0;
                }
                if (coinsObj == null) {
                    coinsObj = 0;
                }

                stars.setText(starsObj.toString());
                coins.setText(coinsObj.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
