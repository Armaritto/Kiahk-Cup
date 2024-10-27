package com.stgsporting.quiz_fut.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.stgsporting.quiz_fut.R;
import com.stgsporting.quiz_fut.activities.LineupActivity;
import com.stgsporting.quiz_fut.data.TextColor;
import com.stgsporting.quiz_fut.data.User;

import java.util.List;

public class LeaderboardUserAdapter extends RecyclerView.Adapter<LeaderboardUserAdapter.ViewHolder> {

    private final LayoutInflater mInflater;
    private final Context context;
    private final List<User> users;
    private final String[] data;

    public LeaderboardUserAdapter(Context context, List<User> users, String[] data) {
        this.mInflater = LayoutInflater.from(context);
        this.users = users;
        this.context = context;
        this.data = data;
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
        holder.ovr.setText(String.format("%s", user.getPoints()));

        setUserCardImage(holder.cardView, holder.cardImage, user);
        holder.button.setOnClickListener(v -> {
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
        ImageView cardImage;
        TextView ovr;
        Button button;
        RelativeLayout row;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            cardImage = itemView.findViewById(R.id.card_image);
            ovr = itemView.findViewById(R.id.ovr);
            button = itemView.findViewById(R.id.viewLineup);
            rank = itemView.findViewById(R.id.rank);
            row = itemView.findViewById(R.id.row);
        }
    }

    private void setUserCardImage(RelativeLayout parent, ImageView cardImage, User user) {
            ImageView icon = parent.findViewById(R.id.card_icon);
            ImageView img = parent.findViewById(R.id.img);
            TextView name = parent.findViewById(R.id.name);
            TextView rating = parent.findViewById(R.id.card_rating);
            TextView position = parent.findViewById(R.id.position);


            if (user.hasCardIcon()) {

                if (user.getCardIcon() != null) {
                    String cardIconPath = user.getCardIcon();

                    FirebaseStorage database = FirebaseStorage.getInstance(data[2]);
                    StorageReference storageRef = database.getReference().child(cardIconPath);

                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Picasso.get().load(uri).into(icon);
                    });
                }

            } else {
                icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.empty));
            }

            if (user.getImageLink() != null && ! user.getImageLink().isEmpty()) {
                Picasso.get().load(user.getImageLink()).into(img);
            }

            name.setText(user.getDisplayName());

            if (user.getCard().getPosition() != null)
                position.setText(user.getCard().getPosition());

            if (user.getCard().getRating() != null)
                rating.setText(user.getCard().getRating());
        }


}