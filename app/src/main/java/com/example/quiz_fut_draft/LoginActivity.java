package com.example.quiz_fut_draft;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity {

    private EditText editTextId;
    private EditText editTextPassword;
    private SharedPreferences sharedPreferences;
    private String dbURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
        dbURL = getIntent().getStringExtra("Database");
        if (sharedPreferences.contains("ID") && sharedPreferences.contains("Password") && sharedPreferences.contains("Name")
                && sharedPreferences.contains("Database") && sharedPreferences.contains("Storage")) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("ID", sharedPreferences.getString("ID", ""));
            intent.putExtra("Name", sharedPreferences.getString("Name", ""));
            intent.putExtra("Database", sharedPreferences.getString("Database", ""));
            intent.putExtra("Storage", sharedPreferences.getString("Storage", ""));
            startActivity(intent);
            finish();
        }

        editTextId = findViewById(R.id.editTextId);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(v -> {
            String id = editTextId.getText().toString();
            String password = editTextPassword.getText().toString();
            if (!id.isEmpty() && !password.isEmpty()) {
                validateLogin(id, password);
            } else {
                Toast.makeText(LoginActivity.this, "Please enter both ID and Password", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void validateLogin(String id, String password) {
        FirebaseDatabase database = FirebaseDatabase.getInstance(dbURL);
        DatabaseReference ref = database.getReference("/elmilad25/Users").child(id);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String storedPassword = dataSnapshot.child("Password").getValue(String.class);
                    if (storedPassword != null && storedPassword.equals(password)) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("ID", id);
                        editor.putString("Password", password);
                        editor.putString("Name", dataSnapshot.child("Name").getValue(String.class));
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("ID", id);
                        intent.putExtra("Name", dataSnapshot.child("Name").getValue(String.class));
                        intent.putExtra("Database", sharedPreferences.getString("Database", ""));
                        intent.putExtra("Storage", sharedPreferences.getString("Storage", ""));

                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "ID not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

}