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
import com.stgsporting.cup.data.Position;

import java.util.ArrayList;

public class ManagePositions extends AppCompatActivity {

    private DatabaseReference ref;

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

        ListView optionsList = findViewById(R.id.options);
        LinearLayout add = findViewById(R.id.add);
        add.setVisibility(View.GONE);

        String[] data = getIntent().getStringArrayExtra("Data");
        FirebaseDatabase database = FirebaseDatabase.getInstance(data[1]);
        ref = database.getReference().child("elmilad25").child("CardPosition");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Position> positions = new ArrayList<>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Position p = new Position();
                    p.setId(item.getKey());
                    p.setPosition(item.child("Position").getValue().toString());
                    p.setPrice(Integer.parseInt(item.child("Price").getValue().toString()));
                    positions.add(p);
                }
                ListAdapter adapter = new ListAdapter(positions);
                optionsList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManagePositions.this, "Database Error", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    private class ListAdapter extends BaseAdapter {

        private ArrayList<Position> positions;

        public ListAdapter(ArrayList<Position> positions) {
            this.positions = positions;
        }

        @Override
        public int getCount() {
            return positions.size();
        }

        @Override
        public Position getItem(int position) {
            return positions.get(position);
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
            title.setText(positions.get(position).getPosition());
            content.setText(positions.get(position).getPrice()+" ★");
            v.setOnClickListener(v1-> {
                showDialog(positions.get(position));
            });
            return v;
        }
    }

    private AlertDialog alertDialog;

    private void showDialog(Position p) {
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

        delete.setVisibility(View.GONE);

        title.setText(p.getPosition());
        title.setEnabled(false);
        stars.setText(String.valueOf(p.getPrice()));

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
            ref.child(p.getId()).child("Price").setValue(Integer.parseInt(stars.getText().toString()));
            alertDialog.dismiss();
        });

        // Show the dialog
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();
    }

}