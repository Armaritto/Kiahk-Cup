package com.example.quiz_fut_draft;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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
    private final DataSnapshot userData;
    private final DataSnapshot snapshot;

    // data is passed into the constructor
    LeaderboardAdapter(Context context, ArrayList<Lineup> lineups,
                       FirebaseDatabase database, String ID, String grade, String Name, DataSnapshot userData, DataSnapshot snapshot) {
        this.mInflater = LayoutInflater.from(context);
        this.lineups = lineups;
        this.context = context;
        this.database = database;
        this.ID = ID;
        this.grade = grade;
        this.Name = Name;
        this.userData = userData;
        this.snapshot = snapshot;
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

//         holder.img.setImageDrawable(lineups.get(i).getImage().getDrawable());
        setUserCardImage(holder.cardImage, userData.child(lineups.get(i).getID()), snapshot);
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
        RelativeLayout cardImage;
        TextView ovr;
        Button button;

        ViewHolder(View itemView) {
            super(itemView);
            cardImage = itemView.findViewById(R.id.card_image);
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

    private void setUserCardImage(RelativeLayout parent, DataSnapshot userData, DataSnapshot allData) {
            RelativeLayout v = parent.findViewById(R.id.main);
            ImageView icon = parent.findViewById(R.id.card_icon);
            ImageView img = parent.findViewById(R.id.img);
            TextView name = parent.findViewById(R.id.name);
            TextView rating = parent.findViewById(R.id.card_rating);
            TextView position = parent.findViewById(R.id.position);

            imagesToLoad = 2;

            if (userData.child("Owned Card Icons").hasChild("Selected")) {
                String selected = userData.child("Owned Card Icons").child("Selected").getValue().toString();

                DataSnapshot cardRef = allData.child("elmilad25").child("CardIcon").child(selected);

                if (cardRef.hasChild("Link")) {
                    String cardIconLink = cardRef.child("Link").getValue().toString();
                    Picasso.get().load(cardIconLink).into(icon, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            imagesToLoad--;
                            TextColor.setColor(icon, name, position, rating);
                            checkIfAllImagesLoaded(v, parent);
                        }

                        @Override
                        public void onError(Exception e) {
                            imagesToLoad--;
                            checkIfAllImagesLoaded(v, parent);
                        }
                    });
                }

            } else {
                icon.setImageDrawable(context.getResources().getDrawable(R.drawable.empty));
                imagesToLoad--;
//                 checkIfAllImagesLoaded(v, imageView);
            }
            if (userData.hasChild("Pic")) {
                String imgLink = userData.child("Pic").getValue().toString();
                Picasso.get().load(imgLink).into(img, new Callback() {
                    @Override
                    public void onSuccess() {
                        imagesToLoad--;
                        checkIfAllImagesLoaded(v, parent);
                    }

                    @Override
                    public void onError(Exception e) {
                        imagesToLoad--;
                        checkIfAllImagesLoaded(v, parent);
                    }
                });
            } else {
                imagesToLoad--;
                checkIfAllImagesLoaded(v, parent);
            }
            name.setText(userData.child("Name").getValue().toString());
            position.setText(userData.child("Card").child("Position").getValue().toString());
    //        position.setText(userPos);
            if (userData.child("Card").hasChild("Rating")) {
                rating.setText(userData.child("Card").child("Rating").getValue().toString());
            }
        }

        private int imagesToLoad = 2;

        private void checkIfAllImagesLoaded(View v, RelativeLayout layout) {
            if (imagesToLoad==0) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {

                    ScaleAnimation scaleAnimation = new ScaleAnimation(
                            1f, 0.5f, // Start and end scale for X
                            1f, 0.5f, // Start and end scale for Y
                            Animation.RELATIVE_TO_SELF, 0.5f, // Pivot X
                            Animation.RELATIVE_TO_SELF, 0.5f  // Pivot Y
                    );
                    scaleAnimation.setDuration(2); // Duration of animation
                    scaleAnimation.setFillAfter(true); // Keep the scale after animation
                    layout.startAnimation(scaleAnimation);

//                     RelativeLayout layout = v.findViewById(R.id.main);
//
//                     layout.measure(View.MeasureSpec.makeMeasureSpec(
//                                     layout.getWidth(), View.MeasureSpec.EXACTLY),
//                             View.MeasureSpec.makeMeasureSpec(
//                                     layout.getHeight(), View.MeasureSpec.EXACTLY));
//
//                     layout.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
//
//                     int totalHeight = v.getMeasuredHeight();
//                     int totalWidth  = v.getMeasuredWidth();
//
//                     Bitmap bitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
//                     Canvas canvas = new Canvas(bitmap);
//                     v.draw(canvas);
//                     imageView.setImageBitmap(bitmap);
//                     imageView.setScaleX(1.05F);
                }, 100);
            }
        }

}