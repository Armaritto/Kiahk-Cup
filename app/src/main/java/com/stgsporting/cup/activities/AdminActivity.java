package com.stgsporting.cup.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stgsporting.cup.R;
import com.stgsporting.cup.helpers.LoadingDialog;


public class AdminActivity extends AppCompatActivity {

    private String[] data;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Button manageQuizzes = findViewById(R.id.manage_quizzes);
        Button manageCards = findViewById(R.id.manage_cards);
        Button manageStars = findViewById(R.id.manage_stars);
        Button manageCardIcons = findViewById(R.id.manage_cardicons);
        Button managePositions = findViewById(R.id.manage_positions);
        Button manageRatingPrice = findViewById(R.id.manage_rating_price);

        Button manageButtons = findViewById(R.id.manage_buttons);

        Button usersList = findViewById(R.id.view_users_list);

        data = getIntent().getStringArrayExtra("Data");

        usersList.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, UsersListActivity.class);
            intent.putExtra("Data", data);
            startActivity(intent);
        });

        manageQuizzes.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AddQuizActivity.class);
            intent.putExtra("Data", data);
            startActivity(intent);
        });

        manageCards.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, CardsListActivity.class);
            intent.putExtra("Data", data);
            startActivity(intent);
        });

        manageStars.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, ManageStarsActivity.class);
            intent.putExtra("Data", data);
            startActivity(intent);
        });

        manageCardIcons.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, CardIconsListActivity.class);
            intent.putExtra("Data", data);
            startActivity(intent);
        });

        managePositions.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, ManagePositions.class);
            intent.putExtra("Data", data);
            startActivity(intent);
        });

        manageRatingPrice.setOnClickListener(v -> {
            FirebaseDatabase database = FirebaseDatabase.getInstance(data[1]);
            DatabaseReference ref = database.getReference().child("elmilad25").child("Rating Price");
            showDialog(ref);
        });


        manageButtons.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, ManageHome.class);
            intent.putExtra("Data", data);
            startActivity(intent);
        });


    }

    private AlertDialog alertDialog;

    private void showDialog(DatabaseReference ref) {
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.stars_dialog, null);

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Get the UI elements from the custom layout
        EditText title = dialogView.findViewById(R.id.title);
        EditText stars = dialogView.findViewById(R.id.stars);
        Button dialogButton = dialogView.findViewById(R.id.dialog_button);
        TextView delete = dialogView.findViewById(R.id.delete);

        delete.setVisibility(View.GONE);
        title.setVisibility(View.GONE);

        LoadingDialog loadingDialog = new LoadingDialog(AdminActivity.this);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stars.setText(snapshot.getValue().toString());
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

        // Set up the dialog button click listener
        dialogButton.setOnClickListener(v -> {
            try {
                Integer.parseInt(stars.getText().toString());
            } catch (Exception e) {
                stars.setError("Please enter stars (digits only)");
                return;
            }
            ref.setValue(Integer.parseInt(stars.getText().toString()));
            alertDialog.dismiss();
        });

        // Show the dialog
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();
    }

}
