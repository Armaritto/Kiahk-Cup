package com.stgsporting.cup.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.stgsporting.cup.R;
import com.stgsporting.cup.helpers.ImageLoader;
import com.stgsporting.cup.helpers.ImageProcessor;
import com.stgsporting.cup.helpers.LoadingDialog;

public class CardIconEditorActivity extends AppCompatActivity {

    private DatabaseReference ref;
    private ImageView img;
    private String imgPath;
    private LoadingDialog loadingDialog;
    private FirebaseStorage storage;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_card_icon_editor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadingDialog = new LoadingDialog(this);
        imageLoader = new ImageLoader(this);

        img = findViewById(R.id.img);
        EditText name = findViewById(R.id.name);
        EditText price = findViewById(R.id.price);
        CheckBox available = findViewById(R.id.available);
        Button submit = findViewById(R.id.submit);

        String[] data = getIntent().getStringArrayExtra("Data");
        String cardID = getIntent().getStringExtra("SelectedCard");

        FirebaseDatabase database = FirebaseDatabase.getInstance(data[1]);
        storage = FirebaseStorage.getInstance(data[2]);
        ref = database.getReference("elmilad25").child("CardIcon").child(cardID);

        img.setOnClickListener(v-> openFileChooser());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot cardData) {
                if (cardData.hasChild("Image")) {
                    imgPath = cardData.child("Image").getValue().toString();
                    StorageReference storageRef = storage.getReference().child(imgPath);
                    storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String downloadUrl = uri.toString();
                                imageLoader.loadImage(downloadUrl, img);
                            })
                            .addOnFailureListener(e -> Toast.makeText(CardIconEditorActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show());
                }
                if (cardData.hasChild("Price"))
                    price.setText(cardData.child("Price").getValue().toString());
                if (cardData.hasChild("Available") && !Boolean.parseBoolean(cardData.child("Available").getValue().toString()))
                    available.setChecked(false);
                if (cardData.hasChild("Name"))
                    name.setText(cardData.child("Name").getValue().toString());

                loadingDialog.dismiss();
                submit.setOnClickListener(v-> {
                    try {
                        Integer.parseInt(price.getText().toString());
                    } catch (Exception e) {
                        price.setError("Please enter price (digits only)");
                        return;
                    }
                    if (imgPath==null) {
                        Toast.makeText(CardIconEditorActivity.this, "Please select card image", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ref.child("Price").setValue(Integer.parseInt(price.getText().toString()));
                    ref.child("Available").setValue(available.isChecked());
                    ref.child("Image").setValue(imgPath);
                    if (!name.getText().toString().equals(""))
                        ref.child("Name").setValue(name.getText().toString());
                    finish();
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss();
                Toast.makeText(CardIconEditorActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
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
        StorageReference fileRef = storageRef.child("CardIcons/").child(System.currentTimeMillis() + ".png");
        // Upload file to Firebase Storage
        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            // Get the download URL
                            Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                            imgPath = fileRef.getPath();
                            loadingDialog.dismiss();
                            imageLoader.loadImage(imageUri, img);
                            // Use the download URL as needed
                        }))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Upload failed\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                });
    }

}