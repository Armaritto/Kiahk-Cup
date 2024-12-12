package com.stgsporting.cup.activities;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stgsporting.cup.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stgsporting.cup.adapters.LeaderboardUserAdapter;
import com.stgsporting.cup.data.Card;
import com.stgsporting.cup.data.User;
import com.stgsporting.cup.helpers.Header;
import com.stgsporting.cup.helpers.ImageLoader;
import com.stgsporting.cup.helpers.LoadingDialog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;


public class LeaderboardActivity extends AppCompatActivity {

    private String[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LoadingDialog loadingDialog = new LoadingDialog(this);

        data = getIntent().getStringArrayExtra("Data");
        Header.render(this, Objects.requireNonNull(data));

        FirebaseDatabase database = FirebaseDatabase.getInstance(data[1]);
        DatabaseReference ref = database.getReference();
        StorageReference storageRef = FirebaseStorage.getInstance(data[2]).getReference();

        RecyclerView lineupsView = findViewById(R.id.recycler_view_lineups);

        ref.child("/elmilad25").get().addOnSuccessListener(snapshot -> {
            Iterable<DataSnapshot> usersIterate = snapshot.child("/Users").getChildren();
            DataSnapshot cardSnapshot = snapshot.child("/Store");
            DataSnapshot cardIconsSnapshot = snapshot.child("/CardIcon");

            List<User> users = new ArrayList<>();

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            for (DataSnapshot user : usersIterate) {
                if (Objects.equals(user.getKey().toLowerCase(), "admin")) continue;

                User aUser = new User();
                aUser.setName(user.getKey());
                if (user.hasChild("Points")) {
                    aUser.setPoints(Double.parseDouble(user.child("Points").getValue().toString()));
                }

                if (aUser.getPoints()<=5) continue;

                if (user.hasChild("ImageLink")) {
                    aUser.setImageLink(user.child("ImageLink").getValue().toString());
                }

                if (Objects.equals(aUser.getName(), data[0])) {
                    aUser.setCurrent(true);
                }
                Card card = new Card();
                if (user.hasChild("Card")) {
                    card.setPosition(user.child("Card").child("Position").getValue().toString());
                    card.setRating(user.child("Card").child("Rating").getValue().toString());
                }
                aUser.setCard(card);

                if (user.hasChild("/Owned Card Icons/Selected")) {
                    String selectedCardIcon = user.child("/Owned Card Icons/Selected").getValue().toString();
                    if (cardIconsSnapshot.hasChild(selectedCardIcon)) {
                        String cardIconPath = cardIconsSnapshot.child(selectedCardIcon + "/Image").getValue().toString();
                        aUser.setCardIcon(cardIconPath);
                    }
                }

                Iterable<DataSnapshot> ownedCardsIterate = user.child("Owned Cards").getChildren();
                for (DataSnapshot ownedCardItem : ownedCardsIterate) {
                    Card ownedCard = new Card();
                    String cardKey = ownedCardItem.getKey();
                    DataSnapshot cardData = cardSnapshot.child(Objects.requireNonNull(cardKey));

                    if (cardData.hasChild("Position")) {
                        ownedCard.setPosition(cardData.child("Position").getValue().toString());
                    }

                    if (cardData.hasChild("Rating")) {
                        ownedCard.setRating(cardData.child("Rating").getValue().toString());
                    }

                    if (cardData.hasChild("Image")) {
                        ownedCard.setImagePath(cardData.child("Image").getValue().toString());
                    }
                    if (cardData.hasChild("Price")) {
                        ownedCard.setPrice(Integer.parseInt(cardData.child("Price").getValue().toString()));
                    }
                    aUser.addOwnedCard(ownedCard);
                }
                futures.add(CompletableFuture.runAsync(() -> {
                    if (aUser.hasCardIcon()) {
                        Task<Uri> task = storageRef.child(aUser.getCardIcon()).getDownloadUrl();

                        while (!task.isComplete()) {
                            try {
                                Thread.sleep(5);
                            } catch (InterruptedException ignored) {}
                        }
                        aUser.setCardIcon(task.getResult().toString());
                    }
                }));

                users.add(aUser);
            }

            users.sort(Comparator.comparingDouble(User::getPoints).reversed());

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            loadingDialog.dismiss();
            RecyclerView.Adapter<LeaderboardUserAdapter.ViewHolder> adapter = new LeaderboardUserAdapter(
                    this, users, data, new ImageLoader(LeaderboardActivity.this));
            lineupsView.setAdapter(adapter);

        });
    }
}