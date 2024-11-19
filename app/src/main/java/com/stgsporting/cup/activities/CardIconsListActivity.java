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
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.stgsporting.cup.R;
import com.stgsporting.cup.data.CardIcon;
import com.stgsporting.cup.helpers.LoadingDialog;

import java.util.ArrayList;
import java.util.Objects;

public class CardIconsListActivity extends AppCompatActivity {

    private ListAdapter listAdapter;
    private String[] data;
    private LoadingDialog loadingDialog;
    private int imgsToLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_card_icons_list);
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
            Intent intent = new Intent(CardIconsListActivity.this, AddCardIconActivity.class);
            intent.putExtra("Data", data);
            startActivity(intent);
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance(Objects.requireNonNull(data)[1]);
        FirebaseStorage storage = FirebaseStorage.getInstance(data[2]);
        DatabaseReference ref = database.getReference();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DataSnapshot storeData = snapshot.child("/elmilad25/CardIcon");
                ArrayList<CardIcon> cards = new ArrayList<>();

                for (DataSnapshot cardData : storeData.getChildren()) {
                    CardIcon cardIcon = new CardIcon();
                    cardIcon.setCard(cardData.getKey());
                    if (cardData.hasChild("Image"))
                        cardIcon.setImagePath(cardData.child("Image").getValue().toString());
                    if (cardData.hasChild("Name"))
                        cardIcon.setCardName(cardData.child("Name").getValue().toString());
                    cards.add(cardIcon);
                }

                imgsToLoad = cards.size();
                for (int i=0;i<cards.size();i++) {
                    CardIcon c = cards.get(i);
                    if (c.getImagePath()==null) continue;
                    StorageReference storageRef = storage.getReference().child(c.getImagePath());
                    final int j = i;
                    storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String downloadUrl = uri.toString();
                                cards.get(j).setImageLink(downloadUrl);
                                imgsToLoad--;
                                if (imgsToLoad==0) {
                                    listAdapter = new ListAdapter(cards);
                                    cards_list.setAdapter(listAdapter);
                                    loadingDialog.dismiss();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(CardIconsListActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                                imgsToLoad--;
                                if (imgsToLoad==0) {
                                    listAdapter = new ListAdapter(cards);
                                    cards_list.setAdapter(listAdapter);
                                    loadingDialog.dismiss();
                                }
                            });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CardIconsListActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
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

        private ArrayList<CardIcon> originalCards;
        private ArrayList<CardIcon> filteredCards;

        public ListAdapter(ArrayList<CardIcon> cards) {
            this.originalCards = cards;
            this.filteredCards = cards;
        }

        @Override
        public int getCount() {
            if (filteredCards!=null)
                return filteredCards.size();
            else
                return 0;
        }

        @Override
        public CardIcon getItem(int position) {
            return filteredCards.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint({"InflateParams", "ViewHolder"})
            View v = getLayoutInflater().inflate(R.layout.activity_store_item, null);
            ImageView img = v.findViewById(R.id.img);
            TextView price = v.findViewById(R.id.price);
            Button purchase = v.findViewById(R.id.purchase);
            Button sell = v.findViewById(R.id.sell);

            price.setVisibility(View.GONE);
            purchase.setVisibility(View.GONE);
            sell.setVisibility(View.GONE);

            Picasso.get().load(filteredCards.get(position).getImageLink()).into(img);
            v.setOnClickListener(v1 -> {
                Intent intent = new Intent(CardIconsListActivity.this, CardIconEditorActivity.class);
                intent.putExtra("Data", data);
                intent.putExtra("SelectedCard", filteredCards.get(position).getCard());
                startActivity(intent);
            });
            return v;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    filteredCards = (ArrayList<CardIcon>) results.values; // has the filtered values
                    notifyDataSetChanged();  // notifies the data with new filtered values
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                    ArrayList<CardIcon> FilteredArrList = new ArrayList<>();

                    if (originalCards == null) {
                        originalCards = new ArrayList<>(filteredCards);
                    }

                    if (constraint == null || constraint.length() == 0) {
                        results.count = originalCards.size();
                        results.values = originalCards;
                    } else {
                        constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < originalCards.size(); i++) {
                            if (originalCards.get(i)!=null) {
                                String data1 = originalCards.get(i).getCard().toLowerCase();
                                if (data1.startsWith(constraint.toString())) {
                                    if (!FilteredArrList.contains(originalCards.get(i))) FilteredArrList.add(originalCards.get(i));
                                }

                                if (originalCards.get(i).getCardName()!=null) {
                                    String data2 = originalCards.get(i).getCardName().toLowerCase();
                                    if (data2.contains(constraint.toString())) {
                                        if (!FilteredArrList.contains(originalCards.get(i))) FilteredArrList.add(originalCards.get(i));
                                    }
                                }
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