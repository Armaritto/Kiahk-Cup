package com.stgsporting.cup.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.stgsporting.cup.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.stgsporting.cup.activities.LineupActivity;
import com.stgsporting.cup.data.Card;
import com.stgsporting.cup.helpers.ConfirmDialog;
import com.stgsporting.cup.helpers.ImageLoader;
import com.stgsporting.cup.helpers.LoadingDialog;
import com.stgsporting.cup.helpers.Logs;
import com.stgsporting.cup.helpers.NetworkUtils;

import java.util.ArrayList;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {

    private final ArrayList<Card> cards;
    private final LayoutInflater mInflater;
    private final Context context;
    private int points;
    private final FirebaseDatabase database;
    private final String name;
    private final String cardPosition;
    private FirebaseStorage storage;
    private ImageLoader imageLoader;
    private LoadingDialog loadingDialog;

    // data is passed into the constructor
    public StoreAdapter(Context context, ArrayList<Card> cards, int points, FirebaseDatabase database,
                        String name, String cardPosition, FirebaseStorage storage, ImageLoader imageLoader) {
        this.mInflater = LayoutInflater.from(context);
        this.cards = cards;
        this.context = context;
        this.points = points;
        this.database = database;
        this.name = name;
        this.cardPosition = cardPosition;
        this.storage = storage;
        this.imageLoader = imageLoader;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_store_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.price.setText(cards.get(position).getPrice()+" €");
        imageLoader.loadImage(cards.get(position).getImageLink(), holder.img);

        if (cards.get(position).isOwned()) {
            if(cards.get(position).isInLineup())
                holder.purchaseButton.setText("Selected");
            else
                holder.purchaseButton.setText("Select");
            holder.sellButton.setVisibility(View.VISIBLE);
        }
        else{
            holder.sellButton.setVisibility(View.INVISIBLE);
        }
        holder.purchaseButton.setOnClickListener(v-> {
            if (!NetworkUtils.isOnline(context)) {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
            purchasePlayer(getItem(position));
        });
        holder.sellButton.setOnClickListener(v -> {
            View.OnClickListener yesListener = v1 -> {
                if (!NetworkUtils.isOnline(context)) {
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                sellPlayer(getItem(position));
            };
            View.OnClickListener noListener = v1 -> {};
            new ConfirmDialog(context,yesListener);
        });
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return cards.size();
    }

    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img;
        TextView price;
        Button purchaseButton;
        Button sellButton;

        ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            price = itemView.findViewById(R.id.price);
            purchaseButton = itemView.findViewById(R.id.purchase);
            sellButton = itemView.findViewById(R.id.sell);
        }

        @Override
        public void onClick(View view) {}
    }

    // convenience method for getting data at click position
    Card getItem(int id) {
        return cards.get(id);
    }

    public void purchasePlayer(Card card) {

        DatabaseReference userRef = database.getReference("/elmilad25/Users").child(name);
        DatabaseReference cardRef = userRef.child("Owned Cards").child(card.getID());
        DatabaseReference storeRef = database.getReference("/elmilad25/Store");
        Logs.log(context, "Purchasing "+card.getID(), 0);
        if (card.isOwned()) {
            addPlayerInLineup(card, userRef, storeRef);
            Logs.log(context, card.getID() + " owned, added to lineup.", 1);
        } else {
            int price = card.getPrice();
            if (points < price) {
                Toast.makeText(context, "Not enough coins", Toast.LENGTH_SHORT).show();
                Logs.log(context, "Not enough coins: "+points+", price: "+price, 2);
            }
            else {
                points -= price;
                Logs.log(context, price+" coins paid", 3);
                userRef.child("Coins").setValue(points);
                Logs.log(context, "New coins: "+points, 4);
                cardRef.setValue(true);
//                userRef.child("Lineup").child(cardPosition).setValue(card.getID());
                addPlayerInLineup(card, userRef, storeRef);
                Logs.log(context, "Purchase success., added to lineup\n------------", 5);
            }
        }
    }

    private void gotoLineup() {
        Intent intent = new Intent(context, LineupActivity.class);
        String[] data = {
                name,
                database.getReference().toString(),
                storage.getReference().toString()
        };
        intent.putExtra("Data",data);
        intent.putExtra("OtherLineup",false);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    private void sellPlayer(Card card) {
        loadingDialog = new LoadingDialog(context);

        DatabaseReference userRef = database.getReference("/elmilad25/Users").child(name);
        DatabaseReference cardRef = userRef.child("Owned Cards").child(card.getID());

        userRef.child("Lineup").addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                Logs.log(context, "Selling "+card.getID(), 0);
                for (DataSnapshot S : snapshot.getChildren()) {
                    if (S.getValue().toString().equals(card.getID())){
                        userRef.child("Lineup").child(S.getKey()).removeValue();
                    }
                }
                Logs.log(context, "doesn't exist in lineup", 1);
                cardRef.removeValue().addOnSuccessListener(unused -> {
                    card.setOwned(false);
                    Logs.log(context, "removed from owned cards", 2);
                    int price = card.getPrice();
                    points += price;
                    userRef.child("Coins").setValue(points).addOnSuccessListener(unused1 -> {
                        Logs.log(context, price+" coins added, total="+points+"\n--------", 3);
                        loadingDialog.dismiss();
                        gotoLineup();
                    }).addOnFailureListener(e -> {
                        Logs.log(context, "cannot add "+price+" coins, total should be="+points+"\n--------", 3);
                        Logs.log(context, e.toString(), 4);
                        cardRef.setValue(true).addOnSuccessListener(unused12 -> {
                            Logs.log(context, "added back to owned cards", 5);
                            loadingDialog.dismiss();
                            gotoLineup();
                        }).addOnFailureListener(e1 -> {
                            Logs.log(context, "cannot add it back to owned cards", 5);
                            Logs.log(context, e1.toString(), 6);
                            loadingDialog.dismiss();
                            gotoLineup();
                        });
                    });
                }).addOnFailureListener(e -> {
                    Logs.log(context, "cannot remove from owned card", 2);
                    Logs.log(context, e.toString(), 3);
                    loadingDialog.dismiss();
                    gotoLineup();
                });
            }
            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
                Logs.log(context, error.toString(), 7);
            }
        });
    }
    public void addPlayerInLineup(Card card, DatabaseReference userRef, DatabaseReference storeRef) {

        database.getReference("elmilad25").addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot allData) {
                
                DataSnapshot LineupData = allData.child("Users").child(name).child("Lineup");
                DataSnapshot storeData = allData.child("Store");

                card.setName(storeData.child(card.getID()).child("Name").getValue(String.class));
                for (DataSnapshot S : LineupData.getChildren()) {
                    if (S.getValue().toString().equals(card.getID())){
                        userRef.child("Lineup").child(S.getKey()).removeValue();
                        continue;
                    }
                    String Sname = storeData.child(S.getValue().toString()).child("Name").getValue(String.class);

                    if (card.getName().equals(Sname))
                        userRef.child("Lineup").child(S.getKey()).removeValue();
                }
                userRef.child("Lineup").child(cardPosition).setValue(card.getID());
                gotoLineup();
        }
            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {}
        });    userRef.child("Lineup").child(cardPosition).setValue(card.getID());
    }
}
