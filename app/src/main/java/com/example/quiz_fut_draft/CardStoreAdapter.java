package com.example.quiz_fut_draft;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CardStoreAdapter extends RecyclerView.Adapter<CardStoreAdapter.ViewHolder> {

    private final ArrayList<CardIcon> cards;
    private final LayoutInflater mInflater;
    private final Context context;
    private final FirebaseDatabase database;
    private final String ID;

    // data is passed into the constructor
    CardStoreAdapter(Context context, ArrayList<CardIcon> cards,
                     FirebaseDatabase database, String ID) {
        this.mInflater = LayoutInflater.from(context);
        this.cards = cards;
        this.context = context;
        this.database = database;
        this.ID = ID;
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
        holder.price.setText(cards.get(position).getPrice()+"$");
        Picasso.get().load(cards.get(position).getLink()).into(holder.img);
        holder.button.setOnClickListener(v-> {
            purchaseObject(getItem(position));
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

        DatabaseReference ref = database.getReference("Login").child(ID);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                double stars = Double.parseDouble(snapshot.child("Stars").getValue().toString());
                if (stars>=card.getPrice()) {
                    stars -= card.getPrice();
                    ref.child("Stars").setValue(stars);
                    ref.child("Card").child("Type").setValue(card.getCard());
                    ((Activity) context).finish();
                } else {
                    Toast.makeText(context, "Not enough stars", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
