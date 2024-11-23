package com.stgsporting.cup.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.stgsporting.cup.R;
import com.stgsporting.cup.data.Option;
import com.stgsporting.cup.helpers.ConfirmDialog;
import com.stgsporting.cup.helpers.LoadingDialog;

import java.util.ArrayList;

public class ManageStarsActivity extends AppCompatActivity {

    private DatabaseReference ref;
    private ListView optionsList;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_stars);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadingDialog = new LoadingDialog(this);

        optionsList = findViewById(R.id.options);
        LinearLayout add = findViewById(R.id.add);
        add.setOnClickListener(v-> {
            showDialog(true, null);
        });

        String[] data = getIntent().getStringArrayExtra("Data");
        FirebaseDatabase database = FirebaseDatabase.getInstance(data[1]);
        ref = database.getReference().child("elmilad25").child("Admin");

        refreshData();

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
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Option> options = new ArrayList<>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Option o = new Option();
                    o.setOptionName(item.getKey());
                    o.setStars(Integer.parseInt(item.getValue().toString()));
                    options.add(o);
                }
                ListAdapter adapter = new ListAdapter(options);
                optionsList.setAdapter(adapter);
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageStarsActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private class ListAdapter extends BaseAdapter {

        private ArrayList<Option> options;

        public ListAdapter(ArrayList<Option> options) {
            this.options = options;
        }

        @Override
        public int getCount() {
            return options.size();
        }

        @Override
        public Option getItem(int position) {
            return options.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = getLayoutInflater().inflate(R.layout.user_editor_listitem, null);
            TextView title = v.findViewById(R.id.title);
            TextView content = v.findViewById(R.id.content);
            title.setText(options.get(position).getOptionName());
            content.setText(options.get(position).getStars()+" ★");
            v.setOnClickListener(v1-> {
                showDialog(false, options.get(position));
            });
            return v;
        }
    }

    private AlertDialog alertDialog;
    private ConfirmDialog confirmDialog;

    private void showDialog(boolean newOption, Option option) {
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

        if (!newOption) {
            title.setText(option.getOptionName());
            title.setEnabled(false);
            stars.setText(String.valueOf(option.getStars()));
            delete.setVisibility(View.VISIBLE);
        } else {
            title.setEnabled(true);
            delete.setVisibility(View.GONE);
        }

        // Set up the dialog button click listener
        dialogButton.setOnClickListener(v -> {
            if (title.getText().toString().isEmpty()) {
                title.setError("Please enter title");
                return;
            }
            try {
                Integer.parseInt(stars.getText().toString());
            } catch (Exception e) {
                stars.setError("Please enter stars (digits only)");
                return;
            }
            ref.child(title.getText().toString()).setValue(Integer.parseInt(stars.getText().toString()));
            refreshData();
            alertDialog.dismiss();
        });

        View.OnClickListener listener = v -> {
            ref.child(title.getText().toString()).removeValue();
            if (confirmDialog!=null) confirmDialog.dismiss();
            refreshData();
            alertDialog.dismiss();
        };

        delete.setOnClickListener(v-> confirmDialog = new ConfirmDialog(ManageStarsActivity.this, listener));

        // Show the dialog
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();
    }

}