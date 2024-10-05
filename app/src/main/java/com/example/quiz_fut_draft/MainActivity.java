package com.example.quiz_fut_draft;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private String Name;
    private String ID;
    private String dbURL;
    private String storageURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent1 = getIntent();
        ID = intent1.getStringExtra("ID");
        Name = intent1.getStringExtra("Name");
        dbURL = intent1.getStringExtra("Database");
        storageURL = intent1.getStringExtra("Storage");
        FirebaseDatabase database = FirebaseDatabase.getInstance(dbURL);
        setupHeader(database.getReference("/elmilad25/Users"));
        Button[] buttons = new Button[4];
        buttons[0] = findViewById(R.id.mosab2a);
        buttons[1] = findViewById(R.id.lineup);
        buttons[2] = findViewById(R.id.myCard);
        buttons[3] = findViewById(R.id.leaderboard);
        Button admin = findViewById(R.id.admin);
        Button logout = findViewById(R.id.logout);
        if(Objects.equals(ID, "9999")){
            admin.setVisibility(View.VISIBLE);
            admin.setOnClickListener(v-> showCustomDialog());
            buttons[3].setVisibility(View.VISIBLE);
        }
        else { // View Leaderboard for admin all the time
            database.getReference("elmilad25").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild("Leaderboard") &&
                            Boolean.parseBoolean(snapshot.child("Leaderboard").getValue().toString())) {
                        buttons[3].setVisibility(View.VISIBLE);
                    } else {
                        buttons[3].setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

        buttons[0].setOnClickListener(v-> {
            Intent intent = new Intent(MainActivity.this, Mosab2aActivity.class);
            intent.putExtra("ID",ID);
            intent.putExtra("Name",Name);
            intent.putExtra("Database", dbURL);
            intent.putExtra("Storage", storageURL);
            startActivity(intent);
        });
        buttons[1].setOnClickListener(v-> {
            Intent intent = new Intent(MainActivity.this, LineupActivity.class);
            intent.putExtra("ID",ID);
            intent.putExtra("Name",Name);
            intent.putExtra("Database", dbURL);
            intent.putExtra("Storage", storageURL);
            startActivity(intent);
        });
        buttons[2].setOnClickListener(v-> {
            Intent intent = new Intent(MainActivity.this, MyCardActivity.class);
            intent.putExtra("ID",ID);
            intent.putExtra("Name",Name);
            intent.putExtra("Database", dbURL);
            intent.putExtra("Storage", storageURL);
            startActivity(intent);
        });
        buttons[3].setOnClickListener(v-> {
            Intent intent = new Intent(MainActivity.this, LeaderboardActivity.class);
            intent.putExtra("ID",ID);
            intent.putExtra("Name",Name);
            intent.putExtra("Database", dbURL);
            intent.putExtra("Storage", storageURL);
            startActivity(intent);
        });
        logout.setOnClickListener(v-> {
            Intent intent = new Intent(MainActivity.this, GradeActivity.class);
            SharedPreferences.Editor editor = getSharedPreferences("Login", MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();
            startActivity(intent);
            finish();
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
                if (!snapshot.child(ID).hasChild("Stars")) {
                    ref.child(ID).child("Stars").setValue(0);
                    stars.setText("0");
                } else
                    stars.setText(Objects.requireNonNull(snapshot.child(ID).child("Stars").getValue()).toString());
                if (!snapshot.child(ID).hasChild("Coins")) {
                    ref.child(ID).child("Coins").setValue(0);
                    coins.setText("0");
                } else
                    coins.setText(Objects.requireNonNull(snapshot.child(ID).child("Coins").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private android.app.AlertDialog alertDialog;

    public void showCustomDialog() {
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.password_dialog, null);

        // Create the AlertDialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Get the UI elements from the custom layout
        EditText dialogInput = dialogView.findViewById(R.id.dialog_input);
        Button dialogButton = dialogView.findViewById(R.id.dialog_button);

        // Set up the dialog button click listener
        dialogButton.setOnClickListener(v -> {
            String input = dialogInput.getText().toString();
            // Handle the input
            if (input.equals("admin")) {
                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                intent.putExtra("ID",ID);
                intent.putExtra("Name",Name);
                intent.putExtra("Database", dbURL);
                intent.putExtra("Storage", storageURL);
                startActivity(intent);
            }
            // Dismiss the dialog
            alertDialog.dismiss();
        });

        // Show the dialog
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();
    }

}