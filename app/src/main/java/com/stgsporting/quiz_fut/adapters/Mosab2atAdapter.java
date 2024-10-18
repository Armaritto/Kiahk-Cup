package com.stgsporting.quiz_fut.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.quiz_fut_draft.R;
import com.stgsporting.quiz_fut.data.Mosab2a;

import java.util.ArrayList;

public class Mosab2atAdapter extends RecyclerView.Adapter<Mosab2atAdapter.ViewHolder> {

    private ArrayList<Mosab2a> mosab2at;
    private LayoutInflater mInflater;
    private Context context;

    // data is passed into the constructor
    public Mosab2atAdapter(Context context, ArrayList<Mosab2a> mosab2at) {
        this.mInflater = LayoutInflater.from(context);
        this.mosab2at = mosab2at;
        this.context = context;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.mosab2a_listitem, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(mosab2at.get(position).getTitle());
        holder.coins.setText(mosab2at.get(position).getCoins()+" Coins");
        holder.mosab2a.setOnClickListener(v-> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(mosab2at.get(position).getLink()));
            context.startActivity(intent);
        });
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mosab2at.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        TextView coins;
        RelativeLayout mosab2a;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            coins = itemView.findViewById(R.id.coins);
            mosab2a = itemView.findViewById(R.id.mosab2a);
        }

        @Override
        public void onClick(View view) {
//            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Mosab2a getItem(int id) {
        return mosab2at.get(id);
    }

}
