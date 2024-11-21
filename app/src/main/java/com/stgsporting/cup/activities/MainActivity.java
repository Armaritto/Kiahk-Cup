package com.stgsporting.cup.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.stgsporting.cup.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stgsporting.cup.helpers.ConfirmDialog;
import com.stgsporting.cup.helpers.Header;
import com.stgsporting.cup.helpers.LoadingDialog;
import com.stgsporting.cup.helpers.NetworkUtils;
import com.stgsporting.cup.helpers.UpdateDialog;


import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private String[] data;
    private static final double current_version = 0.9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LoadingDialog loadingDialog = new LoadingDialog(this);

        data = getIntent().getStringArrayExtra("Data");
        Header.render(this, Objects.requireNonNull(data));

        FirebaseDatabase database = FirebaseDatabase.getInstance(data[1]);
        Button mosab2a = findViewById(R.id.mosab2a);
        Button lineup = findViewById(R.id.lineup);
        Button myCard = findViewById(R.id.myCard);
        Button leaderboard = findViewById(R.id.leaderboard);
        Button admin = findViewById(R.id.admin);
        Button logout = findViewById(R.id.logout);
        if(Objects.equals(data[0], "admin")){
            admin.setVisibility(View.VISIBLE);
            admin.setOnClickListener(v-> {
                if (!NetworkUtils.isOnline(this)) {
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                openAdminPanel();
            });
            leaderboard.setVisibility(View.VISIBLE);
            loadingDialog.dismiss();
        }
        database.getReference("elmilad25").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(current_version < dataSnapshot.child("Version").getValue(Double.class)){
                    View.OnClickListener updateListener = v1 -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.stgsporting.cup"));
                        startActivity(intent);
                    };
                    new UpdateDialog(MainActivity.this,updateListener);
                }
                if(Boolean.TRUE.equals(dataSnapshot.child("Maintenance").getValue(Boolean.class))){
                    loadingDialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, MaintenanceActivity.class);
                    startActivity(intent);
                    finish();
                }

                DataSnapshot snapshot = dataSnapshot.child("Buttons");
                if (!Objects.equals(data[0], "admin")) {
                    if (snapshot.hasChild("Leaderboard") &&
                            Boolean.parseBoolean(snapshot.child("Leaderboard").getValue().toString())) {
                        leaderboard.setVisibility(View.VISIBLE);
                    } else {
                        leaderboard.setVisibility(View.GONE);
                    }
                    if (snapshot.hasChild("Lineup") &&
                            Boolean.parseBoolean(snapshot.child("Lineup").getValue().toString())) {
                        lineup.setVisibility(View.VISIBLE);
                    } else {
                        lineup.setVisibility(View.GONE);
                    }
                    if (snapshot.hasChild("Mosab2a") &&
                            Boolean.parseBoolean(snapshot.child("Mosab2a").getValue().toString())) {
                        mosab2a.setVisibility(View.VISIBLE);
                    } else {
                        mosab2a.setVisibility(View.GONE);
                    }
                    if (snapshot.hasChild("My Card") &&
                            Boolean.parseBoolean(snapshot.child("My Card").getValue().toString())) {
                        myCard.setVisibility(View.VISIBLE);
                    } else {
                        myCard.setVisibility(View.GONE);
                    }
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if(!Objects.equals(data[0], "admin")){
                    loadingDialog.dismiss();
                    Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });


        mosab2a.setOnClickListener(v-> {
            if (!NetworkUtils.isOnline(this)) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MainActivity.this, ShowQuizzesActivity.class);
            intent.putExtra("Data", data);
            startActivity(intent);
        });
        lineup.setOnClickListener(v-> {
            if (!NetworkUtils.isOnline(this)) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MainActivity.this, LineupActivity.class);
            intent.putExtra("Data", data);
            intent.putExtra("Other_Lineup", false);
            startActivity(intent);
        });
        myCard.setOnClickListener(v-> {
            if (!NetworkUtils.isOnline(this)) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MainActivity.this, MyCardActivity.class);
            intent.putExtra("Data", data);
            startActivity(intent);
        });
        leaderboard.setOnClickListener(v-> {
            if (!NetworkUtils.isOnline(this)) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
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

    public void openAdminPanel() {
        Intent intent = new Intent(MainActivity.this, AdminActivity.class);
        intent.putExtra("Data", data);
        startActivity(intent);
    }
}