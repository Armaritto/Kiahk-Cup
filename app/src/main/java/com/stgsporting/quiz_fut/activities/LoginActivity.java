package com.stgsporting.quiz_fut.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.stgsporting.quiz_fut.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stgsporting.quiz_fut.helpers.LoadingDialog;

import java.util.Arrays;
import java.util.stream.Collectors;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private String dbURL;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
        dbURL = getIntent().getStringExtra("Database");
        if (sharedPreferences.contains("Name")
                && sharedPreferences.contains("Database") && sharedPreferences.contains("Storage")) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            String[] data = {
                    sharedPreferences.getString("Name", ""),
                    sharedPreferences.getString("Database", ""),
                    sharedPreferences.getString("Storage", ""),
                    sharedPreferences.getString("school_year", "")
            };
            intent.putExtra("Data", data);
            startActivity(intent);
            finish();
        }

        EditText name_edittext = findViewById(R.id.name_edittext);
        EditText passcode_edittext = findViewById(R.id.passcode_edittext);
        Button buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(v -> {
            String name = name_edittext.getText().toString();
            String passcode = passcode_edittext.getText().toString();
            if (!name.isEmpty() && !passcode.isEmpty()) {
                name = name.toLowerCase();
                validateLogin(name, passcode);
            } else {
                Toast.makeText(LoginActivity.this, "Please enter both ID and Password", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void validateLogin(String name, String passcode) {
        loadingDialog = new LoadingDialog(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance(dbURL);
        DatabaseReference ref = database.getReference("/elmilad25/Users").child(name);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (!dataSnapshot.hasChild("Passcode")) {
                        Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String storedPasscode = dataSnapshot.child("Passcode").getValue().toString();
                    if (storedPasscode.equals(passcode)) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("Name", name);
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        String[] data = {
                                name,
                                sharedPreferences.getString("Database", ""),
                                sharedPreferences.getString("Storage", ""),
                                sharedPreferences.getString("school_year", "")
                        };
                        intent.putExtra("Data", data);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Incorrect Passcode", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Name not found", Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

}