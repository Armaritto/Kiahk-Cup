package com.stgsporting.cup.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stgsporting.cup.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.stgsporting.cup.activities.LineupActivity;
import com.stgsporting.cup.data.Card;
import com.stgsporting.cup.helpers.ConfirmDialog;
import com.stgsporting.cup.helpers.NetworkUtils;

import java.util.ArrayList;
import java.util.Objects;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {

    private final ArrayList<Card> cards;
    private final LayoutInflater mInflater;
    private final Context context;
    private int points;
    private final FirebaseDatabase database;
    private final String name;
    private final String cardPosition;
    private FirebaseStorage storage;

    // data is passed into the constructor
    public StoreAdapter(Context context, ArrayList<Card> cards, int points, FirebaseDatabase database,
                        String name, String cardPosition, FirebaseStorage storage) {
        this.mInflater = LayoutInflater.from(context);
        this.cards = cards;
        this.context = context;
        this.points = points;
        this.database = database;
        this.name = name;
        this.cardPosition = cardPosition;
        this.storage = storage;
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
        Picasso.get().load(cards.get(position).getImageLink()).into(holder.img, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(context, "Image Loading Failed", Toast.LENGTH_SHORT).show();
            }
        });
        if (cards.get(position).isOwned()) {
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

        if (card.isOwned()) {
            userRef.child("Lineup").addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if (snapshot1.getValue().toString().equals(card.getID()))
                            userRef.child("Lineup").child(snapshot1.getKey()).removeValue();
                    }
                }
                @Override
                public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {}
            });
            userRef.child("Lineup").child(cardPosition).setValue(card.getID());
        }
        else {
            int price = card.getPrice();
            if (points < price) {
                Toast.makeText(context, "Not enough coins", Toast.LENGTH_SHORT).show();
            } else {
                points -= price;
                userRef.child("Coins").setValue(points);
                cardRef.setValue(true);
                userRef.child("Lineup").child(cardPosition).setValue(card.getID());
            }
        }
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

    public void sellPlayer(Card card) {

        DatabaseReference userRef = database.getReference("/elmilad25/Users").child(name);
        DatabaseReference cardRef = userRef.child("Owned Cards").child(card.getID());

        int price = card.getPrice();
        points += price/2;
        userRef.child("Coins").setValue(points);
        cardRef.removeValue();
        userRef.child("Lineup").child(cardPosition).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                if(Objects.requireNonNull(snapshot.getValue()).toString().equals(card.getID()))
                    userRef.child("Lineup").child(cardPosition).removeValue();
            }
            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {}
        });
        card.setOwned(false);
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

}
