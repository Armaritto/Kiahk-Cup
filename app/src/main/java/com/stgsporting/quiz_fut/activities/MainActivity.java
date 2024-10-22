package com.stgsporting.quiz_fut.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quiz_fut_draft.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stgsporting.quiz_fut.helpers.HeaderSetup;
import com.stgsporting.quiz_fut.helpers.LoadingDialog;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private String[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoadingDialog loadingDialog = new LoadingDialog(this);

        data = getIntent().getStringArrayExtra("Data");

        FirebaseDatabase database = FirebaseDatabase.getInstance(data[1]);
        Button mosab2a = findViewById(R.id.mosab2a);
        Button lineup = findViewById(R.id.lineup);
        Button myCard = findViewById(R.id.myCard);
        Button leaderboard = findViewById(R.id.leaderboard);
        Button admin = findViewById(R.id.admin);
        Button logout = findViewById(R.id.logout);
        if(Objects.equals(data[0], "Admin")){
            admin.setVisibility(View.VISIBLE);
            admin.setOnClickListener(v-> showCustomDialog());
            leaderboard.setVisibility(View.VISIBLE);
            loadingDialog.dismiss();
        }
        else { // View Leaderboard for admin all the time
            database.getReference("elmilad25").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    new HeaderSetup(MainActivity.this, snapshot, data);
                    if (snapshot.hasChild("Leaderboard") &&
                            Boolean.parseBoolean(snapshot.child("Leaderboard").getValue().toString())) {
                        leaderboard.setVisibility(View.VISIBLE);
                    } else {
                        leaderboard.setVisibility(View.GONE);
                    }
                    loadingDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    loadingDialog.dismiss();
                    Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

        mosab2a.setOnClickListener(v-> {
            Intent intent = new Intent(MainActivity.this, Mosab2aActivity.class);
            intent.putExtra("Data", data);
            startActivity(intent);
        });
        lineup.setOnClickListener(v-> {
            Intent intent = new Intent(MainActivity.this, LineupActivity.class);
            intent.putExtra("Data", data);
            startActivity(intent);
        });
        myCard.setOnClickListener(v-> {
            Intent intent = new Intent(MainActivity.this, MyCardActivity.class);
            intent.putExtra("Data", data);
            startActivity(intent);
        });
        leaderboard.setOnClickListener(v-> {
            Intent intent = new Intent(MainActivity.this, LeaderboardActivity.class);
            intent.putExtra("Data", data);
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
                Intent intent = new Intent(MainActivity.this, UsersListActivity.class);
                intent.putExtra("Data", data);
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