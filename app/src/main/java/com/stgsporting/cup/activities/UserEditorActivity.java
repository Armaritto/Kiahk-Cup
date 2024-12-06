package com.stgsporting.cup.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.stgsporting.cup.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.stgsporting.cup.data.Option;
import com.stgsporting.cup.helpers.ConfirmDialog;
import com.stgsporting.cup.helpers.ImageLoader;
import com.stgsporting.cup.helpers.ImageProcessor;
import com.stgsporting.cup.helpers.LoadingDialog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class UserEditorActivity extends AppCompatActivity {

    private String[] data;
    private String userName;
    private DatabaseReference ref;
    private ImageView img;
    private LoadingDialog loadingDialog;
    private ImageLoader imageLoader;
    private TextView name;
    private TextView rating;
    private ListView list;
    private LinearLayout add;
    private EditText passcode_edittext;
    private EditText coins_edittext;
    private EditText stars_edittext;
    private TextView cards_t, t_coins;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Handle the result here
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        Uri imageUri = data.getData();
                        if (imageUri == null) return;

                        loadingDialog.show();
                        ImageProcessor processor = new ImageProcessor(this);
                        imageUri = processor.compressImage(imageUri);

                        processor.removeBackground(imageUri).thenApply(
                                image -> {
                                    runOnUiThread(() -> uploadImage(image));
                                    return null;
                                }
                        );
                    }
                }
            });

    private final ActivityResultLauncher<String[]> requestPermissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                    Boolean isGranted = entry.getValue();
                    if (isGranted) {
                        System.out.println("Permission granted");

                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        pickImageLauncher.launch(intent);
                    } else {
                        Toast.makeText(this, "You have to enable storage permissions", Toast.LENGTH_SHORT).show();
                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_editor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadingDialog = new LoadingDialog(this);
        imageLoader = new ImageLoader(this);

        name = findViewById(R.id.name);
        rating = findViewById(R.id.rating);
        passcode_edittext = findViewById(R.id.passcode_edittext);
        coins_edittext = findViewById(R.id.coins_edittext);
        stars_edittext = findViewById(R.id.stars_edittext);
        ImageView conf_passcode = findViewById(R.id.conf_passcode);
        ImageView conf_coins = findViewById(R.id.conf_coins);
        ImageView conf_stars = findViewById(R.id.conf_stars);
        img = findViewById(R.id.img);
        list = findViewById(R.id.list);
        add = findViewById(R.id.add);
        cards_t = findViewById(R.id.cards_t);
        t_coins = findViewById(R.id.t_coins);

        data = getIntent().getStringArrayExtra("Data");
        userName = getIntent().getStringExtra("SelectedUser");

        FirebaseDatabase database = FirebaseDatabase.getInstance(data[1]);
        ref = database.getReference("elmilad25");

        refreshData();

        img.setOnClickListener(v-> openFileChooser());

        conf_passcode.setOnClickListener(v-> {
            if (passcode_edittext.getText().length()<4) {
                passcode_edittext.setError("Passcode must be 4 digits");
                return;
            }
            ref.child("Users").child(userName).child("Passcode").setValue(passcode_edittext.getText().toString());
            Toast.makeText(this, "Passcode updated successfully", Toast.LENGTH_SHORT).show();
            refreshData();
        });

        conf_coins.setOnClickListener(v-> {
            if (coins_edittext.getText().toString().equals("")) {
                coins_edittext.setError("Coins must be 4 digits");
                return;
            }
            ref.child("Users").child(userName).child("Coins").setValue(Integer.parseInt(coins_edittext.getText().toString()));
            Toast.makeText(this, "Coins updated successfully", Toast.LENGTH_SHORT).show();
            refreshData();
        });

        conf_stars.setOnClickListener(v-> {
            if (stars_edittext.getText().toString().equals("")) {
                return;
            }
            ref.child("Users").child(userName).child("Stars").setValue(Integer.parseInt(stars_edittext.getText().toString()));
            Toast.makeText(this, "Stars updated successfully", Toast.LENGTH_SHORT).show();
            refreshData();
        });

    }

    private void refreshData() {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                DataSnapshot snapshot = data.child("Users").child(userName);

                String new_name = snapshot.getKey();
                new_name = Arrays.stream(new_name.split("\\s+"))
                        .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                        .collect(Collectors.joining(" "));
                name.setText(new_name);
                if (snapshot.hasChild("Passcode"))
                    passcode_edittext.setText(snapshot.child("Passcode").getValue().toString());
                if (snapshot.hasChild("Points"))
                    rating.setText(snapshot.child("Points").getValue().toString());
                if (snapshot.hasChild("Coins"))
                    coins_edittext.setText(snapshot.child("Coins").getValue().toString());
                if (snapshot.hasChild("Stars"))
                    stars_edittext.setText(snapshot.child("Stars").getValue().toString());
                if (snapshot.hasChild("ImageLink")) {
                    String imgLink = snapshot.child("ImageLink").getValue().toString();
                    imageLoader.loadImage(imgLink, img);
                }

                ArrayList<String> cardsIDs = new ArrayList<>();

                for (DataSnapshot s : snapshot.child("Owned Cards").getChildren()) {
                    if (Boolean.parseBoolean(s.getValue().toString()))
                        cardsIDs.add(s.getKey());
                }

                int cardsPrice = 0;
                for (String cardID : cardsIDs) {
                    int price = Integer.parseInt(data.child("Store").child(cardID).child("Price").getValue().toString());
                    cardsPrice+=price;
                }

                int totalGainedCoins = Integer.parseInt(coins_edittext.getText().toString())+cardsPrice;
                cards_t.setText(String.valueOf(cardsPrice));
                t_coins.setText(String.valueOf(totalGainedCoins));
                loadingDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss();
                Toast.makeText(UserEditorActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> titles = new ArrayList<>();
                ArrayList<String> contents = new ArrayList<>();

                for (DataSnapshot s : snapshot.child("Users").child(userName).child("Mosab2at").getChildren()) {
                    String mosab2aKey = s.getKey();
                    String title = snapshot.child("Mosab2at").child(mosab2aKey).child("Title").getValue().toString();
                    String max = snapshot.child("Mosab2at").child(mosab2aKey).child("Points").getValue().toString();
                    String score = s.child("Score").getValue().toString();
                    titles.add(title);
                    contents.add(score+"/"+max);
                }

                for (DataSnapshot s : snapshot.child("Users").child(userName).child("Attendance").getChildren()) {
                    titles.add(s.getKey());
                    contents.add(s.getValue().toString());
                }

                list.setAdapter(new ListAdapter(titles, contents));
                setListViewHeightBasedOnItems(list);

                add.setOnClickListener(v-> {
                    showAddDialog(snapshot);
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserEditorActivity.this, "Error", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private ConfirmDialog confirmDialog;

    private class ListAdapter extends BaseAdapter {

        private final ArrayList<String> titles;
        private final ArrayList<String> contents;

        public ListAdapter(ArrayList<String> titles, ArrayList<String> contents) {
            this.titles = titles;
            this.contents = contents;
        }

        @Override
        public int getCount() {
            return titles.size();
        }

        @Override
        public String getItem(int position) {
            return titles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint({"InflateParams", "ViewHolder"})
            View v = getLayoutInflater().inflate(R.layout.user_editor_listitem, null);
            TextView title = v.findViewById(R.id.title);
            TextView content = v.findViewById(R.id.content);
            title.setText(titles.get(position));
            content.setText(contents.get(position));

            View.OnClickListener yesListener = view -> {
                loadingDialog.show();
                ref.child("Users").child(userName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("Stars")) {
                            int totalStars = Integer.parseInt(snapshot.child("Stars").getValue().toString());
                            int stars = Integer.parseInt(contents.get(position).replaceAll("[\\D]", ""));
                            totalStars-=stars;
                            ref.child("Users").child(userName).child("Stars").setValue(totalStars).addOnSuccessListener(unused -> ref.child("Users").child(userName).child("Attendance").child(titles.get(position)).removeValue((error, ref) -> {
                                Toast.makeText(UserEditorActivity.this, stars+" stars removed", Toast.LENGTH_SHORT).show();
                                refreshData();
                                loadingDialog.dismiss();
                                if (confirmDialog!=null) confirmDialog.dismiss();
                            })).addOnFailureListener(e -> {
                                Toast.makeText(UserEditorActivity.this, "Operation Failed", Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
                                if (confirmDialog!=null) confirmDialog.dismiss();
                            });
                        } else {
                            Toast.makeText(UserEditorActivity.this, "Operation Failed", Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();
                            if (confirmDialog!=null) confirmDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(UserEditorActivity.this, "Operation Failed", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                        confirmDialog.dismiss();
                    }
                });
            };

            v.setOnLongClickListener(view -> {
               confirmDialog = new ConfirmDialog(UserEditorActivity.this, yesListener);
                return true;
            });
            return v;
        }
    }

    private void openFileChooser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // For Android 14 (API 34) and above
            requestPermissionsLauncher.launch(new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            });
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 (API 33)
            requestPermissionsLauncher.launch(new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
            });
        } else {
            // For Android 12 (API 32) and below
            requestPermissionsLauncher.launch(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            });
        }
    }

    private void uploadImage(Uri imageUri) {
        if (imageUri == null) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            loadingDialog.dismiss();
            return;
        }

        Toast.makeText(this, "Uploading Image", Toast.LENGTH_SHORT).show();
        // Create a reference to the Firebase Storage location
        FirebaseStorage storage = FirebaseStorage.getInstance(data[2]);
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child("Users/").child(System.currentTimeMillis() + ".png");

        // Upload file to Firebase Storage
        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            // Get the download URL
                            Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                            String downloadlink = uri.toString();
                            ref.child("Users").child(userName).child("ImageLink").setValue(downloadlink);
                            loadingDialog.dismiss();
                            imageLoader.loadImage(uri, img);
                        }))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Upload failed\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    loadingDialog.dismiss();
                }).addOnCompleteListener(task -> {
                    ImageProcessor processor = new ImageProcessor(this);
                    processor.deleteImage(imageUri);
                    loadingDialog.dismiss();
                });
    }

    private AlertDialog alertDialog;

    private void showAddDialog(DataSnapshot snapshot) {
        loadingDialog.show();
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.admin_dialog, null);

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Get the UI elements from the custom layout
        TextView name = dialogView.findViewById(R.id.name);
        name.setText(userName);
        ListView options = dialogView.findViewById(R.id.options);

        ArrayList<Option> optionsList = new ArrayList<>();

        DataSnapshot optionsData = snapshot.child("Admin");
        for (DataSnapshot optionData : optionsData.getChildren()) {
            Option o = new Option();
            o.setOptionName(optionData.getKey());
            o.setStars(Integer.parseInt(optionData.getValue().toString()));
            optionsList.add(o);
        }

        options.setAdapter(new DialogAdapter(snapshot, optionsList));

        // Show the dialog
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();
        loadingDialog.dismiss();
    }

    private class DialogAdapter extends BaseAdapter {

        private final DataSnapshot snapshot;
        private final ArrayList<Option> options;

        public DialogAdapter(DataSnapshot snapshot, ArrayList<Option> options) {
            this.snapshot = snapshot;
            this.options = options;
        }

        @Override
        public int getCount() {
            return options.size();
        }

        @Override
        public Option getItem(int position) {
            return options.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = getLayoutInflater().inflate(R.layout.admin_options_listitem, null);
            Button option = v.findViewById(R.id.option);
            String text = options.get(position).getOptionName()+" ("+options.get(position).getStars()+")";
            option.setText(text);
            option.setOnClickListener(v1-> {
                loadingDialog.show();
                int stars = Integer.parseInt(
                        snapshot.child("Users").child(userName).child("Stars").getValue().toString());
                stars+=options.get(position).getStars();
                ref.child("Users").child(userName).child("Stars").setValue(stars);
                DateTimeFormatter formatter;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = LocalDateTime.now().format(formatter);
                    ref.child("Users").child(userName).child("Attendance").child(formattedDate).setValue(text);
                } else
                    ref.child("Users").child(userName).child("Attendance").child(String.valueOf(System.currentTimeMillis())).setValue(text);
                Toast.makeText(UserEditorActivity.this, options.get(position).getStars()+" stars added",
                        Toast.LENGTH_SHORT).show();
                refreshData();
                loadingDialog.dismiss();
                alertDialog.dismiss();
            });
            return v;
        }
    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {
        ListAdapter listAdapter = (ListAdapter) listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                float px = 500 * (listView.getResources().getDisplayMetrics().density);
                item.measure(View.MeasureSpec.makeMeasureSpec((int)px, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);
            // Get padding
            int totalPadding = listView.getPaddingTop() + listView.getPaddingBottom();

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight + totalPadding;
            listView.setLayoutParams(params);
            listView.requestLayout();
            return true;

        } else {
            return false;
        }

    }

}