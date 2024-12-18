package com.stgsporting.cup.adapters;

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

import com.stgsporting.cup.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stgsporting.cup.data.CardIcon;
import com.stgsporting.cup.helpers.ConfirmDialog;
import com.stgsporting.cup.helpers.ImageLoader;
import com.stgsporting.cup.helpers.ItemClickListener;
import com.stgsporting.cup.helpers.NetworkUtils;

import java.util.ArrayList;

public class CardStoreAdapter extends RecyclerView.Adapter<CardStoreAdapter.ViewHolder> {

    private final ArrayList<CardIcon> cards;
    private final LayoutInflater mInflater;
    private final Context context;
    private final FirebaseDatabase database;
    private final String ID;
    private ImageLoader imageLoader;

    // data is passed into the constructor
    public CardStoreAdapter(Context context, ArrayList<CardIcon> cards,
                     FirebaseDatabase database, String ID, ImageLoader imageLoader) {
        this.mInflater = LayoutInflater.from(context);
        this.cards = cards;
        this.context = context;
        this.database = database;
        this.ID = ID;
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
        holder.price.setText(String.format("%.0f", cards.get(position).getPrice())+" ★");
        imageLoader.loadImage(cards.get(position).getImageLink(), holder.img);

        holder.button.setOnClickListener(v-> {
            View.OnClickListener listener = view -> {
                if (!NetworkUtils.isOnline(context)) {
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                purchaseObject(getItem(position));
            };
            new ConfirmDialog(context, listener);
        });
        if (cards.get(position).isOwned()) {
            holder.button.setText("Select");
        } else
            holder.button.setText("Purchase");
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
        Button button;

        ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            price = itemView.findViewById(R.id.price);
            button = itemView.findViewById(R.id.purchase);
        }

        @Override
        public void onClick(View view) {
//            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    CardIcon getItem(int id) {
        return cards.get(id);
    }

    public void purchaseObject(CardIcon card) {

        DatabaseReference ref = database.getReference("/elmilad25/Users").child(ID);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (card.isOwned()) {
                    ref.child("Owned Card Icons").child("Selected").setValue(card.getCard());
                    ((Activity) context).finish();
                } else {
                    double stars = Double.parseDouble(snapshot.child("Stars").getValue().toString());
                    if (stars>=card.getPrice()) {
                        stars -= card.getPrice();
                        ref.child("Stars").setValue(stars);
                        ref.child("Owned Card Icons").child(card.getCard()).child("Owned").setValue(true);
//                    ref.child("Card").child("CardIcon").setValue(card.getLink());
//                    ref.child("Card").child("Type").setValue(card.getCard());
                        ref.child("Owned Card Icons").child("Selected").setValue(card.getCard());
                        ((Activity) context).finish();
                    } else {
                        Toast.makeText(context, "Not enough stars", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}