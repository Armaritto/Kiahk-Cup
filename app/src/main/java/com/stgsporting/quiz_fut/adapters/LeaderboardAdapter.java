package com.stgsporting.quiz_fut.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
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

import com.example.quiz_fut_draft.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.stgsporting.quiz_fut.activities.LineupActivity;
import com.stgsporting.quiz_fut.data.Lineup;
import com.stgsporting.quiz_fut.data.TextColor;
import com.stgsporting.quiz_fut.helpers.LoadingDialog;

import java.util.ArrayList;
import java.util.Objects;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private final ArrayList<Lineup> lineups;
    private final LayoutInflater mInflater;
    private final Context context;
    private final String[] data;
    private final DataSnapshot userData;
    private final DataSnapshot snapshot;
    private final int[] imagesToLoad;
    private FirebaseStorage storage;
    private LoadingDialog loadingDialog;

    // data is passed into the constructor
    public LeaderboardAdapter(Context context, ArrayList<Lineup> lineups, String[] data,
                       DataSnapshot userData, DataSnapshot snapshot, FirebaseStorage storage, LoadingDialog loadingDialog) {
        this.mInflater = LayoutInflater.from(context);
        this.lineups = lineups;
        this.context = context;
        this.userData = userData;
        this.data = data;
        this.snapshot = snapshot;
        this.imagesToLoad = new int[lineups.size()];
        this.storage = storage;
        this.loadingDialog = loadingDialog;
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
        setUserCardImage(holder.cardView, holder.cardImage, userData.child(lineups.get(i).getID()), snapshot, i, holder.row, storage);
        holder.button.setOnClickListener(v-> {
            Intent intent;
            boolean OtherLineup;
            intent = new Intent(context, LineupActivity.class);
            if(Objects.equals(lineups.get(i).getID(), data[0]))
                OtherLineup = false;
            else
                OtherLineup = true;
            data[0] = lineups.get(i).getID();
            intent.putExtra("Data", data);
            intent.putExtra("OtherLineup", OtherLineup);
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

        @Override
        public void onClick(View view) {
//            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Lineup getItem(int id) {
        return lineups.get(id);
    }

    private void setUserCardImage(RelativeLayout parent, ImageView cardImage, DataSnapshot userData,
                                  DataSnapshot allData, int i, RelativeLayout row, FirebaseStorage storage) {
            ImageView icon = parent.findViewById(R.id.card_icon);
            ImageView img = parent.findViewById(R.id.img);
            TextView name = parent.findViewById(R.id.name);
            TextView rating = parent.findViewById(R.id.card_rating);
            TextView position = parent.findViewById(R.id.position);

            imagesToLoad[i] = 2;

            if (userData.child("Owned Card Icons").hasChild("Selected")) {
                String selected = userData.child("Owned Card Icons").child("Selected").getValue().toString();

                DataSnapshot cardRef = allData.child("elmilad25").child("CardIcon").child(selected);

                if (cardRef.hasChild("Image")) {
                    String cardIconPath = cardRef.child("Image").getValue().toString();

                    StorageReference storageRef = storage.getReference().child(cardIconPath);
                    storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String downloadUrl = uri.toString();
                                Picasso.get().load(downloadUrl).into(icon, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        imagesToLoad[i]--;
                                        TextColor.setColor(icon, name, position, rating);
                                        checkIfAllImagesLoaded(parent, cardImage, imagesToLoad[i], row, i);
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        imagesToLoad[i]--;
                                        checkIfAllImagesLoaded(parent, cardImage, imagesToLoad[i], row, i);
                                    }
                                });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context,
                                        "Failed to get download URL", Toast.LENGTH_SHORT).show();
                                imagesToLoad[i]--;
                            });
                }

            } else {
                icon.setImageDrawable(context.getResources().getDrawable(R.drawable.empty));
                imagesToLoad[i]--;
                 checkIfAllImagesLoaded(parent, cardImage, imagesToLoad[i], row, i);
            }
            if (userData.hasChild("ImageLink")) {
                String imgLink = userData.child("ImageLink").getValue().toString();
                Picasso.get().load(imgLink).into(img, new Callback() {
                    @Override
                    public void onSuccess() {
                        imagesToLoad[i]--;
                        checkIfAllImagesLoaded(parent, cardImage, imagesToLoad[i], row, i);
                    }

                    @Override
                    public void onError(Exception e) {
                        imagesToLoad[i]--;
                        checkIfAllImagesLoaded(parent, cardImage, imagesToLoad[i], row, i);
                    }
                });
            } else {
                imagesToLoad[i]--;
                checkIfAllImagesLoaded(parent, cardImage, imagesToLoad[i], row, i);
            }
            name.setText(userData.getKey());
            if (userData.child("Card").hasChild("Position"))
                position.setText(userData.child("Card").child("Position").getValue().toString());
    //        position.setText(userPos);
            if (userData.child("Card").hasChild("Rating"))
                rating.setText(userData.child("Card").child("Rating").getValue().toString());
        }

        private void checkIfAllImagesLoaded(RelativeLayout parent, ImageView cardImage, int imagesToLoad, RelativeLayout row, int i) {
            if (imagesToLoad==0) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {

                     parent.measure(View.MeasureSpec.makeMeasureSpec(
                                     parent.getWidth(), View.MeasureSpec.EXACTLY),
                             View.MeasureSpec.makeMeasureSpec(
                                     parent.getHeight(), View.MeasureSpec.EXACTLY));

                     parent.setVisibility(View.GONE);
                     parent.layout(0, 0, parent.getMeasuredWidth(), parent.getMeasuredHeight());

                     int totalHeight = parent.getMeasuredHeight();
                     int totalWidth  = parent.getMeasuredWidth();

                     Bitmap bitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
                     Canvas canvas = new Canvas(bitmap);
                    parent.draw(canvas);
                    cardImage.setImageBitmap(bitmap);
                    row.setVisibility(View.VISIBLE);

                    if (i==lineups.size()-1) loadingDialog.dismiss();

                }, 200);
            }
        }

}