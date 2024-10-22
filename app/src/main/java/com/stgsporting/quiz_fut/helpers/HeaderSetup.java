package com.stgsporting.quiz_fut.helpers;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quiz_fut_draft.R;
import com.google.firebase.database.DataSnapshot;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.Objects;

public class HeaderSetup {

    public HeaderSetup(AppCompatActivity layout, DataSnapshot snapshot, String[] data) {
        TextView stars = layout.findViewById(R.id.rating);
        TextView coins = layout.findViewById(R.id.coins);
        TextView name = layout.findViewById(R.id.nametextview);
        String new_name = Arrays.stream(data[0].split("\\s+"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
        name.setText(new_name);
        DataSnapshot userData = snapshot.child("Users").child(data[0]);
        stars.setText(Objects.requireNonNull(userData.child("Stars").getValue()).toString());
        coins.setText(Objects.requireNonNull(userData.child("Coins").getValue()).toString());
    }

}
