package com.stgsporting.quiz_fut.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quiz_fut_draft.R;
import com.stgsporting.quiz_fut.helpers.LoadingDialog;


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
        if (sharedPreferences.contains("Database") && sharedPreferences.contains("Storage"))
            moveToLogin(sharedPreferences.getString("Database", ""),
                    sharedPreferences.getString("Storage", ""));

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
//                case "Junior 1":
//                    setupDatabase(j1);
//                    break;
//                case "Junior 2":
//                    setupDatabase(j2);
//                    break;
//                case "Junior 3":
//                    setupDatabase(j3);
//                    break;
//                case "Junior 4":
//                    setupDatabase(j4);
//                    break;
//                case "Junior 5":
//                    setupDatabase(j5);
//                    break;
                case "Junior 6":
                    setupDatabase("https://j6-fut-draft-default-rtdb.firebaseio.com/",
                            "gs://j6-fut-draft.appspot.com/");
                    break;
                default:
                    setupDatabase("https://quiz-fut-draft-default-rtdb.firebaseio.com/",
                            "gs://quiz-fut-draft.appspot.com/");
                    break;
            }
        });

    }

    private void setupDatabase(String dbURL, String storageURL) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Database", dbURL);
        editor.putString("Storage", storageURL);
        editor.apply();
        loadingDialog.dismiss();
        moveToLogin(dbURL, storageURL);
    }

    private void moveToLogin(String dbURL, String storageURL) {
        Intent intent = new Intent(GradeActivity.this, LoginActivity.class);
        intent.putExtra("Database", dbURL);
        intent.putExtra("Storage", storageURL);
        startActivity(intent);
        finish();
    }

}