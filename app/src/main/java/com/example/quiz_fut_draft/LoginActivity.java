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
    private EditText editTextGrade;
    private Button buttonLogin;
    private SharedPreferences sharedPreferences;

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
        if (sharedPreferences.contains("ID") && sharedPreferences.contains("Password")) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("ID", sharedPreferences.getString("ID", ""));
            intent.putExtra("Grade", sharedPreferences.getString("Grade", ""));
            intent.putExtra("Name", sharedPreferences.getString("Name", ""));
            startActivity(intent);
            finish();
        }

        editTextId = findViewById(R.id.editTextId);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextGrade = findViewById(R.id.editTextGrade);
        buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(v -> {
            String id = editTextId.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String grade = editTextGrade.getText().toString().trim();
            if (!id.isEmpty() && !password.isEmpty()) {
                validateLogin(id, password,grade);
            } else {
                Toast.makeText(LoginActivity.this, "Please enter ID, Grade, and Password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validateLogin(String id, String password,String grade) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(Users_Path.getPath(grade)).child(id);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String storedPassword = dataSnapshot.child("Password").getValue(String.class);
                    if (storedPassword != null && storedPassword.equals(password)) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("ID", id);
                        intent.putExtra("Name", dataSnapshot.child("Name").getValue(String.class));
                        intent.putExtra("Grade", grade);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("ID", id);
                        editor.putString("Password", password);
                        editor.putString("Grade", grade);
                        editor.putString("Name", dataSnapshot.child("Name").getValue(String.class));
                        editor.apply();

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