package com.stgsporting.quiz_fut.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz_fut_draft.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stgsporting.quiz_fut.adapters.PositionStoreAdapter;
import com.stgsporting.quiz_fut.data.Position;
import com.stgsporting.quiz_fut.helpers.HeaderSetup;
import com.stgsporting.quiz_fut.helpers.LoadingDialog;

import java.util.ArrayList;

public class PositionStoreActivity extends AppCompatActivity {

    private String[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_store);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LoadingDialog loadingDialog = new LoadingDialog(this);

        data = getIntent().getStringArrayExtra("Data");

        FirebaseDatabase database = FirebaseDatabase.getInstance(data[1]);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        DatabaseReference ref = database.getReference();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataS) {
                new HeaderSetup(PositionStoreActivity.this, dataS.child("elmilad25"), data);
                DataSnapshot snapshot = dataS.child("elmilad25").child("CardPosition");
                ArrayList<Position> positions = new ArrayList<>();
                ArrayList<String> positionsIds = new ArrayList<>();
                for (DataSnapshot p : snapshot.getChildren()) {
                    String id = p.getKey();
                    Position position = new Position();
                    position.setId(id);
                    position.setPosition(p.child("Position").getValue().toString());
                    position.setPrice(Integer.parseInt(p.child("Price").getValue().toString()));
                    positions.add(position);
                    positionsIds.add(id);
                }

                DataSnapshot ownedPositions = dataS.child(
                        "/elmilad25/Users").child(data[0]).child("Owned Positions");
                for (DataSnapshot position : ownedPositions.getChildren()) {
                    if (Boolean.parseBoolean(position.child("Owned").getValue().toString())) {
                        String positionId = position.getKey();
                        if (positionsIds.contains(positionId)) {
                            positions.get(positionsIds.indexOf(positionId)).setOwned(true);
                        }
                    }
                }

               PositionStoreAdapter adapter = new PositionStoreAdapter(
                       PositionStoreActivity.this, positions, database, data[0]);
               recyclerView.setAdapter(adapter);
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                loadingDialog.dismiss();
                Toast.makeText(PositionStoreActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}