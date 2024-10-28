package com.stgsporting.quiz_fut.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.stgsporting.quiz_fut.data.User;
import com.stgsporting.quiz_fut.helpers.Http;
import com.stgsporting.quiz_fut.helpers.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class UsersListActivity extends AppCompatActivity {

    private ListAdapter listAdapter;
    private String[] data;
    private DatabaseReference ref;
    private LoadingDialog loadingDialog;

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

        loadingDialog = new LoadingDialog(this);

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

                listAdapter = new UsersListActivity.ListAdapter(users);
                users_list.setAdapter(listAdapter);
                loadingDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UsersListActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(listAdapter == null) return;

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
                Intent intent = new Intent(UsersListActivity.this, UserEditorActivity.class);
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
            DatabaseReference usersRef = ref.child("elmilad25/Users");

            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    JSONObject dataRequest = new JSONObject();
                    JSONArray users = new JSONArray();

                    for (String n : names) {
                        Random random = new Random();
                        int passcode = random.nextInt(9999);

                        try {
                            users.put(new JSONObject().put("name", n));
                        }catch (JSONException ignored) {}

                        if (passcode<1000) passcode+=1000;
                        n = n.toLowerCase();
                        if (!snapshot.hasChild(n))
                            ref.child("elmilad25/Users").child(n).child("Passcode").setValue(passcode);
                        else Toast.makeText(UsersListActivity.this, n+" already exists!", Toast.LENGTH_SHORT).show();
                    }

                    try {
                        dataRequest.put("users", users);
                        dataRequest.put("school_year_id", data[3]);
                    } catch (JSONException ignored) {}

                    Http.post(Uri.parse(Http.URL + "/users"))
                            .expectsJson()
                            .addData(dataRequest)
                            .sendAsync().thenApply(res -> {
                                runOnUiThread(() -> {
                                    loadingDialog.dismiss();
                                    alertDialog.dismiss();

                                    if (res.getCode() == 200) {
                                        Toast.makeText(UsersListActivity.this, "Users added successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return null;
                            });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UsersListActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                    alertDialog.dismiss();
                }
            });
        });

        // Show the dialog
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();
    }

}