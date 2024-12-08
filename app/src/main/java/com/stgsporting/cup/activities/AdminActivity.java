package com.stgsporting.cup.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.stgsporting.cup.helpers.Http;
import com.stgsporting.cup.helpers.LoadingDialog;
import com.stgsporting.cup.helpers.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class AdminActivity extends AppCompatActivity {

    private String[] data;
    private Button manageQuizzes;
    private Button manageCards;
    private Button manageStars;
    private Button manageCardIcons;
    private Button managePositions;
    private Button manageRatingPrice;
    private Button manageButtons;
    private Button usersList;
    private Button errors;
    private LoadingDialog loadingDialog;
    private DatabaseReference ref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        manageQuizzes = findViewById(R.id.manage_quizzes);
        manageCards = findViewById(R.id.manage_cards);
        manageStars = findViewById(R.id.manage_stars);
        manageCardIcons = findViewById(R.id.manage_cardicons);
        managePositions = findViewById(R.id.manage_positions);
        manageRatingPrice = findViewById(R.id.manage_rating_price);
        manageButtons = findViewById(R.id.manage_buttons);
        usersList = findViewById(R.id.view_users_list);
        errors = findViewById(R.id.errors);

        data = getIntent().getStringArrayExtra("Data");

        FirebaseDatabase database = FirebaseDatabase.getInstance(data[1]);
        ref = database.getReference().child("elmilad25");

        loadingDialog = new LoadingDialog(this);

        refreshData();

        usersList.setOnClickListener(v -> {
            if (!NetworkUtils.isOnline(this)) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(AdminActivity.this, UsersListActivity.class);
            intent.putExtra("Data", data);
            startActivity(intent);
        });

        manageQuizzes.setOnClickListener(v -> {
            if (!NetworkUtils.isOnline(this)) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(AdminActivity.this, AddQuizActivity.class);
            intent.putExtra("Data", data);
            startActivity(intent);
        });

        manageCards.setOnClickListener(v -> {
            if (!NetworkUtils.isOnline(this)) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(AdminActivity.this, CardsListActivity.class);
            intent.putExtra("Data", data);
            startActivity(intent);
        });

        manageStars.setOnClickListener(v -> {
            if (!NetworkUtils.isOnline(this)) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(AdminActivity.this, ManageStarsActivity.class);
            intent.putExtra("Data", data);
            startActivity(intent);
        });

        manageCardIcons.setOnClickListener(v -> {
            if (!NetworkUtils.isOnline(this)) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(AdminActivity.this, CardIconsListActivity.class);
            intent.putExtra("Data", data);
            startActivity(intent);
        });

        managePositions.setOnClickListener(v -> {
            if (!NetworkUtils.isOnline(this)) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(AdminActivity.this, ManagePositions.class);
            intent.putExtra("Data", data);
            startActivity(intent);
        });

        manageButtons.setOnClickListener(v -> {
            if (!NetworkUtils.isOnline(this)) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(AdminActivity.this, ManageHome.class);
            intent.putExtra("Data", data);
            startActivity(intent);
        });

        errors.setOnClickListener(v -> {
            if (!NetworkUtils.isOnline(this)) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
            calculateErrors();
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        loadingDialog.show();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot rating = dataSnapshot.child("Rating Price");
                manageRatingPrice.setOnClickListener(v -> {
                    showDialog(rating, ref.child("Rating Price"));
                });

                DataSnapshot btns = dataSnapshot.child("Buttons").child("Admin");
                checkBtn(btns, "Manage Users", usersList);
                checkBtn(btns, "Manage Quizzes", manageQuizzes);
                checkBtn(btns, "Manage Cards", manageCards);
                checkBtn(btns, "Manage Stars", manageStars);
                checkBtn(btns, "Manage Card Icons", manageCardIcons);
                checkBtn(btns, "Manage Positions", managePositions);
                checkBtn(btns, "Manage Rating Price", manageRatingPrice);
                checkBtn(btns, "Manage Home", manageButtons);
                checkBtn(btns, "View Errors", errors);

                loadingDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private AlertDialog alertDialog;

    private void showDialog(DataSnapshot data, DatabaseReference ref) {
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
        stars.setText(data.getValue().toString());

        // Set up the dialog button click listener
        dialogButton.setOnClickListener(v -> {
            try {
                Integer.parseInt(stars.getText().toString());
            } catch (Exception e) {
                stars.setError("Please enter stars (digits only)");
                return;
            }
            ref.setValue(Integer.parseInt(stars.getText().toString()));
            refreshData();
            alertDialog.dismiss();
        });

        // Show the dialog
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();
    }

    private void checkBtn(DataSnapshot btns, String key, Button button) {
        if (btns.hasChild(key) &&
                Boolean.parseBoolean(btns.child(key).getValue().toString())) {
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.GONE);
        }
    }

    private JSONObject allCoins;

    private void calculateErrors() {
        loadingDialog.show();

        Http.get(Uri.parse("https://cup.stgsporting.com/api/coins/"+data[3]))
                .expectsJson()
                .sendAsync().thenApply((res) -> {
                    allCoins = res.getJson();
                    return null;
                });

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot usersData = snapshot.child("Users");

                StringBuilder allErrors = new StringBuilder();

                for (DataSnapshot userData : usersData.getChildren()) {
                    if (userData.getKey().equals("admin")) continue;
                    ArrayList<String> cardsIDs = new ArrayList<>();
                    for (DataSnapshot s : userData.child("Owned Cards").getChildren()) {
                        if (Boolean.parseBoolean(s.getValue().toString()))
                            cardsIDs.add(s.getKey());
                    }

                    int cardsPrice = 0;
                    for (String cardID : cardsIDs) {
                        int price = Integer.parseInt(snapshot.child("Store").child(cardID).child("Price").getValue().toString());
                        cardsPrice+=price;
                    }
                    int cash;
                    if (userData.hasChild("Coins"))
                        cash = Integer.parseInt(userData.child("Coins").getValue().toString());
                    else cash = 0;

                    int totalGainedCoins = cash+cardsPrice;
                    if (data[3].equals("5")) totalGainedCoins-=1000;
                    try {
                        while (allCoins==null) Thread.sleep(100);
                        int qCoins;
                        if (allCoins.has(userData.getKey()))
                            qCoins = Integer.parseInt(allCoins.get(userData.getKey()).toString());
                        else
                            qCoins = 0;

                        if (qCoins!=totalGainedCoins) {
                            int diff = totalGainedCoins - qCoins;
                            if (data[3].equals("5") && diff==100) continue;
                            if (data[3].equals("3") && (diff==300 || diff==400)) continue;
                            StringBuilder bd = new StringBuilder();
                            bd.append(userData.getKey()).append(" ");
                            if (diff>0) bd.append("+");
                            bd.append(diff);
                            allErrors.append(bd).append("\n");
                        }
                    } catch (Exception e) {
                        Log.e("AAA", e.toString());
                        Toast.makeText(AdminActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                if (loadingDialog.isShowing()) loadingDialog.dismiss();
                new com.stgsporting.cup.helpers.AlertDialog(AdminActivity.this, allErrors.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
