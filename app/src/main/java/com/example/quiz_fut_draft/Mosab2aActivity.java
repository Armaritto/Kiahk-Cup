package com.example.quiz_fut_draft;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

import java.util.ArrayList;
import java.util.Objects;

public class Mosab2aActivity extends AppCompatActivity {
    private String Name;
    private String ID;
    private String grade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mosab2a);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        ID = intent.getStringExtra("ID");
        Name = intent.getStringExtra("Name");
        grade = intent.getStringExtra("Grade");

        RecyclerView mosab2at_list = findViewById(R.id.mosab2at_list);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        setupHeader(ref);
        int numberOfColumns = 2;
        mosab2at_list.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Mosab2a> mosab2at = new ArrayList<>();
                ArrayList<String> mosab2atIDs = new ArrayList<>();
                assert grade != null;
                DataSnapshot mosab2atData = snapshot.child(Mosab2a_Path.getPath(grade));
                for (DataSnapshot mosab2a : mosab2atData.getChildren()) {
                    Mosab2a m = new Mosab2a();
                    mosab2atIDs.add(mosab2a.getKey());
                    m.setId(mosab2a.getKey());
                    m.setTitle(mosab2a.child("Title").getValue(String.class));
                    m.setCoins(mosab2a.child("Points").getValue().toString());
                    m.setLink(mosab2a.child("Link").getValue(String.class));
                    mosab2at.add(m);
                }
                DataSnapshot userM = snapshot.child(Users_Path.getPath(grade)).child(ID).child("Mosab2at");
                for (DataSnapshot mosab2a : userM.getChildren()) {
                    if (mosab2atIDs.contains(mosab2a.getKey())) mosab2at.remove(mosab2atIDs.indexOf(mosab2a.getKey()));
                }

                Mosab2atAdapter adapter = new Mosab2atAdapter(Mosab2aActivity.this, mosab2at);
                mosab2at_list.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Mosab2aActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupHeader(DatabaseReference ref) {
        TextView stars = findViewById(R.id.rating);
        TextView coins = findViewById(R.id.coins);
        TextView name = findViewById(R.id.nametextview);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setText(Name);
                snapshot = snapshot.child(Users_Path.getPath(grade));
                stars.setText(Objects.requireNonNull(snapshot.child(ID).child("Stars").getValue()).toString());
                coins.setText(Objects.requireNonNull(snapshot.child(ID).child("Coins").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}