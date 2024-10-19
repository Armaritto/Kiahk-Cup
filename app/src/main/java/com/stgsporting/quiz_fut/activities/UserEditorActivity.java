package com.stgsporting.quiz_fut.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quiz_fut_draft.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.stgsporting.quiz_fut.data.Option;
import com.stgsporting.quiz_fut.helpers.ImageProcessor;
import com.stgsporting.quiz_fut.helpers.LoadingDialog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class UserEditorActivity extends AppCompatActivity {

    private String[] data;
    private String userName;
    private DatabaseReference ref;
    private ImageView img;

    private ProgressBar progressBar;
    private LoadingDialog loadingDialog;

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

        TextView name = findViewById(R.id.name);
        TextView rating = findViewById(R.id.rating);
        EditText passcode_edittext = findViewById(R.id.passcode_edittext);
        EditText coins_edittext = findViewById(R.id.coins_edittext);
        EditText stars_edittext = findViewById(R.id.stars_edittext);
        ImageView conf_passcode = findViewById(R.id.conf_passcode);
        ImageView conf_coins = findViewById(R.id.conf_coins);
        ImageView conf_stars = findViewById(R.id.conf_stars);
        img = findViewById(R.id.img);
        ListView list = findViewById(R.id.list);
        LinearLayout add = findViewById(R.id.add);

        data = getIntent().getStringArrayExtra("Data");
        userName = getIntent().getStringExtra("SelectedUser");

        FirebaseDatabase database = FirebaseDatabase.getInstance(data[1]);
        ref = database.getReference("elmilad25");

        ref.child("Users").child(userName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setText(snapshot.getKey());
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
                    Picasso.get().load(imgLink).into(img, new Callback() {
                        @Override
                        public void onSuccess() {
                            loadingDialog.dismiss();
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(UserEditorActivity.this, "Picasso Error", Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();
                        }
                    });
                } else {
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss();
                Toast.makeText(UserEditorActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        ref.addValueEventListener(new ValueEventListener() {
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

        img.setOnClickListener(v-> openFileChooser());

        conf_passcode.setOnClickListener(v-> {
            if (passcode_edittext.getText().length()<4) {
                passcode_edittext.setError("Passcode must be 4 digits");
                return;
            }
            ref.child("Users").child(userName).child("Passcode").setValue(passcode_edittext.getText().toString());
            Toast.makeText(this, "Passcode updated successfully", Toast.LENGTH_SHORT).show();
        });

        conf_coins.setOnClickListener(v-> {
            if (coins_edittext.getText().toString().equals("")) {
                coins_edittext.setError("Coins must be 4 digits");
                return;
            }
            ref.child("Users").child(userName).child("Coins").setValue(coins_edittext.getText().toString());
            Toast.makeText(this, "Coins updated successfully", Toast.LENGTH_SHORT).show();
        });

        conf_stars.setOnClickListener(v-> {
            if (stars_edittext.getText().toString().equals("")) {
                return;
            }
            ref.child("Users").child(userName).child("Stars").setValue(stars_edittext.getText().toString());
            Toast.makeText(this, "Stars updated successfully", Toast.LENGTH_SHORT).show();
        });

//        passcode.setOnClickListener(v-> {
//            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//            ClipData clip = ClipData.newPlainText("Passcode", passcode.getText().toString());
//            clipboard.setPrimaryClip(clip);
//        });

    }

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
            return v;
        }
    }

    // Inside your Activity or Fragment

    private static final int PICK_IMAGE_REQUEST = 1;

    // Method to open the gallery
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle the image selection result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
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
                            String downloadlink = uri.toString();
                            ref.child("Users").child(userName).child("ImageLink").setValue(downloadlink);
                            Picasso.get().load(uri).into(img, new Callback() {
                                @Override
                                public void onSuccess() {
                                    loadingDialog.dismiss();
                                }

                                @Override
                                public void onError(Exception e) {
                                    Toast.makeText(UserEditorActivity.this, "Picasso Error", Toast.LENGTH_SHORT).show();
                                    loadingDialog.dismiss();
                                }
                            });
                            // Use the download URL as needed
                        }))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Upload failed\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                })
                .addOnCompleteListener(task -> {
                    ImageProcessor processor = new ImageProcessor(this);
                    processor.deleteImage(imageUri);
                });
    }

    private android.app.AlertDialog alertDialog;

    private void showAddDialog(DataSnapshot snapshot) {
        loadingDialog.show();
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.admin_dialog, null);

        // Create the AlertDialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
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
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = LocalDateTime.now().format(formatter);
                    ref.child("Users").child(userName).child("Attendance").child(formattedDate).setValue(text);
                } else
                    ref.child("Users").child(userName).child("Attendance").child(String.valueOf(System.currentTimeMillis())).setValue(text);
                Toast.makeText(UserEditorActivity.this, options.get(position).getStars()+" stars added",
                        Toast.LENGTH_SHORT).show();
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