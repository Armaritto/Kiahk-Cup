package com.stgsporting.quiz_fut.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.util.concurrent.CountDownLatch;


public class GradeActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_grade);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
        if (sharedPreferences.contains("Database") && sharedPreferences.contains("Storage")){
            loadingDialog = new LoadingDialog(this);
            checkMaintenance(sharedPreferences.getString("Database", ""),
                    sharedPreferences.getString("Storage", ""));
        }


        RadioGroup grade_selection = findViewById(R.id.grade_selection);
        Button cont = findViewById(R.id.cont);

        cont.setOnClickListener(v-> {
            RadioButton selected = findViewById(grade_selection.getCheckedRadioButtonId());
            if (selected == null) {
                Toast.makeText(this, "You must choose your grade", Toast.LENGTH_SHORT).show();
                return;
            }
            loadingDialog = new LoadingDialog(this);
            switch (selected.getText().toString()) {
                case "Junior 1":
//                    setupDatabase("https://j1-fut-draft-default-rtdb.firebaseio.com/",
//                            "gs://j1-fut-draft.appspot.com/", "1");
                    Intent intent = new Intent(GradeActivity.this, MaintenanceActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case "Junior 2":
                    setupDatabase("https://j2-fut-draft-default-rtdb.firebaseio.com/",
                            "gs://j2-fut-draft.appspot.com/", "2");
                    break;
                case "Junior 3":
                    setupDatabase("https://j3-fut-draft-default-rtdb.firebaseio.com/",
                            "gs://j3-fut-draft.appspot.com/", "3");
                    break;
                case "Junior 4":
                    setupDatabase("https://j4-fut-draft-default-rtdb.firebaseio.com/",
                            "gs://j4-fut-draft.appspot.com/", "4");
                    break;
                case "Junior 5":
                    setupDatabase("https://j5-fut-draft-default-rtdb.firebaseio.com/",
                            "gs://j5-fut-draft.appspot.com/", "5");
                    break;
                case "Junior 6":
                    setupDatabase("https://j6-fut-draft-default-rtdb.firebaseio.com/",
                            "gs://j6-fut-draft.appspot.com/", "6");
                    break;
                default:
                    setupDatabase("https://quiz-fut-draft-default-rtdb.firebaseio.com/",
                            "gs://quiz-fut-draft.appspot.com/", "6");
                    break;
            }
        });

    }

    private void setupDatabase(String dbURL, String storageURL, String schoolYear) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Database", dbURL);
        editor.putString("Storage", storageURL);
        editor.putString("school_year", schoolYear);
        editor.apply();
        checkMaintenance(dbURL, storageURL);
    }

    private void checkMaintenance(String dbURL, String storageURL) {
        FirebaseDatabase database = FirebaseDatabase.getInstance(dbURL);
        DatabaseReference ref = database.getReference("/elmilad25");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(Boolean.TRUE.equals(snapshot.child("Maintenance").getValue(Boolean.class))){
                    Intent intent = new Intent(GradeActivity.this, MaintenanceActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    loadingDialog.dismiss();
                    moveToLogin(dbURL, storageURL);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss();
                moveToLogin(dbURL, storageURL);
            }
        });

    }

    private void moveToLogin(String dbURL, String storageURL) {
        Intent intent = new Intent(GradeActivity.this, LoginActivity.class);
        intent.putExtra("Database", dbURL);
        intent.putExtra("Storage", storageURL);
        startActivity(intent);
        finish();
    }

}