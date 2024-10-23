package com.stgsporting.quiz_fut.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz_fut_draft.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.stgsporting.quiz_fut.data.Card;
import com.stgsporting.quiz_fut.helpers.LoadingDialog;

import java.util.ArrayList;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {

    private final ArrayList<Card> cards;
    private final LayoutInflater mInflater;
    private final Context context;
    private int points;
    private final FirebaseDatabase database;
    private final String ID;
    private final String cardPosition;
    private LoadingDialog loadingDialog;
    private int imgs;

    // data is passed into the constructor
    public StoreAdapter(Context context, ArrayList<Card> cards, int points, FirebaseDatabase database,
                        String ID, String cardPosition, LoadingDialog loadingDialog) {
        this.mInflater = LayoutInflater.from(context);
        this.cards = cards;
        this.context = context;
        this.points = points;
        this.database = database;
        this.ID = ID;
        this.cardPosition = cardPosition;
        this.loadingDialog = loadingDialog;
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
        loadingDialog.show();
        imgs++;
        holder.price.setText(cards.get(position).getPrice()+"$");
        Picasso.get().load(cards.get(position).getImageLink()).into(holder.img, new Callback() {
            @Override
            public void onSuccess() {
                imgs--;
                checkAllImgsLoaded();
            }

            @Override
            public void onError(Exception e) {
                imgs--;
                Toast.makeText(context, "Image Loading Failed", Toast.LENGTH_SHORT).show();
                checkAllImgsLoaded();
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
            purchasePlayer(getItem(position));
        });
        holder.sellButton.setOnClickListener(v -> {
            sellPlayer(getItem(position));
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

        DatabaseReference userRef = database.getReference("/elmilad25/Users").child(ID);
        DatabaseReference cardRef = userRef.child("Owned Cards").child(card.getID());

        if (card.isOwned()) {
            userRef.child("Lineup").child(cardPosition).setValue(card.getID());
            ((Activity) context).finish();
        } else {
            int price = card.getPrice();
            if (points < price) {
                Toast.makeText(context, "Not enough coins", Toast.LENGTH_SHORT).show();
            } else {
                points -= price;
                userRef.child("Coins").setValue(points);
                cardRef.setValue(true);
                userRef.child("Lineup").child(cardPosition).setValue(card.getID());
                ((Activity) context).finish();
            }
        }

    }

    public void sellPlayer(Card card) {

        DatabaseReference userRef = database.getReference("/elmilad25/Users").child(ID);
        DatabaseReference cardRef = userRef.child("Owned Cards").child(card.getID());

        int price = card.getPrice();
        points += price/2;
        userRef.child("Coins").setValue(points);
        cardRef.removeValue();
        if(userRef.child("Lineup").child(cardPosition).toString().equals(card.getID()))
            userRef.child("Lineup").child(cardPosition).removeValue();
        card.setOwned(false);
        ((Activity) context).finish();

    }

    private void checkAllImgsLoaded() {
        if (imgs==0) loadingDialog.dismiss();
    }

}
