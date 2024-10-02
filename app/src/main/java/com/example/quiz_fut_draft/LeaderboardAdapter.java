package com.example.quiz_fut_draft;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private final ArrayList<Lineup> lineups;
    private final LayoutInflater mInflater;
    private final Context context;
    private final FirebaseDatabase database;
    private final String ID;
    private final String grade;
    private final String Name;

    // data is passed into the constructor
    LeaderboardAdapter(Context context, ArrayList<Lineup> lineups,
                         FirebaseDatabase database, String ID, String grade, String Name) {
        this.mInflater = LayoutInflater.from(context);
        this.lineups = lineups;
        this.context = context;
        this.database = database;
        this.ID = ID;
        this.grade = grade;
        this.Name = Name;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_leaderboard_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.rank.setText(String.valueOf(i+1));
        holder.ovr.setText(lineups.get(i).getOVR());

        holder.img.setImageDrawable(lineups.get(i).getImage().getDrawable());

        holder.button.setOnClickListener(v-> {
            Intent intent;
            if(Objects.equals(lineups.get(i).getID(), ID))
                intent = new Intent(context, LineupActivity.class);
            else
                intent = new Intent(context, ViewOthersLineupActivity.class);
            intent.putExtra("ID", lineups.get(i).getID());
            intent.putExtra("Grade", grade);
            intent.putExtra("Name", Name);
            context.startActivity(intent);
        });
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return lineups.size();
    }

    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView rank;
        ImageView img;
        TextView ovr;
        Button button;
        ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            ovr = itemView.findViewById(R.id.ovr);
            button = itemView.findViewById(R.id.viewLineup);
            rank = itemView.findViewById(R.id.rank);
        }

        @Override
        public void onClick(View view) {
//            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Lineup getItem(int id) {
        return lineups.get(id);
    }
}