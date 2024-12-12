package com.stgsporting.cup.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.stgsporting.cup.R;
import com.stgsporting.cup.activities.LineupActivity;
import com.stgsporting.cup.data.TextColor;
import com.stgsporting.cup.data.User;
import com.stgsporting.cup.helpers.ImageLoader;
import com.stgsporting.cup.helpers.NetworkUtils;

import java.util.List;

public class LeaderboardUserAdapter extends RecyclerView.Adapter<LeaderboardUserAdapter.ViewHolder> {

    private final LayoutInflater mInflater;
    private final Context context;
    private final List<User> users;
    private final String[] data;
    private final ImageLoader imageLoader;

    public LeaderboardUserAdapter(Context context, List<User> users, String[] data, ImageLoader imageLoader) {
        this.mInflater = LayoutInflater.from(context);
        this.users = users;
        this.context = context;
        this.data = data;
        this.imageLoader = imageLoader;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_leaderboard_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.rank.setText(String.valueOf(i+1));
        User user = users.get(i);
        holder.ovr.setText(String.format("%s", (int) Math.round(user.getPoints())));

        setUserCardImage(holder.cardView, user);
        holder.button.setOnClickListener(v -> {
            if (!NetworkUtils.isOnline(context)) {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent;
            intent = new Intent(context, LineupActivity.class);
            intent.putExtra("Data", new String[]{user.getName(), data[1], data[2], data[3]});
            intent.putExtra("OtherLineup", ! user.isCurrent());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rank;
        RelativeLayout cardView;
        TextView ovr;
        Button button;
        RelativeLayout row;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            ovr = itemView.findViewById(R.id.ovr);
            button = itemView.findViewById(R.id.viewLineup);
            rank = itemView.findViewById(R.id.rank);
            row = itemView.findViewById(R.id.row);
        }
    }

    private void setUserCardImage(RelativeLayout parent, User user) {
            ImageView icon = parent.findViewById(R.id.card_icon);
            ImageView img = parent.findViewById(R.id.img);
            TextView name = parent.findViewById(R.id.name);
            TextView rating = parent.findViewById(R.id.card_rating);
            TextView position = parent.findViewById(R.id.position);

            Callback onlineCallback = new Callback() {
                public void onSuccess() {TextColor.setColor(icon, name, rating, position);}
                public void onError() {Toast.makeText(context, "Could not fetch image", Toast.LENGTH_SHORT).show();}
            };

            Callback offlineCallback = new Callback() {
                public void onSuccess() {TextColor.setColor(icon, name, rating, position);}
                public void onError() {imageLoader.loadImageOnline(user.getCardIcon(), icon, onlineCallback);}
            };

            imageLoader.loadImageOffline(user.getCardIcon(), icon, offlineCallback);
            imageLoader.loadImage(user.getImageLink(), img);

            name.setText(user.getFirstName());
            position.setText(user.getCard().getPosition());
            rating.setText(user.getCard().getRating());
    }


}