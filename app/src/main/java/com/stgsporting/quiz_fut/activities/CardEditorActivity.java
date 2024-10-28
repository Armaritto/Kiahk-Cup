package com.stgsporting.quiz_fut.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.stgsporting.quiz_fut.R;
import com.stgsporting.quiz_fut.helpers.ImageProcessor;
import com.stgsporting.quiz_fut.helpers.LoadingDialog;

public class CardEditorActivity extends AppCompatActivity {

    private DatabaseReference ref;
    private ImageView img;
    private String imgPath;
    private LoadingDialog loadingDialog;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_card_editor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadingDialog = new LoadingDialog(this);

        String[] positions = {
                "ST",
                "LW",
                "RW",
                "CAM",
                "CM",
                "LB",
                "CB",
                "RB",
                "GK"
        };

        img = findViewById(R.id.img);
        EditText name = findViewById(R.id.name);
        AutoCompleteTextView position = findViewById(R.id.position);
        EditText price = findViewById(R.id.price);
        EditText rating = findViewById(R.id.rating);
        Button submit = findViewById(R.id.submit);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, positions);
        position.setThreshold(1);
        position.setAdapter(adapter);

        String[] data = getIntent().getStringArrayExtra("Data");
        String cardID = getIntent().getStringExtra("SelectedCard");

        FirebaseDatabase database = FirebaseDatabase.getInstance(data[1]);
        storage = FirebaseStorage.getInstance(data[2]);
        ref = database.getReference("elmilad25").child("Store").child(cardID);

        img.setOnClickListener(v-> openFileChooser());
        
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot cardData) {
                if (cardData.hasChild("Image")) {
                    imgPath = cardData.child("Image").getValue().toString();
                    StorageReference storageRef = storage.getReference().child(imgPath);
                    storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String downloadUrl = uri.toString();
                                Picasso.get().load(downloadUrl).into(img, new Callback() {
                                    @Override
                                    public void onSuccess() {}

                                    @Override
                                    public void onError(Exception e) {
                                        Toast.makeText(CardEditorActivity.this, "Picasso Error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            })
                            .addOnFailureListener(e -> Toast.makeText(CardEditorActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show());
                }
                if (cardData.hasChild("Position"))
                    position.setText(cardData.child("Position").getValue().toString());
                if (cardData.hasChild("Price"))
                    price.setText(cardData.child("Price").getValue().toString());
                if (cardData.hasChild("Rating"))
                    rating.setText(cardData.child("Rating").getValue().toString());
                if (cardData.hasChild("Name"))
                    name.setText(cardData.child("Name").getValue().toString());

                loadingDialog.dismiss();
                submit.setOnClickListener(v-> {
                    if (name.getText().toString().isEmpty()) {
                        name.setError("Please enter name");
                        return;
                    }
                    if (position.getText().toString().isEmpty()) {
                        position.setError("Please enter position");
                        return;
                    }
                    if (!ArrayUtils.contains(positions, position.getText().toString())) {
                        position.setError("Please enter a valid position");
                        return;
                    }
                    try {
                        Integer.parseInt(price.getText().toString());
                    } catch (Exception e) {
                        price.setError("Please enter price (digits only)");
                        return;
                    }
                    try {
                        Integer.parseInt(rating.getText().toString());
                    } catch (Exception e) {
                        rating.setError("Please enter rating (digits only)");
                        return;
                    }
                    if (imgPath==null) {
                        Toast.makeText(CardEditorActivity.this, "Please select card image", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ref.child("Name").setValue(name.getText().toString());
                    ref.child("Position").setValue(position.getText().toString());
                    ref.child("Price").setValue(Integer.parseInt(price.getText().toString()));
                    ref.child("Rating").setValue(Integer.parseInt(rating.getText().toString()));
                    ref.child("Image").setValue(imgPath);
                    finish();
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss();
                Toast.makeText(CardEditorActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

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
            uploadImage(processor.compressImage(imageUri));
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
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child("Cards/").child(System.currentTimeMillis() + ".png");
        // Upload file to Firebase Storage
        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            // Get the download URL
                            imgPath = fileRef.getPath();
                            Picasso.get().load(uri).into(img, new Callback() {
                                @Override
                                public void onSuccess() {
                                    loadingDialog.dismiss();
                                }

                                @Override
                                public void onError(Exception e) {
                                    Toast.makeText(CardEditorActivity.this, "Picasso Error", Toast.LENGTH_SHORT).show();
                                    loadingDialog.dismiss();
                                }
                            });
                            // Use the download URL as needed
                        }))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Upload failed\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                });
    }

}