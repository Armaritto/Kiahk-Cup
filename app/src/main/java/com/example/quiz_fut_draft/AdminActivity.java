package com.example.quiz_fut_draft;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
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

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    private ListAdapter listAdapter;
    private String grade;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText search = findViewById(R.id.search);
        ListView users_list = findViewById(R.id.users_list);

        Intent intent = getIntent();
        String ID = intent.getStringExtra("ID");
        String name = intent.getStringExtra("Name");
        grade = intent.getStringExtra("Grade");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DataSnapshot userData = snapshot.child(Users_Path.getPath(grade));
                ArrayList<User> users = new ArrayList<>();

                for (DataSnapshot user : userData.getChildren()) {
                    User u = new User();
                    u.setID(user.getKey());
                    u.setName(user.child("Name").getValue().toString());
                    users.add(u);
                }

                listAdapter = new AdminActivity.ListAdapter(users, snapshot);
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
        private DataSnapshot snapshot;

        public ListAdapter(ArrayList<User> users, DataSnapshot snapshot) {
            this.originalUsers = users;
            this.filteredUsers = users;
            this.snapshot = snapshot;
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
            View v = getLayoutInflater().inflate(R.layout.users_listitem, null);
            Button name = v.findViewById(R.id.name);
            name.setText(filteredUsers.get(position).getName());
            name.setOnClickListener(v1-> {
                showDialog(filteredUsers.get(position), snapshot);
            });
            return v;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {

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
                            String data = originalUsers.get(i).getName();
                            if (data.toLowerCase().startsWith(constraint.toString())) {
                                FilteredArrList.add(originalUsers.get(i));
                            }
                        }
                        results.count = FilteredArrList.size();
                        results.values = FilteredArrList;
                    }
                    return results;
                }
            };
            return filter;
        }
    }

    private android.app.AlertDialog alertDialog;

    private void showDialog(User user, DataSnapshot snapshot) {
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.admin_dialog, null);

        // Create the AlertDialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Get the UI elements from the custom layout
        TextView name = dialogView.findViewById(R.id.name);
        name.setText(user.getName());
        ListView options = dialogView.findViewById(R.id.options);

        ArrayList<Option> optionsList = new ArrayList<>();

        DataSnapshot optionsData = snapshot.child("elmilad25").child("Admin").child("J"+grade);
        for (DataSnapshot optionData : optionsData.getChildren()) {
            Option o = new Option();
            o.setOptionName(optionData.getKey());
            o.setStars(Integer.parseInt(optionData.getValue().toString()));
            optionsList.add(o);
        }

        options.setAdapter(new DialogAdapter(user.getID(), snapshot, optionsList));

        // Show the dialog
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();
    }

    private class DialogAdapter extends BaseAdapter {

        private String ID;
        private DataSnapshot snapshot;
        private ArrayList<Option> options;

        public DialogAdapter(String ID, DataSnapshot snapshot, ArrayList<Option> options) {
            this.ID = ID;
            this.snapshot = snapshot;
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
            View v = getLayoutInflater().inflate(R.layout.admin_options_listitem, null);
            Button option = v.findViewById(R.id.option);
            option.setText(options.get(position).getOptionName()+" ("+options.get(position).getStars()+")");
            option.setOnClickListener(v1-> {
                int stars = Integer.parseInt(
                        snapshot.child(Users_Path.getPath(grade)).child(ID).child("Stars").getValue().toString());
                stars+=options.get(position).getStars();
                ref.child(Users_Path.getPath(grade)).child(ID).child("Stars").setValue(stars);
                Toast.makeText(AdminActivity.this, options.get(position).getStars()+" stars added",
                        Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            });
            return v;
        }
    }


}