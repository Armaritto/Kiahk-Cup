package com.example.quiz_fut_draft;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;

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

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class UsersList extends AppCompatActivity {

    private ListAdapter listAdapter;
    private String[] data;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_users_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText search = findViewById(R.id.search);
        ListView users_list = findViewById(R.id.users_list);

        data = getIntent().getStringArrayExtra("Data");

        LinearLayout add = findViewById(R.id.add);
        add.setOnClickListener(v-> addUsersDialog());

        FirebaseDatabase database = FirebaseDatabase.getInstance(Objects.requireNonNull(data)[1]);
        ref = database.getReference();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DataSnapshot userData = snapshot.child("/elmilad25/Users");
                ArrayList<User> users = new ArrayList<>();

                for (DataSnapshot user : userData.getChildren()) {
                    if (user.getKey().equals("NextID")) continue;
                    User u = new User();
                    u.setName(user.getKey());
                    if (user.hasChild("Passcode"))
                        u.setPasscode(Objects.requireNonNull(user.child("Passcode").getValue()).toString());
                    if (user.hasChild("ImageLink"))
                        u.setImageLink(Objects.requireNonNull(user.child("ImageLink").getValue()).toString());
                    users.add(u);
                }

                listAdapter = new UsersList.ListAdapter(users);
                users_list.setAdapter(listAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private class ListAdapter extends BaseAdapter implements Filterable {

        private ArrayList<User> originalUsers;
        private ArrayList<User> filteredUsers;

        public ListAdapter(ArrayList<User> users) {
            this.originalUsers = users;
            this.filteredUsers = users;
        }

        @Override
        public int getCount() {
            return filteredUsers.size();
        }

        @Override
        public User getItem(int position) {
            return filteredUsers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint({"InflateParams", "ViewHolder"})
            View v = getLayoutInflater().inflate(R.layout.users_listitem, null);
            Button name = v.findViewById(R.id.name);
            name.setText(filteredUsers.get(position).getName());
            name.setOnClickListener(v1-> {
                Intent intent = new Intent(UsersList.this, UserEditor.class);
                intent.putExtra("Data", data);
                intent.putExtra("SelectedUser", filteredUsers.get(position).getName());
                startActivity(intent);
//                showDialog(filteredUsers.get(position), snapshot);
            });
            return v;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint,FilterResults results) {

                    filteredUsers = (ArrayList<User>) results.values; // has the filtered values
                    notifyDataSetChanged();  // notifies the data with new filtered values
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                    ArrayList<User> FilteredArrList = new ArrayList<>();

                    if (originalUsers == null) {
                        originalUsers = new ArrayList<>(filteredUsers);
                    }

                    if (constraint == null || constraint.length() == 0) {
                        results.count = originalUsers.size();
                        results.values = originalUsers;
                    } else {
                        constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < originalUsers.size(); i++) {
                            String data1 = originalUsers.get(i).getName();
                            if (data1.toLowerCase().startsWith(constraint.toString())) {
                                FilteredArrList.add(originalUsers.get(i));
                            }
                        }
                        results.count = FilteredArrList.size();
                        results.values = FilteredArrList;
                    }
                    return results;
                }
            };
        }
    }

    private android.app.AlertDialog alertDialog;

    public void addUsersDialog() {
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.new_user_dialog, null);

        // Create the AlertDialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Get the UI elements from the custom layout
        EditText dialogInput = dialogView.findViewById(R.id.dialog_input);
        Button dialogButton = dialogView.findViewById(R.id.dialog_button);

        // Set up the dialog button click listener
        dialogButton.setOnClickListener(v -> {
            String input = dialogInput.getText().toString();
            String[] names = input.split("\n");
            for (String n : names) {
                Random random = new Random();
                int passcode = random.nextInt(9999);
                if (passcode<1000) passcode+=1000;
                ref.child("elmilad25/Users").child(n).child("Passcode").setValue(passcode);
            }
            alertDialog.dismiss();
        });

        // Show the dialog
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();
    }

}