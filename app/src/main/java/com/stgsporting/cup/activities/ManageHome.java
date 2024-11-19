package com.stgsporting.cup.activities;

import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stgsporting.cup.R;
import com.stgsporting.cup.helpers.LoadingDialog;

public class ManageHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Switch mosab2a = findViewById(R.id.mosab2a);
        Switch lineup = findViewById(R.id.lineup);
        Switch myCard = findViewById(R.id.my_card);
        Switch leaderboard = findViewById(R.id.leaderboard);
        Switch store = findViewById(R.id.store);

        LoadingDialog loadingDialog = new LoadingDialog(this);

        String[] data = getIntent().getStringArrayExtra("Data");
        FirebaseDatabase database = FirebaseDatabase.getInstance(data[1]);
        DatabaseReference ref = database.getReference().child("elmilad25").child("Buttons");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mosab2a.setChecked(Boolean.parseBoolean(snapshot.child("Mosab2a").getValue().toString()));
                lineup.setChecked(Boolean.parseBoolean(snapshot.child("Lineup").getValue().toString()));
                myCard.setChecked(Boolean.parseBoolean(snapshot.child("My Card").getValue().toString()));
                leaderboard.setChecked(Boolean.parseBoolean(snapshot.child("Leaderboard").getValue().toString()));
                store.setChecked(Boolean.parseBoolean(snapshot.child("Store").getValue().toString()));
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageHome.this, "Database Error", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        mosab2a.setOnCheckedChangeListener((buttonView, isChecked) -> ref.child("Mosab2a").setValue(isChecked));
        lineup.setOnCheckedChangeListener((buttonView, isChecked) -> ref.child("Lineup").setValue(isChecked));
        myCard.setOnCheckedChangeListener((buttonView, isChecked) -> ref.child("My Card").setValue(isChecked));
        leaderboard.setOnCheckedChangeListener((buttonView, isChecked) -> ref.child("Leaderboard").setValue(isChecked));
        store.setOnCheckedChangeListener((buttonView, isChecked) -> ref.child("Store").setValue(isChecked));

    }
}