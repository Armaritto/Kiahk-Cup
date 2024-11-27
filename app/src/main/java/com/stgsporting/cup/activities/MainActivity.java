package com.stgsporting.cup.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stgsporting.cup.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stgsporting.cup.helpers.Header;
import com.stgsporting.cup.helpers.ImageProcessor;
import com.stgsporting.cup.helpers.LoadingDialog;
import com.stgsporting.cup.helpers.NetworkUtils;
import com.stgsporting.cup.helpers.UpdateDialog;


import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private String[] data;
    private static final double current_version = 1.0;
    private LoadingDialog loadingDialog;
    private FirebaseDatabase database;

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
        setContentView(R.layout.activity_main);
        loadingDialog = new LoadingDialog(this);

        data = getIntent().getStringArrayExtra("Data");
        Header.render(this, Objects.requireNonNull(data));

        database = FirebaseDatabase.getInstance(data[1]);
        Button mosab2a = findViewById(R.id.mosab2a);
        Button lineup = findViewById(R.id.lineup);
        Button myCard = findViewById(R.id.myCard);
        Button leaderboard = findViewById(R.id.leaderboard);
        Button admin = findViewById(R.id.admin);
        Button change_pic = findViewById(R.id.change_pic);
        Button logout = findViewById(R.id.logout);
        if(Objects.equals(data[0], "admin")){
            admin.setVisibility(View.VISIBLE);
            admin.setOnClickListener(v-> {
                if (!NetworkUtils.isOnline(this)) {
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                openAdminPanel();
            });
            leaderboard.setVisibility(View.VISIBLE);
            loadingDialog.dismiss();
        }
        database.getReference("elmilad25").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(current_version < dataSnapshot.child("Version").getValue(Double.class)){
                    View.OnClickListener updateListener = v1 -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.stgsporting.cup"));
                        startActivity(intent);
                    };
                    new UpdateDialog(MainActivity.this,updateListener);
                }
                if(Boolean.TRUE.equals(dataSnapshot.child("Maintenance").getValue(Boolean.class))){
                    loadingDialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, MaintenanceActivity.class);
                    startActivity(intent);
                    finish();
                }

                DataSnapshot snapshot = dataSnapshot.child("Buttons");
                if (!Objects.equals(data[0], "admin")) {
                    if (snapshot.hasChild("Leaderboard") &&
                            Boolean.parseBoolean(snapshot.child("Leaderboard").getValue().toString())) {
                        leaderboard.setVisibility(View.VISIBLE);
                    } else {
                        leaderboard.setVisibility(View.GONE);
                    }
                    if (snapshot.hasChild("Lineup") &&
                            Boolean.parseBoolean(snapshot.child("Lineup").getValue().toString())) {
                        lineup.setVisibility(View.VISIBLE);
                    } else {
                        lineup.setVisibility(View.GONE);
                    }
                    if (snapshot.hasChild("Mosab2a") &&
                            Boolean.parseBoolean(snapshot.child("Mosab2a").getValue().toString())) {
                        mosab2a.setVisibility(View.VISIBLE);
                    } else {
                        mosab2a.setVisibility(View.GONE);
                    }
                    if (snapshot.hasChild("My Card") &&
                            Boolean.parseBoolean(snapshot.child("My Card").getValue().toString())) {
                        myCard.setVisibility(View.VISIBLE);
                    } else {
                        myCard.setVisibility(View.GONE);
                    }
                    if (snapshot.hasChild("Change Picture") &&
                            Boolean.parseBoolean(snapshot.child("Change Picture").getValue().toString())) {
                        change_pic.setVisibility(View.VISIBLE);
                    } else {
                        change_pic.setVisibility(View.GONE);
                    }
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if(!Objects.equals(data[0], "admin")){
                    loadingDialog.dismiss();
                    Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });


        mosab2a.setOnClickListener(v-> {
            if (!NetworkUtils.isOnline(this)) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MainActivity.this, ShowQuizzesActivity.class);
            intent.putExtra("Data", data);
            startActivity(intent);
        });
        lineup.setOnClickListener(v-> {
            if (!NetworkUtils.isOnline(this)) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MainActivity.this, LineupActivity.class);
            intent.putExtra("Data", data);
            intent.putExtra("Other_Lineup", false);
            startActivity(intent);
        });
        myCard.setOnClickListener(v-> {
            if (!NetworkUtils.isOnline(this)) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MainActivity.this, MyCardActivity.class);
            intent.putExtra("Data", data);
            startActivity(intent);
        });
        leaderboard.setOnClickListener(v-> {
            if (!NetworkUtils.isOnline(this)) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MainActivity.this, LeaderboardActivity.class);
            intent.putExtra("Data", data);
            startActivity(intent);
        });
        change_pic.setOnClickListener(v-> openFileChooser());
        logout.setOnClickListener(v-> {
            Intent intent = new Intent(MainActivity.this, GradeActivity.class);
            SharedPreferences.Editor editor = getSharedPreferences("Login", MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();
            startActivity(intent);
            finish();
        });
    }

    public void openAdminPanel() {
        Intent intent = new Intent(MainActivity.this, AdminActivity.class);
        intent.putExtra("Data", data);
        startActivity(intent);
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
                            database.getReference("elmilad25").child("Users").child(data[0]).child("ImageLink").setValue(downloadlink);
                            loadingDialog.dismiss();
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

}