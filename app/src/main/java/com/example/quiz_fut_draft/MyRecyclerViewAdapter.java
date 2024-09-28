package com.example.quiz_fut_draft;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
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

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Card> cards;
    private LayoutInflater mInflater;
    private Context context;
    private int points;
    private FirebaseDatabase database;
    private String team;
    private String cardStoreCheck;
    private String ID;
    private String grade;

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, ArrayList<Card> data, int points, FirebaseDatabase database, String team, String cardStoreCheck
            , String ID, String grade) {
        this.mInflater = LayoutInflater.from(context);
        this.cards = data;
        this.context = context;
        this.points = points;
        this.database = database;
        this.team = team;
        this.cardStoreCheck = cardStoreCheck;
        this.ID = ID;
        this.grade = grade;
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
        Drawable drawable = context.getResources().getDrawable(context.getResources()
                .getIdentifier(cards.get(position).getImage(), "drawable", context.getPackageName()));
        holder.img.setImageDrawable(drawable);
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
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
    Card getItem(int id) {
        return cards.get(id);
    }

    // allows clicks events to be caught
//    void setClickListener(ItemClickListener itemClickListener) {
//        this.mClickListener = itemClickListener;
//    }
//
//    // parent activity will implement this method to respond to click events
//    public interface ItemClickListener {
//        void onItemClick(View view, int position);
//    }

    public void purchaseObject(Card card) {

        DatabaseReference ref = database.getReference("/elmilad25/Store").child("Card "+card.getID());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int price = Integer.parseInt(snapshot.child("Price").getValue().toString());

                if (points >= price) {
                    points -= price;
                    database.getReference(Users_Path.getPath(grade)).child(ID).child("Coins").setValue(points);
                    ref.child("Owner").setValue(ID);
                    Toast.makeText(context, "Purchased " + card.getImage(), Toast.LENGTH_SHORT).show();
                    if(cardStoreCheck != null)
                        ((Activity) context).finish();
                } else {
                    Toast.makeText(context, "Not enough coins", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
