package com.example.quiz_fut_draft;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private String Name;
    private String ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent1 = getIntent();
        ID = intent1.getStringExtra("ID");
        Name = intent1.getStringExtra("Name");
        setupHeader(FirebaseDatabase.getInstance().getReference("/Login"));
        Button[] buttons = new Button[4];
        buttons[0] = findViewById(R.id.mosab2a);
        buttons[1] = findViewById(R.id.lineup);
        buttons[2] = findViewById(R.id.myCard);
        buttons[3] = findViewById(R.id.leaderboard);
        Button admin = findViewById(R.id.admin);
        Button logout = findViewById(R.id.logout);
        if(Objects.equals(ID, "9999")){
            admin.setVisibility(View.VISIBLE);
            admin.setOnClickListener(v-> {
                showCustomDialog();
            });
        }
        buttons[0].setOnClickListener(v-> {
            Intent intent = new Intent(MainActivity.this, Mosab2aActivity.class);
            intent.putExtra("ID",ID);
            intent.putExtra("Name",Name);
            startActivity(intent);
        });
        buttons[1].setOnClickListener(v-> {
            Intent intent = new Intent(MainActivity.this, LineupActivity.class);
            intent.putExtra("ID",ID);
            intent.putExtra("Name",Name);
            startActivity(intent);
        });
        buttons[2].setOnClickListener(v-> {
            Intent intent = new Intent(MainActivity.this, MyCardActivity.class);
            intent.putExtra("ID",ID);
            intent.putExtra("Name",Name);
            startActivity(intent);
        });
        buttons[3].setOnClickListener(v-> {
            Intent intent = new Intent(MainActivity.this, LeaderboardActivity.class);
            intent.putExtra("ID",ID);
            intent.putExtra("Name",Name);
            startActivity(intent);
        });
        logout.setOnClickListener(v-> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
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
                stars.setText(Objects.requireNonNull(snapshot.child(ID).child("Stars").getValue()).toString());
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