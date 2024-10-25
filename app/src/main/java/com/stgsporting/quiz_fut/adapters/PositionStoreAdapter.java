package com.stgsporting.quiz_fut.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stgsporting.quiz_fut.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stgsporting.quiz_fut.data.Position;

import java.util.ArrayList;

public class PositionStoreAdapter extends RecyclerView.Adapter<PositionStoreAdapter.ViewHolder> {

    private final ArrayList<Position> positions;
    private final LayoutInflater mInflater;
    private final Context context;
    private final FirebaseDatabase database;
    private final String ID;

    // data is passed into the constructor
    public PositionStoreAdapter(Context context, ArrayList<Position> positions,
                     FirebaseDatabase database, String ID) {
        this.mInflater = LayoutInflater.from(context);
        this.positions = positions;
        this.context = context;
        this.database = database;
        this.ID = ID;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_store_item_position, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.price.setText(positions.get(i).getPrice()+" ★");
        holder.position.setText(positions.get(i).getPosition());
        holder.button.setOnClickListener(v-> {
            purchaseObject(getItem(i));
        });
        if (positions.get(i).isOwned()) {
            holder.button.setText("Select");
        }
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return positions.size();
    }

    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView position;
        TextView price;
        Button button;
        ViewHolder(View itemView) {
            super(itemView);
            position = itemView.findViewById(R.id.position);
            price = itemView.findViewById(R.id.price);
            button = itemView.findViewById(R.id.purchase);
        }

        @Override
        public void onClick(View view) {
//            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Position getItem(int id) {
        return positions.get(id);
    }

    public void purchaseObject(Position position) {

        DatabaseReference ref = database.getReference("/elmilad25/Users").child(ID);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (position.isOwned()) {
                    ref.child("Card").child("Position").setValue(position.getPosition());
                    ((Activity) context).finish();
                } else {
                    double stars = Double.parseDouble(snapshot.child("Stars").getValue().toString());
                    if (stars>=position.getPrice()) {
                        stars -= position.getPrice();
                        ref.child("Stars").setValue(stars);
                        ref.child("Owned Positions").child(position.getId()).child("Owned").setValue(true);
                    ref.child("Card").child("Position").setValue(position.getPosition());
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