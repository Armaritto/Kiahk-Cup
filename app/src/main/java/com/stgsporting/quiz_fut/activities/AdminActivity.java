package com.stgsporting.quiz_fut.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.stgsporting.quiz_fut.R;


public class AdminActivity extends AppCompatActivity {

    private String[] data;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Button manageQuizzes = findViewById(R.id.manage_quizzes);
        Button manageCards = findViewById(R.id.manage_cards);
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
    }
}
