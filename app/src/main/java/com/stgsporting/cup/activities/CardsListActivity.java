package com.stgsporting.cup.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stgsporting.cup.R;
import com.stgsporting.cup.data.Card;
import com.stgsporting.cup.helpers.LoadingDialog;

import java.util.ArrayList;
import java.util.Objects;

public class CardsListActivity extends AppCompatActivity {

    private ListAdapter listAdapter;
    private String[] data;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cards_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadingDialog = new LoadingDialog(this);

        EditText search = findViewById(R.id.search);
        ListView cards_list = findViewById(R.id.cards_list);

        data = getIntent().getStringArrayExtra("Data");

        LinearLayout add = findViewById(R.id.add);
        add.setOnClickListener(v -> {
            Intent intent = new Intent(CardsListActivity.this, AddCardActivity.class);
            intent.putExtra("Data", data);
            startActivity(intent);
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance(Objects.requireNonNull(data)[1]);
        DatabaseReference ref = database.getReference();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DataSnapshot storeData = snapshot.child("/elmilad25/Store");
                ArrayList<Card> cards = new ArrayList<>();

                for (DataSnapshot cardData : storeData.getChildren()) {
                    Card c = new Card();
                    c.setID(cardData.getKey());
                    if (cardData.hasChild("Image"))
                        c.setImagePath(cardData.child("Image").getValue().toString());
                    if (cardData.hasChild("Position"))
                        c.setPosition(cardData.child("Position").getValue().toString());
                    if (cardData.hasChild("Price"))
                        c.setPrice(Integer.parseInt(cardData.child("Price").getValue().toString()));
                    if (cardData.hasChild("Rating"))
                        c.setRating(cardData.child("Rating").getValue().toString());
                    if (cardData.hasChild("Name"))
                        c.setName(cardData.child("Name").getValue().toString());
                    cards.add(c);
                }

                listAdapter = new ListAdapter(cards);
                cards_list.setAdapter(listAdapter);
                loadingDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CardsListActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private class ListAdapter extends BaseAdapter implements Filterable {

        private ArrayList<Card> originalCards;
        private ArrayList<Card> filteredCards;

        public ListAdapter(ArrayList<Card> cards) {
            this.originalCards = cards;
            this.filteredCards = cards;
        }

        @Override
        public int getCount() {
            return filteredCards.size();
        }

        @Override
        public Card getItem(int position) {
            return filteredCards.get(position);
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
            if (filteredCards.get(position).getName()!=null)
                name.setText(filteredCards.get(position).getName());
            else
                name.setText(filteredCards.get(position).getID());
            name.setOnClickListener(v1 -> {
                Intent intent = new Intent(CardsListActivity.this, CardEditorActivity.class);
                intent.putExtra("Data", data);
                intent.putExtra("SelectedCard", filteredCards.get(position).getID());
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
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    filteredCards = (ArrayList<Card>) results.values; // has the filtered values
                    notifyDataSetChanged();  // notifies the data with new filtered values
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                    ArrayList<Card> FilteredArrList = new ArrayList<>();

                    if (originalCards == null) {
                        originalCards = new ArrayList<>(filteredCards);
                    }

                    if (constraint == null || constraint.length() == 0) {
                        results.count = originalCards.size();
                        results.values = originalCards;
                    } else {
                        constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < originalCards.size(); i++) {
                            if (originalCards.get(i).getName()!=null) {
                                String data1 = originalCards.get(i).getName().toLowerCase();
                                if (data1.startsWith(constraint.toString())) {
                                    if (!FilteredArrList.contains(originalCards.get(i))) FilteredArrList.add(originalCards.get(i));
                                }
                            }
                            String data2 = originalCards.get(i).getID().toLowerCase();
                            if (data2.startsWith(constraint.toString())) {
                                if (!FilteredArrList.contains(originalCards.get(i))) FilteredArrList.add(originalCards.get(i));
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

}