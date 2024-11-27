package com.stgsporting.cup;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.stgsporting.cup.helpers.Logs;

public class LogsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_logs);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText logs = findViewById(R.id.logs);
        TextView user = findViewById(R.id.user);

        String[] data = getIntent().getStringArrayExtra("Data");
        user.setText(data[0]);

        String[] logsData = Logs.getLogs(this);
        StringBuilder sb = new StringBuilder();
        for (String s : logsData) {
            sb.append(s).append("\n");
        }
        logs.setText(sb.toString());

    }
}