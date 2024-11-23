package com.stgsporting.cup.activities;

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

import com.stgsporting.cup.R;
import com.stgsporting.cup.helpers.LoadingDialog;
import com.stgsporting.cup.helpers.NetworkUtils;


public class GradeActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
        if (sharedPreferences.contains("Database") && sharedPreferences.contains("Storage")){
            loadingDialog = new LoadingDialog(this);
            moveToLogin(sharedPreferences.getString("Database", ""),
                    sharedPreferences.getString("Storage", ""));
        }


        RadioGroup grade_selection = findViewById(R.id.grade_selection);
        Button cont = findViewById(R.id.cont);

        cont.setOnClickListener(v-> {
            if (!NetworkUtils.isOnline(this)) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton selected = findViewById(grade_selection.getCheckedRadioButtonId());
            if (selected == null) {
                Toast.makeText(this, "You must choose your grade", Toast.LENGTH_SHORT).show();
                return;
            }
            loadingDialog = new LoadingDialog(this);
            switch (selected.getText().toString()) {
                case "Junior 1":
                  setupDatabase("https://quiz-fut-draft-default-rtdb.firebaseio.com/",
                            "gs://quiz-fut-draft.appspot.com/", "1");
                    break;
                case "Junior 2":
                    setupDatabase("https://j2-fut-draft-default-rtdb.firebaseio.com/",
                            "gs://j2-fut-draft.appspot.com", "2");
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
        moveToLogin(dbURL, storageURL);
    }

    private void moveToLogin(String dbURL, String storageURL) {
        loadingDialog.dismiss();
        Intent intent = new Intent(GradeActivity.this, LoginActivity.class);
        intent.putExtra("Database", dbURL);
        intent.putExtra("Storage", storageURL);
        startActivity(intent);
        finish();
    }

}